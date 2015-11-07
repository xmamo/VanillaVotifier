package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.Vote;

public interface RequestEvent extends SocketEvent {

	Vote getVote();
}
