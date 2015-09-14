package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.event.Event;

public interface RconCommandEvent extends Event {

	String getCommand();
}
