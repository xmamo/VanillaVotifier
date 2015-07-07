package com.mamoslab.vanillaVotifier.handlers;

import com.mamoslab.vanillaVotifier.Rcon;
import com.mamoslab.vanillaVotifier.Utils;
import com.mamoslab.vanillaVotifier.VanillaVotifier;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

public class ConnectionHandler {

	private static final Logger LOGGER;

	private volatile boolean running;
	private ServerSocket serverSocket;
	private Rcon rCon;
	private volatile boolean result;

	static {
		LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
		LOGGER.setLevel(Level.ALL);
	}

	public boolean start() {
		if (running) {
			return true;
		}

		LOGGER.info("Connecting to RCon...");
		try {
			rCon = new Rcon(VanillaVotifier.getConfigHandler().getMcRconIp(), VanillaVotifier.getConfigHandler().getMcRconPort());
		} catch (Exception e) {
			LOGGER.severe("Coudln't set up socket connection with RCon!");
			return false;
		}
		LOGGER.info("Connected to RCon.");
		if (!rCon.logIn(VanillaVotifier.getConfigHandler().getMcRconPassword())) {
			return false;
		}

		LOGGER.info("Starting Vanilla votifier server...");
		result = true;
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket();
					serverSocket.bind(new InetSocketAddress(VanillaVotifier.getConfigHandler().getVotifierIp(), VanillaVotifier.getConfigHandler().getVotifierPort()));
				} catch (IllegalArgumentException e) {
					if (e.getMessage().startsWith("port out of range")) {
						LOGGER.severe("Votifier port is invalid!");
					} else {
						LOGGER.severe("Unexpected exception while setting up server socket! " + e.getMessage());
					}
					result = false;
					return;
				} catch (IOException e) {
					LOGGER.severe("Couldn't set up server socket! Perhaps a server is already running on that port?");
					result = false;
					return;
				} catch (Exception e) {
					LOGGER.severe("Unexpected exception while setting up server socket: aborting! " + e.getMessage());
					result = false;
					return;
				}
				LOGGER.info("Vanilla votifier started on " + serverSocket.getLocalSocketAddress() + ". Public key is " + new String(Base64.getEncoder().encode(VanillaVotifier.getConfigHandler().getVotifierRSAPublicKey().getEncoded())));

				running = true;
				while (running) {
					Socket socket;
					InputStream in;
					BufferedWriter out;
					try {
						socket = serverSocket.accept();
						socket.setSoTimeout(5000);
						in = socket.getInputStream();
						out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					} catch (SocketException e) {
						if (e.getMessage().equals("socket closed")) {
							// Don't care
						} else {
							LOGGER.warning("Unexpected exception while establishing socket connection: " + e.getMessage());
						}
						continue;
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while establishing socket connection: " + e.getMessage());
						continue;
					}
					LOGGER.info(socket.getRemoteSocketAddress() + " established a connection.");

					try {
						out.write("Provided by Vanilla Votifier (https://github.com/MamosLab/Vanilla-votifier).");
						out.newLine();
						out.flush();
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while writing output to client: " + e.getMessage());
					}

					byte[] request = new byte[253];
					try {
						in.read(request);
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while reading client request: " + e.getMessage());
						continue;
					}
					request = Arrays.copyOf(request, request.length);

					Cipher cipher;
					try {
						cipher = Utils.getDecryptCipher();
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while initializing RSA cipher: " + e.getMessage());
						continue;
					}
					try {
						request = cipher.doFinal(request);
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while decrypting client request: " + e.getMessage());
						e.printStackTrace();
						continue;
					}
					String[] requestArray;
					try {
						requestArray = new String(request, "UTF-8").split("\n");
					} catch (UnsupportedEncodingException ex) {
						// Can't happen
						continue;
					}
					if (requestArray.length >= 5 || requestArray[0].equals("VOTE")) {
						String serviceName = requestArray[1];
						String userName = requestArray[2];
						String address = requestArray[3];
						String timeStamp = requestArray[4];
						LOGGER.info(userName + " (" + address + ") voted for your server at " + serviceName + ".");

						for (String command : VanillaVotifier.getConfigHandler().getVotifierOnVoteMcScript().split("\t")) {
							command = String.format(command, serviceName, userName, address, timeStamp);
							LOGGER.info("Sending command to RCon: " + command);
							Rcon.Packet packet = rCon.sendRequest(new Rcon.Packet(rCon, 2, command)); // Type 2 to send a command
							if (packet != null) {
								if (packet.getPayload() != null && !packet.getPayload().isEmpty()) {
									LOGGER.info("Command sent. RCon response: " + packet.getPayload());
								} else {
									LOGGER.info("Command sent.");
								}
							}
						}
					} else {
						LOGGER.info("Received invalid request from " + socket.getRemoteSocketAddress() + ": " + new String(request).replaceAll("\n", "\t"));
					}

					try {
						socket.close();
						LOGGER.info(socket.getRemoteSocketAddress() + " disconnected.");
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while closing socket connection with " + socket.getRemoteSocketAddress() + ": " + e.getMessage());
					}
				}
				LOGGER.info("Server stopped.");
			}
		}).start();
		return result;
	}

	public void stop() {
		LOGGER.info("Stopping server...");
		try {
			serverSocket.close();
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while closing server socket: " + e.getMessage());
		}
		running = false;
	}
}
