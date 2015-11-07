package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class RconExceptionEvent extends AbstractExceptionEvent {

	public RconExceptionEvent(Exception exception) {
		super(exception);
	}
}
