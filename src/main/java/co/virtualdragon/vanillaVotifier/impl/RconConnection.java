package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Rcon.Packet;
import co.virtualdragon.vanillaVotifier.Rcon.Packet.Type;
import co.virtualdragon.vanillaVotifier.Votifier;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Random;

public class RconConnection implements Rcon {

	private final Votifier votifier;
	
	private Socket socket;
	private int requestId;

	public RconConnection(Votifier votifier) {
		this.votifier = votifier;
		Random random = new Random(System.currentTimeMillis());
		while (true) {
			requestId = random.nextInt();
			if (requestId != -1) {
				break;
			}
		}
	}

	@Override
	public void connect() throws IOException {
		socket = new Socket(votifier.getConfig().getRconInetSocketAddress().getAddress(), votifier.getConfig().getRconInetSocketAddress().getPort());
	}

	@Override
	public boolean isConnected() {
		return socket != null && !socket.isClosed();
	}

	@Override
	public int getRequestId() {
		return requestId;
	}

	@Override
	public Packet logIn(String password) throws UnsupportedEncodingException, IOException {
		return sendRequest(new Packet(requestId, Type.LOG_IN, password));
	}

	@Override
	public Packet sendRequest(Packet request) throws UnsupportedEncodingException, IOException {
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
		return new Packet(responseLength, responseRequestId, responseType, new String(responsePayload, "UTF-8"));
	}
}
