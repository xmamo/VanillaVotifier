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
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.net.SocketTimeoutException;
import java.security.interfaces.RSAPublicKey;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.regex.PatternSyntaxException;

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
									BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
									writer.write("VOTIFIER 2.9\n");
									writer.flush();
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
										for (VoteAction voteAction : votifier.getConfig().getVoteActions()) {
											String[] params = new String[4];
											try {
												for (int i = 0; i < params.length; i++) {
													params[i] = SubstitutionUtils.applyRegexReplacements(requestArray[i + 1], voteAction.getRegexReplacements());
												}
											} catch (PatternSyntaxException e) {
												notifyListeners(new RegularExpressionPatternErrorException(e));
												params = new String[]{requestArray[1], requestArray[2], requestArray[3], requestArray[4]};
											}
											if (voteAction.getCommandSender() instanceof RconCommandSender) {
												RconCommandSender commandSender = (RconCommandSender) voteAction.getCommandSender();
												StrSubstitutor substitutor = SubstitutionUtils.buildStrSubstitutor(
														new SimpleEntry<String, Object>("service-name", params[0]),
														new SimpleEntry<String, Object>("user-name", params[1]),
														new SimpleEntry<String, Object>("address", params[2]),
														new SimpleEntry<String, Object>("timestamp", params[3])
												);
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
												HashMap<String, String> environment = new HashMap<String, String>();
												environment.put("voteServiceName", params[0]);
												environment.put("voteUserName", params[1]);
												environment.put("voteAddress", params[2]);
												environment.put("voteTimestamp", params[3]);
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
								} catch (SocketTimeoutException e) {
									notifyListeners(new ReadTimedOutExceptionEvent(socket, e));
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