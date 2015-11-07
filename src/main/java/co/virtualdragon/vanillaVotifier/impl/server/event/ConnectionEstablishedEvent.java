package co.virtualdragon.vanillaVotifier.impl.server.event;

import java.net.Socket;

public class ConnectionEstablishedEvent extends AbstractSocketEvent {

	public ConnectionEstablishedEvent(Socket socket) {
		super(socket);
	}
}
