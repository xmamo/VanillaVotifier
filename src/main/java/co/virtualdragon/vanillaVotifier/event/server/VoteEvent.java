package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.Vote;
import java.net.Socket;

public class VoteEvent implements RequestEvent {

	private final Socket socket;
	private final Vote vote;

	public VoteEvent(Socket socket, Vote vote) {
		this.socket = socket;
		this.vote = vote;
	}

	@Override
	public Socket getSocket() {
		return socket;
	}

	@Override
	public Vote getVote() {
		return vote;
	}
}
