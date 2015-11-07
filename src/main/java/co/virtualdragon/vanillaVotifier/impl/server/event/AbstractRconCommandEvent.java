package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.server.event.RconCommandEvent;

public abstract class AbstractRconCommandEvent implements RconCommandEvent {

	private final String command;

	public AbstractRconCommandEvent(String command) {
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
