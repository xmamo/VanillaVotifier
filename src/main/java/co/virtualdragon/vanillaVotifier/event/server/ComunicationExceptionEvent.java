package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class ComunicationExceptionEvent extends AbstractExceptionEvent {

	public ComunicationExceptionEvent(Exception exception) {
		super(exception);
	}
}
