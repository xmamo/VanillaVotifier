package co.virtualdragon.vanillaVotifier.impl.server.event;

import java.net.Socket;

public class ConnectionClosedEvent extends AbstractSocketEvent {

	public ConnectionClosedEvent(Socket socket) {
		super(socket);
	}
}
