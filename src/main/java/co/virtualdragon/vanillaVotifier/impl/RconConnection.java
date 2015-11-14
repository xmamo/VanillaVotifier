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

import co.virtualdragon.vanillaVotifier.Config.RconConfig;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Rcon.Packet.Type;
import co.virtualdragon.vanillaVotifier.Rcon.VanillaVotifierPacket;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public class RconConnection implements Rcon {

	private final RconConfig rconConfig;

	private Socket socket;
	private int requestId;

	public RconConnection(RconConfig rconConfig) {
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

	@Override
	public RconConfig getRconConfig() {
		return rconConfig;
	}

	@Override
	public int getRequestId() {
		return requestId;
	}

	@Override
	public synchronized void connect() throws IOException {
		socket = new Socket(rconConfig.getInetSocketAddress().getAddress(), rconConfig.getInetSocketAddress().getPort());
		socket.setSoTimeout(SocketOptions.SO_TIMEOUT);
	}

	@Override
	public synchronized boolean isConnected() {
		if (socket != null) {
			try {
				System.out.println(sendRequest(new VanillaVotifierPacket(requestId, Type.COMMAND, null)));
				return true;
			} catch (Exception e) {
			}
		}
		return false;
	}

	@Override
	public Packet logIn() throws UnsupportedEncodingException, IOException {
		return sendRequest(new VanillaVotifierPacket(requestId, Type.LOG_IN, rconConfig.getPassword()));
	}

	@Override
	public synchronized Packet sendRequest(Packet request) throws UnsupportedEncodingException, IOException {
		if (socket == null) {
			throw new IllegalStateException("RCon has yet to be connected!");
		}
		byte[] requestBytes = new byte[request.getLength() + Integer.SIZE / 8];
		ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
		requestBuffer.order(ByteOrder.LITTLE_ENDIAN);
		requestBuffer.putInt(request.getLength());
		requestBuffer.putInt(requestId);
		requestBuffer.putInt(request.getType().toInt());
		requestBuffer.put(request.getPayload().getBytes("UTF-8"));
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
		Type responseType = Type.fromInt(responseBuffer.getInt());
		byte[] responsePayload = new byte[responseLength - Integer.SIZE / 8 - Integer.SIZE / 8 - Byte.SIZE / 8 * 2];
		responseBuffer.get(responsePayload);
		responseBuffer.get();
		responseBuffer.get();
		return new VanillaVotifierPacket(responseLength, responseRequestId, responseType, new String(responsePayload, "UTF-8"));
	}
}
