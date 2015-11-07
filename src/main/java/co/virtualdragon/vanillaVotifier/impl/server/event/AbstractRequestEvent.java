package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.server.event.RequestEvent;
import co.virtualdragon.vanillaVotifier.Vote;
import java.net.Socket;

public abstract class AbstractRequestEvent extends AbstractSocketEvent implements RequestEvent {

	private final Vote vote;

	public AbstractRequestEvent(Socket socket, Vote vote) {
		super(socket);
		this.vote = vote;
	}

	@Override
	public Vote getVote() {
		return vote;
	}
}
