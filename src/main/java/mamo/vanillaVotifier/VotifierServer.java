/*
 * Copyright (C) 2016  Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mamo.vanillaVotifier;

import mamo.vanillaVotifier.event.*;
import mamo.vanillaVotifier.utils.RsaUtils;
import mamo.vanillaVotifier.utils.SubstitutionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.crypto.BadPaddingException;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class VotifierServer {
	@NotNull protected VanillaVotifier votifier;
	@NotNull protected CopyOnWriteArrayList<Listener> listeners;
	protected boolean running;
	@Nullable protected ServerSocket serverSocket;

	public VotifierServer(@NotNull VanillaVotifier votifier) {
		this.votifier = votifier;
		listeners = new CopyOnWriteArrayList<Listener>();
		listeners.add(new VotifierServerListener(votifier));
	}

	public synchronized void start() throws IOException {
		if (isRunning()) {
			throw new IllegalStateException("Server is already running!");
		}
		notifyListeners(new ServerStartingEvent());
		serverSocket = new ServerSocket();
		serverSocket.bind(votifier.getConfig().getInetSocketAddress());
		running = true;
		notifyListeners(new ServerStartedEvent());
		new Thread(new Runnable() {
			@Override
			public void run() {
				ExecutorService executorService = Executors.newSingleThreadExecutor();
				while (isRunning()) {
					try {
						final Socket socket = serverSocket.accept();
						executorService.execute(new Runnable() {
							@Override
							public void run() {
								try {
									notifyListeners(new ConnectionEstablishedEvent(socket));
									socket.setSoTimeout(SocketOptions.SO_TIMEOUT); // SocketException: handled by try/catch.
									BufferedInputStream in = new BufferedInputStream(socket.getInputStream()); // IOException: handled by try/catch.
									byte[] request = new byte[((RSAPublicKey) votifier.getConfig().getKeyPair().getPublic()).getModulus().bitLength() / Byte.SIZE];
									in.read(request); // IOException: handled by try/catch.
									notifyListeners(new EncryptedInputReceivedEvent(socket, new String(request)));
									request = RsaUtils.getDecryptCipher(votifier.getConfig().getKeyPair().getPrivate()).doFinal(request); // IllegalBlockSizeException: can't happen.
									String requestString = new String(request);
									notifyListeners(new DecryptedInputReceivedEvent(socket, requestString));
									String[] requestArray = requestString.split("\n");
									if ((requestArray.length == 5 || requestArray.length == 6) && requestArray[0].equals("VOTE")) {
										notifyListeners(new VoteEventVotifier(socket, new Vote(requestArray[1], requestArray[2], requestArray[3], requestArray[4])));
										SimpleEntry<String, Object>[] substitutions = new SimpleEntry[4];
										substitutions[0] = new SimpleEntry<String, Object>("service-name", requestArray[1]);
										substitutions[1] = new SimpleEntry<String, Object>("user-name", requestArray[2]);
										substitutions[2] = new SimpleEntry<String, Object>("address", requestArray[3]);
										substitutions[3] = new SimpleEntry<String, Object>("timestamp", requestArray[4]);
										StrSubstitutor substitutor = SubstitutionUtils.buildStrSubstitutor(substitutions);
										HashMap<String, String> environment = new HashMap<String, String>();
										environment.put("voteServiceName", requestArray[1]);
										environment.put("voteUserName", requestArray[2]);
										environment.put("voteAddress", requestArray[3]);
										environment.put("voteTimestamp", requestArray[4]);
										for (VoteAction voteAction : votifier.getConfig().getVoteActions()) {
											if (voteAction.getCommandSender() instanceof RconCommandSender) {
												RconCommandSender commandSender = (RconCommandSender) voteAction.getCommandSender();
												for (String command : voteAction.getCommands()) {
													String theCommand = substitutor.replace(command);
													notifyListeners(new SendingRconCommandEvent(commandSender.getRconConnection(), theCommand));
													try {
														notifyListeners(new RconCommandResponseEvent(commandSender.getRconConnection(), commandSender.sendCommand(theCommand).getPayload()));
													} catch (Exception e) {
														notifyListeners(new RconExceptionEvent(commandSender.getRconConnection(), e));
													}
												}
											}
											if (voteAction.getCommandSender() instanceof ShellCommandSender) {
												ShellCommandSender commandSender = (ShellCommandSender) voteAction.getCommandSender();
												for (String command : voteAction.getCommands()) {
													notifyListeners(new SendingShellCommandEvent(command));
													try {
														commandSender.sendCommand(command, environment);
														notifyListeners(new ShellCommandSentEvent());
													} catch (Exception e) {
														notifyListeners(new ShellCommandExceptionEvent(e));
													}
												}
											}
										}
									} else {
										notifyListeners(new InvalidRequestEvent(socket, requestString));
									}
									try {
										in.close();
									} catch (Exception e) { // IOException: catching just in case. Continue even if stream doesn't close.
										notifyListeners(new ConnectionInputStreamCloseExceptionEvent(socket, e));
									}
								} catch (BadPaddingException e) {
									notifyListeners(new DecryptInputExceptionEvent(socket, e));
								} catch (Exception e) {
									notifyListeners(new CommunicationExceptionEvent(socket, e));
								}
								try {
									socket.close();
									notifyListeners(new ConnectionClosedEvent(socket));
								} catch (Exception e) { // IOException: catching just in case. Continue even if socket doesn't close.
									notifyListeners(new ConnectionCloseExceptionEvent(socket, e));
								}
							}
						});
					} catch (Exception e) {
						if (running) { // Show errors only while running, to hide error while stopping.
							notifyListeners(new ConnectionEstablishExceptionEvent(e));
						}
					}
				}
				executorService.shutdown();
				if (!executorService.isTerminated()) {
					notifyListeners(new ServerAwaitingTaskCompletionEvent());
					try {
						executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
					} catch (Exception e) {
						// InterruptedException: can't happen.
					}
				}
				notifyListeners(new ServerStoppedEvent());
			}
		}).start();
	}

	public synchronized void stop() throws IOException {
		if (!isRunning()) {
			throw new IllegalStateException("Server isn't running!");
		}
		notifyListeners(new ServerStoppingEvent());
		running = false;
		serverSocket.close();
	}

	public synchronized boolean isRunning() {
		return running;
	}

	@NotNull
	public List<Listener> getListeners() {
		return listeners;
	}

	public void notifyListeners(@NotNull Event event) {
		for (Listener listener : listeners) {
			listener.onEvent(event);
		}
	}
}