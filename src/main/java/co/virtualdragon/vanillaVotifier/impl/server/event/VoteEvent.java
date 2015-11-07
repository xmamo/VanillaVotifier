package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.Vote;
import java.net.Socket;

public class VoteEvent extends AbstractRequestEvent {

	public VoteEvent(Socket socket, Vote vote) {
		super(socket, vote);
	}
}
