package co.virtualdragon.vanillaVotifier.impl.server.event;

import co.virtualdragon.vanillaVotifier.server.event.MessageEvent;

public abstract class AbstractMessageEvent implements MessageEvent {

	private final String message;

	public AbstractMessageEvent(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
