package co.virtualdragon.vanillaVotifier.server.event;

import java.net.Socket;

public abstract class AbstractSocketEvent implements SocketEvent {
	
	private Socket socket;

	public AbstractSocketEvent(Socket socket) {
		this.socket = socket;
	}
	
	@Override
	public Socket getSocket() {
		return socket;
	}
}
