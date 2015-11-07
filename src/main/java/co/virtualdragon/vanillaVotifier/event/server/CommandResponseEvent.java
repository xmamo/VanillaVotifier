package co.virtualdragon.vanillaVotifier.event.server;

public class CommandResponseEvent implements MessageEvent {

	private final String message;

	public CommandResponseEvent(String message) {
		this.message = message;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
