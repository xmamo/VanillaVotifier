package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.event.AbstractExceptionEvent;

public class ServerCloseExceptionEvent extends AbstractExceptionEvent {

	public ServerCloseExceptionEvent(Exception exception) {
		super(exception);
	}
}
