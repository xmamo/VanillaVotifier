package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.server.event.SocketEvent;
import java.net.Socket;

public class EncryptedInputReceivedEvent extends AbstractMessageEvent implements SocketEvent {

	private final Socket socket;

	public EncryptedInputReceivedEvent(Socket socket, String message) {
		super(message);
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
