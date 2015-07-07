package com.mamoslab.vanillaVotifier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;
import java.util.logging.Logger;

public class Rcon {

	private static final Logger LOGGER;

	private Socket socket;
	private int requestId;

	static {
		LOGGER = Logger.getLogger(Rcon.class.getName());
	}

	public Rcon(String host, int port) throws UnknownHostException, IOException {
		socket = new Socket(host, port);

		Random random = new Random(System.currentTimeMillis());
		while (true) {
			requestId = random.nextInt();
			if (requestId != -1) {
				break;
			}
		}
	}

	public boolean logIn(String password) {
		LOGGER.info("Logging in to RCon...");
		Packet response = sendRequest(new Packet(this, 3, password)); // Type 3 to log in
		if (response == null) {
			return false;
		}
		if (response.getRequestId() == -1) {
			LOGGER.severe("RCon password is incorrect!");
			return false;
		}
		LOGGER.info("Logged in to RCon.");
		return true;
	}

	public Packet sendRequest(Packet request) {
		try {
			byte[] requestBytes = new byte[request.getLength() + Integer.SIZE / 8];
			ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
			requestBuffer.order(ByteOrder.LITTLE_ENDIAN);
			requestBuffer.putInt(request.getLength());
			requestBuffer.putInt(requestId);
			requestBuffer.putInt(request.getType());
			try {
				requestBuffer.put(request.getPayload().getBytes("UTF-8"));
			} catch (UnsupportedEncodingException e) {
				// Can't happen
			}
			requestBuffer.put((byte) 0);
			requestBuffer.put((byte) 0);
			try {
				socket.getOutputStream().write(requestBytes);
				socket.getOutputStream().flush();
			} catch (Exception e) {
				if (request.getType() == 3) {
					LOGGER.severe("Unexpected exception while sending request to RCon: " + e.getMessage());
				} else {
					LOGGER.warning("Unexpected exception while sending request to RCon: " + e.getMessage());
				}
				return null;
			}
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while sending request to RCon: " + e.getMessage());
			return null;
		}

		try {
			byte[] responseBytes = new byte[Integer.SIZE / 8];
			try {
				socket.getInputStream().read(responseBytes);
			} catch (Exception e) {
				if (request.getType() == 3) {
					LOGGER.severe("Unexpected exception while reading RCon response: " + e.getMessage());
				} else {
					LOGGER.warning("Unexpected exception while reading RCon response: " + e.getMessage());
				}
				return null;
			}
			ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);
			responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int responseLength = responseBuffer.getInt();

			responseBytes = new byte[responseLength];
			try {
				socket.getInputStream().read(responseBytes);
			} catch (Exception e) {
				if (request.getType() == 3) {
					LOGGER.severe("Unexpected exception while reading RCon response: " + e.getMessage());
				} else {
					LOGGER.warning("Unexpected exception while reading RCon response: " + e.getMessage());
				}
				return null;
			}
			responseBuffer = ByteBuffer.wrap(responseBytes);
			responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
			int responseRequestId = responseBuffer.getInt();
			int responseType = responseBuffer.getInt();
			byte[] responsePayload = new byte[responseLength - Integer.SIZE / 8 - Integer.SIZE / 8 - Byte.SIZE / 8 * 2];
			responseBuffer.get(responsePayload);
			responseBuffer.get();
			responseBuffer.get();
			try {
				return new Packet(responseLength, responseRequestId, responseType, new String(responsePayload, "UTF-8"));
			} catch (UnsupportedEncodingException ex) {
				// Can't happen
				return null;
			}
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while reading response from RCon: " + e.getMessage());
			return null;
		}
	}

	public static class Packet {

		private int length;
		private int requestId;
		private int type;
		private String payload;

		public Packet(Rcon rcon, int type, String payload) {
			this(Integer.SIZE / 8 + Integer.SIZE / 8 + payload.length() + Byte.SIZE / 8 * 2, rcon.requestId, type, payload);
		}

		private Packet(int length, int requestId, int type, String payload) {
			this.length = length;
			this.requestId = requestId;
			this.type = type;
			this.payload = payload;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public int getRequestId() {
			return requestId;
		}

		public void setRequestId(int requestId) {
			this.requestId = requestId;
		}

		public int getType() {
			return type;
		}

		public void setType(int type) {
			this.type = type;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(String payload) {
			this.payload = payload;
		}

		@Override
		public String toString() {
			return length + "\t" + requestId + "\t" + type + "\t" + payload;
		}
	}
}
