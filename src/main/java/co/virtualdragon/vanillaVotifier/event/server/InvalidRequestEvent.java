package co.virtualdragon.vanillaVotifier.event.server;

import java.net.Socket;

public class InvalidRequestEvent implements SocketEvent, MessageEvent {

	private final Socket socket;
	private final String message;

	public InvalidRequestEvent(Socket socket, String message) {
		this.socket = socket;
		this.message = message;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
