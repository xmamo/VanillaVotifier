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

import mamo.vanillaVotifier.exception.InvalidRconPasswordException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketOptions;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.security.SecureRandom;

public class RconConnection {
	@NotNull protected InetSocketAddress inetSocketAddress;
	@NotNull protected String password;
	protected int requestId;
	@Nullable protected Socket socket;

	public RconConnection(@NotNull InetSocketAddress inetSocketAddress, @NotNull String password) {
		this.inetSocketAddress = inetSocketAddress;
		this.password = password;
		SecureRandom random = new SecureRandom();
		while (true) {
			requestId = random.nextInt();
			if (requestId != -1) {
				break;
			}
		}
	}

	@NotNull
	public InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

	@NotNull
	public String getPassword() {
		return password;
	}

	public int getRequestId() {
		return requestId;
	}

	public synchronized VotifierPacket logIn() throws IOException, InvalidRconPasswordException {
		return sendRequest(new VotifierPacket(getRequestId(), VotifierPacket.Type.LOG_IN, getPassword()));
	}

	@NotNull
	public synchronized VotifierPacket sendCommand(@NotNull String command) throws IOException {
		try {
			return sendRequest(new VotifierPacket(getRequestId(), VotifierPacket.Type.COMMAND, command));
		} catch (InvalidRconPasswordException e) {
			// Can't happen.
		}
		return null;
	}

	@NotNull
	public synchronized VotifierPacket sendRequest(@NotNull VotifierPacket request) throws IOException, InvalidRconPasswordException {
		if (socket == null) {
			socket = new Socket(inetSocketAddress.getAddress(), inetSocketAddress.getPort());
			socket.setSoTimeout(SocketOptions.SO_TIMEOUT);
		}
		byte[] requestBytes = new byte[request.getLength() + Integer.SIZE / 8];
		ByteBuffer requestBuffer = ByteBuffer.wrap(requestBytes);
		requestBuffer.order(ByteOrder.LITTLE_ENDIAN);
		requestBuffer.putInt(request.getLength());
		requestBuffer.putInt(getRequestId());
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
		if (request.getType() == VotifierPacket.Type.LOG_IN) {
			if (responseRequestId != getRequestId()) {
				throw new InvalidRconPasswordException();
			}
		}
		return new VotifierPacket(responseLength, responseRequestId, responseType, new String(responsePayload));
	}
}