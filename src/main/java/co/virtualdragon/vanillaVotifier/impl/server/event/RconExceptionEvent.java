package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class RconExceptionEvent extends AbstractExceptionEvent {

	public RconExceptionEvent(Exception exception) {
		super(exception);
	}
}
