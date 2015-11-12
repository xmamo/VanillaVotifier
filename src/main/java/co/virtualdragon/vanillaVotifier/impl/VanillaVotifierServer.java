/* 
 * Copyright (C) 2015 VirtualDragon
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Server;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.event.server.RconCommandResponseEvent;
import co.virtualdragon.vanillaVotifier.event.server.ComunicationExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionClosedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionInputStreamCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.DecryptInputExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.DecryptedInputReceivedEvent;
import co.virtualdragon.vanillaVotifier.event.server.EncryptedInputReceivedEvent;
import co.virtualdragon.vanillaVotifier.event.server.InvalidRequestEvent;
import co.virtualdragon.vanillaVotifier.event.server.RconExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.SendingRconCommandEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerAwaitingTaskCompletionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartingEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppingEvent;
import co.virtualdragon.vanillaVotifier.event.server.VoteEvent;
import co.virtualdragon.vanillaVotifier.util.RsaUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketOptions;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.crypto.BadPaddingException;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifierServer implements Server {

	private final Votifier votifier;
	private final ArrayList<Listener> listeners;

	private boolean running;
	private ServerSocket serverSocket;

	{
		listeners = new ArrayList<Listener>();
		getListeners().add(new VanillaVotifierServerListener());
	}

	public VanillaVotifierServer(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public void start() throws IOException, GeneralSecurityException {
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
									notifyListeners(new EncryptedInputReceivedEvent(socket, new String(request, StandardCharsets.UTF_8))); // UnsupportedEncodingException: can't happen.
									request = RsaUtils.getDecryptCipher(votifier.getConfig().getKeyPair().getPrivate()).doFinal(request); // IllegalBlockSizeException: can't happen.
									String requestString = new String(request, StandardCharsets.UTF_8); // UnsupportedEncodingException: can't happen.
									notifyListeners(new DecryptedInputReceivedEvent(socket, requestString));
									String[] requestArray = requestString.split("\n");
									if ((requestArray.length == 5 || requestArray.length == 6) && requestArray[0].equals("VOTE")) {
										notifyListeners(new VoteEvent(socket, new VanillaVotifierVote(requestArray[1], requestArray[2], requestArray[3], requestArray[4])));
										HashMap<String, String> substitutions = new HashMap<String, String>();
										substitutions.put("service-name", requestArray[1]);
										substitutions.put("user-name", requestArray[2]);
										substitutions.put("address", requestArray[3]);
										substitutions.put("time-stamp", requestArray[4]);
										StrSubstitutor substitutor = new StrSubstitutor(substitutions);
										for (Rcon rcon : votifier.getRcons()) {
											for (String command : rcon.getRconConfig().getCommands()) {
												command = substitutor.replace(command);
												notifyListeners(new SendingRconCommandEvent(rcon, command));
												try {
													notifyListeners(new RconCommandResponseEvent(rcon, votifier.getCommandSender().sendCommand(rcon, command)));
												} catch (Exception e) {
													notifyListeners(new RconExceptionEvent(rcon, e));
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
									notifyListeners(new ComunicationExceptionEvent(socket, e));
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

	@Override
	public void stop() throws IOException {
		if (!isRunning()) {
			throw new IllegalStateException("Server isn't running!");
		}
		notifyListeners(new ServerStoppingEvent());
		running = false;
		serverSocket.close();
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public List<Listener> getListeners() {
		return listeners;
	}

	@Override
	public void notifyListeners(Event event) {
		for (Listener listener : (ArrayList<Listener>) listeners.clone()) {
			listener.onEvent(event, votifier);
		}
	}
}
