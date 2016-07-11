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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public class Rcon {
	private final RconConfig rconConfig;

	private Socket socket;
	private int requestId;

	public Rcon(RconConfig rconConfig) {
		if (rconConfig == null) {
			throw new IllegalArgumentException("rconConfig can't be null!");
		}
		this.rconConfig = rconConfig;
		SecureRandom random = new SecureRandom();
		while (true) {
			requestId = random.nextInt();
			if (requestId != -1) {
				break;
			}
		}
	}

	public RconConfig getRconConfig() {
		return rconConfig;
	}

	public int getRequestId() {
		return requestId;
	}

	public synchronized void connect() throws IOException {
		socket = new Socket(rconConfig.getInetSocketAddress().getAddress(), rconConfig.getInetSocketAddress().getPort());
		socket.setSoTimeout(SocketOptions.SO_TIMEOUT);
	}

	public synchronized boolean isConnected() {
		if (socket != null) {
			try {
				sendRequest(new VotifierPacket(requestId, VotifierPacket.Type.COMMAND, null));
				return true;
			} catch (Exception e) {
				// IOException
			}
		}
		return false;
	}

	public VotifierPacket logIn() throws IOException {
		return sendRequest(new VotifierPacket(requestId, VotifierPacket.Type.LOG_IN, rconConfig.getPassword()));
	}

	public synchronized VotifierPacket sendRequest(VotifierPacket request) throws IOException {
		if (socket == null) {
			throw new IllegalStateException("RCon has yet to be connected!");
		}
		byte[] requestBytes = new byte[request.getLength() + Integer.SIZE / 8];
		ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
		requestBuffer.order(ByteOrder.LITTLE_ENDIAN);
		requestBuffer.putInt(request.getLength());
		requestBuffer.putInt(requestId);
		requestBuffer.putInt(request.getType().toInt());
		requestBuffer.put(request.getPayload().getBytes());
		requestBuffer.put((byte) 0);
		requestBuffer.put((byte) 0);
		socket.getOutputStream().write(requestBytes);
		socket.getOutputStream().flush();
		byte[] responseBytes = new byte[Integer.SIZE / 8];
		socket.getInputStream().read(responseBytes);
		ByteBuffer responseBuffer = ByteBuffer.wrap(responseBytes);
		responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int responseLength = responseBuffer.getInt();
		responseBytes = new byte[responseLength];
		socket.getInputStream().read(responseBytes);
		responseBuffer = ByteBuffer.wrap(responseBytes);
		responseBuffer.order(ByteOrder.LITTLE_ENDIAN);
		int responseRequestId = responseBuffer.getInt();
		VotifierPacket.Type responseType = VotifierPacket.Type.fromInt(responseBuffer.getInt());
		byte[] responsePayload = new byte[responseLength - Integer.SIZE / 8 - Integer.SIZE / 8 - Byte.SIZE / 8 * 2];
		responseBuffer.get(responsePayload);
		responseBuffer.get();
		responseBuffer.get();
		return new VotifierPacket(responseLength, responseRequestId, responseType, new String(responsePayload));
	}
}