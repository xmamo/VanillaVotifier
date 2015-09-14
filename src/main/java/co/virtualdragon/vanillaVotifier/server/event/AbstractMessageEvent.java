package co.virtualdragon.vanillaVotifier.server.event;

public abstract class AbstractMessageEvent implements MessageEvent {

	private String message;

	public AbstractMessageEvent(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
