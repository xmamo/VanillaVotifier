package co.virtualdragon.vanillaVotifier.event.server;

import java.net.Socket;

public class ConnectionClosedEvent implements SocketEvent {

	private final Socket socket;

	public ConnectionClosedEvent(Socket socket) {
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
