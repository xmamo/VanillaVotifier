package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.Server;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.event.server.CommandResponseEvent;
import co.virtualdragon.vanillaVotifier.event.server.ComunicationExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionClosedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionInputStreamCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.DecryptedInputReceivedEvent;
import co.virtualdragon.vanillaVotifier.event.server.EncryptedInputReceivedEvent;
import co.virtualdragon.vanillaVotifier.event.server.InvalidRequestEvent;
import co.virtualdragon.vanillaVotifier.event.server.RconExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.SendingRconCommandEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartingEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppingEvent;
import co.virtualdragon.vanillaVotifier.event.server.VoteEvent;
import co.virtualdragon.vanillaVotifier.util.RsaUtils;
import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import javax.crypto.Cipher;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifierServer implements Server {

	private final Votifier votifier;
	private final HashSet<Listener> listeners;
	
	private boolean running;
	private ServerSocket serverSocket;

	{
		listeners = new HashSet<Listener>();
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
		final Cipher cipher = RsaUtils.getDecryptCipher(votifier.getConfig().getKeyPair().getPrivate());
		running = true;
		notifyListeners(new ServerStartedEvent());
		new Thread(new Runnable() {
			@Override
			public void run() {
				while (isRunning()) {
					try {
						final Socket socket = serverSocket.accept();
						new Thread(new Runnable() {
							@Override
							public void run() {
								try {
									notifyListeners(new ConnectionEstablishedEvent(socket));
									socket.setSoTimeout(10000); // SocketException: handled by try/catch.
									InputStream in = socket.getInputStream(); // IOException: handled by try/catch.
									byte[] request = new byte[253];
									in.read(request); // IOException: handled by try/catch.
									notifyListeners(new EncryptedInputReceivedEvent(socket, new String(request, "UTF-8"))); // UnsupportedEncodingException: can't happen.
									request = cipher.doFinal(request); // Exception: handled by try/catch.
									String requestString = new String(request, "UTF-8"); // UnsupportedEncodingException: can't happen.
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
										for (String command : votifier.getConfig().getCommands()) {
											command = substitutor.replace(command);
											notifyListeners(new SendingRconCommandEvent(command));
											try {
												notifyListeners(new CommandResponseEvent(votifier.getCommandSender().sendCommand(command)));
											} catch (Exception e) {
												notifyListeners(new RconExceptionEvent(e));
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
									try {
										socket.close();
										notifyListeners(new ConnectionClosedEvent(socket));
									} catch (Exception e) { // IOException: catching just in case. Continue even if socket doesn't close.
										notifyListeners(new ConnectionCloseExceptionEvent(socket, e));
									}
								} catch (Exception e) {
									notifyListeners(new ComunicationExceptionEvent(e));
								}
							}
						}).start();
					} catch (Exception e) {
						if (running) {
							notifyListeners(new ConnectionEstablishExceptionEvent(e));
						} // else {} To hide error while stopping.
					}
				}
				try {
					serverSocket.close();
				} catch (Exception e) {
					notifyListeners(new ServerCloseExceptionEvent(e));
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
		serverSocket.close();
		running = false;
	}

	@Override
	public boolean isRunning() {
		return running;
	}

	@Override
	public Set<Listener> getListeners() {
		return listeners;
	}

	@Override
	public void notifyListeners(Event event) {
		for (Listener listener : listeners) {
			listener.onEvent(event, votifier);
		}
	}
}
