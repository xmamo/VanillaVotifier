package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.Vote;

public interface RequestEvent extends SocketEvent {

	Vote getVote();
}
