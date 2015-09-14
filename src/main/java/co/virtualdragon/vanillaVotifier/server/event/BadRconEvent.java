package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class BadRconEvent extends AbstractExceptionEvent {

	public BadRconEvent(Exception exception) {
		super(exception);
	}
}
