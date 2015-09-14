package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;
import java.net.Socket;

public class ConnectionCloseException extends AbstractExceptionEvent implements SocketEvent {

	private Socket socket;

	public ConnectionCloseException(Socket socket, Exception exception) {
		super(exception);
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
