package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.Event;

public interface RconCommandEvent extends Event {

	String getCommand();
}
