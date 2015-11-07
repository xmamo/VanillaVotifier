package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class ServerCloseExceptionEvent extends AbstractExceptionEvent {

	public ServerCloseExceptionEvent(Exception exception) {
		super(exception);
	}
}
