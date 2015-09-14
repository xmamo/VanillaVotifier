package co.virtualdragon.vanillaVotifier;

import co.virtualdragon.vanillaVotifier.event.Event;

public interface Listener {

	void onEvent(Event event, VanillaVotifier votifier);
}
