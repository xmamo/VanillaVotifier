package com.mamoslab.vanillaVotifier.handlers;

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
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

public class ConnectionHandler {

	private static final Logger LOGGER;

	private volatile boolean running;
	private ServerSocket serverSocket;
	private Socket mcRcon;
	private int requestId;

	static {
		LOGGER = Logger.getLogger(ConnectionHandler.class.getName());
		LOGGER.setLevel(Level.ALL);
	}

	public void start() {
		if (running) {
			return;
		}

		LOGGER.info("Connecting to RCon...");
		try {
			mcRcon = new Socket(VanillaVotifier.getConfigHandler().getMcRconIp(), VanillaVotifier.getConfigHandler().getMcRconPort());
		} catch (Exception e) {
			LOGGER.severe("Coudln't set up socket connection with RCon!");
			System.exit(0);
		}
		LOGGER.info("Connected to RCon.");

		LOGGER.info("Logging in to RCon...");
		{
			Random random = new Random(System.currentTimeMillis());
			while (true) {
				requestId = random.nextInt();
				if (requestId != -1) {
					break;
				}
			}
			int type = 3; // To log in
			String payload = VanillaVotifier.getConfigHandler().getMcRconPassword();
			int length = Integer.SIZE / 8 + Integer.SIZE / 8 + payload.length() + Byte.SIZE / 8 * 2;
			byte[] command = new byte[length + Integer.SIZE / 8];
			ByteBuffer byteBuffer = ByteBuffer.wrap(command);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			byteBuffer.putInt(length);
			byteBuffer.putInt(requestId);
			byteBuffer.putInt(type);
			try {
				byteBuffer.put(payload.getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// Can't happen
			}
			byteBuffer.put((byte) 0);
			byteBuffer.put((byte) 0);
			try {
				mcRcon.getOutputStream().write(command);
				mcRcon.getOutputStream().flush();
			} catch (Exception e) {
				LOGGER.severe("Unexpected exception while sending password to RCon: " + e.getMessage());
				System.exit(0);
			}
		}
		{
			byte[] message = new byte[Integer.SIZE / 8];
			try {
				mcRcon.getInputStream().read(message);
			} catch (Exception e) {
				LOGGER.severe("Unexpected exception while reading RCon response: " + e.getMessage());
				System.exit(0);
			}
			ByteBuffer byteBuffer = ByteBuffer.wrap(message);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int length = byteBuffer.getInt();

			message = new byte[length];
			byteBuffer = ByteBuffer.wrap(message);
			byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int requestId = byteBuffer.getInt();
			int type = byteBuffer.getInt();
			byte[] payload = new byte[length - Integer.SIZE / 8 - Integer.SIZE / 8 - Byte.SIZE / 8 * 2];
			byteBuffer.get(payload);
			byteBuffer.get(new byte[Byte.SIZE / 8 * 2]);
			if (requestId == -1) {
				LOGGER.severe("RCon password is incorrect! Aborting!");
				System.exit(0);
			}
		}
		LOGGER.info("Logged in to RCon.");

		LOGGER.info("Starting Vanilla votifier server...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					serverSocket = new ServerSocket();
					serverSocket.bind(new InetSocketAddress(VanillaVotifier.getConfigHandler().getVotifierIp(), VanillaVotifier.getConfigHandler().getVotifierPort()));
				} catch (IllegalArgumentException e) {
					if (e.getMessage().startsWith("port out of range")) {
						LOGGER.severe("Votifier port is invalid! Aborting");
					} else {
						LOGGER.severe("Unexpected exception while setting up server socket: aborting! " + e.getMessage());
					}
					System.exit(0);
				} catch (IOException e) {
					LOGGER.severe("Couldn't set up server socket! Perhaps a server is already running on that port?");
					System.exit(0);
				} catch (Exception e) {
					LOGGER.severe("Unexpected exception while setting up server socket: aborting! " + e.getMessage());
					System.exit(0);
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
						out.write("Provided by Vanilla Votifier, coded by Mamo (http://mamoslab.com/)");
						out.newLine();
						out.flush();
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while writing output to client: " + e.getMessage());
					}

					byte[] message = new byte[253];
					try {
						in.read(message);
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while reading input from client: " + e.getMessage());
						continue;
					}
					message = Arrays.copyOf(message, message.length);

					Cipher cipher;
					try {
						cipher = Utils.getDecryptCipher();
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while initializing RSA cipher: " + e.getMessage());
						continue;
					}
					try {
						message = cipher.doFinal(message);
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while decrypting client message: " + e.getMessage());
						e.printStackTrace();
						continue;
					}
					String[] command;
					try {
						command = new String(message, "UTF-8").split("\n");
					} catch (UnsupportedEncodingException ex) {
						// Can't happen
						continue;
					}
					if (command.length >= 5 || command[0].equals("VOTE")) {
						String serviceName = command[1];
						String userName = command[2];
						String address = command[3];
						String timeStamp = command[4];
						LOGGER.info(userName + " (" + address + ") voted for your server at " + serviceName + ".");

						for (String payload : VanillaVotifier.getConfigHandler().getVotifierOnVoteMcScript().split("\t")) {
							payload = String.format(payload, serviceName, userName, address, timeStamp);
							LOGGER.info("Sending command to RCon: " + payload);
							int type = 2; // To send a command
							int length = Integer.SIZE / 8 + Integer.SIZE / 8 + payload.length() + Byte.SIZE / 8 * 2;
							byte[] command_ = new byte[length + Integer.SIZE / 8];
							ByteBuffer byteBuffer = ByteBuffer.wrap(command_);
							byteBuffer.order(ByteOrder.LITTLE_ENDIAN);
							byteBuffer.putInt(length);
							byteBuffer.putInt(requestId);
							byteBuffer.putInt(type);
							try {
								byteBuffer.put(payload.getBytes("UTF-8"));
							} catch (UnsupportedEncodingException e) {
								// Can't happen
							}
							byteBuffer.put((byte) 0);
							byteBuffer.put((byte) 0);
							try {
								mcRcon.getOutputStream().write(command_);
								mcRcon.getOutputStream().flush();
							} catch (Exception e) {
								LOGGER.severe("Unexpected exception while sending password to RCon: " + e.getMessage());
								System.exit(0);
							}
						}
					} else {
						LOGGER.info("Received invalid message from " + socket.getRemoteSocketAddress() + ": " + new String(message).replaceAll("\n", "\t"));
					}

					try {
						socket.close();
						LOGGER.info(socket.getRemoteSocketAddress() + " disconnected.");
					} catch (Exception e) {
						LOGGER.warning("Unexpected exception while closing socket connection with " + socket.getRemoteSocketAddress().toString().replaceFirst("/", "") + ": " + e.getMessage());
					}
				}
				LOGGER.info("Server stopped.");
			}
		}).start();
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
