package co.virtualdragon.vanillaVotifier.server.event;

import java.net.Socket;

public class DecryptedInputReceivedEvent extends AbstractMessageEvent implements SocketEvent {

	private Socket socket;

	public DecryptedInputReceivedEvent(Socket socket, String message) {
		super(message);
		this.socket = socket;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}
}
