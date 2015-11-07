package co.virtualdragon.vanillaVotifier.event.server;

import java.net.Socket;

public class ConnectionEstablishedEvent implements SocketEvent {

	private final Socket socket;

	public ConnectionEstablishedEvent(Socket socket) {
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
