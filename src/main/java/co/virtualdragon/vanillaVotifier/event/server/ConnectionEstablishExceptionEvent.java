package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class ConnectionEstablishExceptionEvent extends AbstractExceptionEvent {

	public ConnectionEstablishExceptionEvent(Exception exception) {
		super(exception);
	}
}
