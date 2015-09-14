package co.virtualdragon.vanillaVotifier.server.event;

import java.net.Socket;

public class InvalidRequestEvent extends AbstractMessageEvent implements SocketEvent {

	private Socket socket;

	public InvalidRequestEvent(Socket socket, String message) {
		super(message);
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

}
