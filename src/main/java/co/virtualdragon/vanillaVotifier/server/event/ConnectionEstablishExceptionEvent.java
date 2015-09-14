package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class ConnectionEstablishExceptionEvent extends AbstractExceptionEvent {

	public ConnectionEstablishExceptionEvent(Exception exception) {
		super(exception);
	}
}
