package co.virtualdragon.vanillaVotifier.server.event;

import java.net.Socket;

public class VoteEvent extends AbstractRequestEvent {

	public VoteEvent(Socket socket, String message, String serviceName, String userName, String address, String timeStamp) {
		super(socket, message, serviceName, userName, address, timeStamp);
	}
}
