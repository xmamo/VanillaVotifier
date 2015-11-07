package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.server.event.SocketEvent;
import java.net.Socket;

public abstract class AbstractSocketEvent implements SocketEvent {

	private final Socket socket;

	public AbstractSocketEvent(Socket socket) {
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
