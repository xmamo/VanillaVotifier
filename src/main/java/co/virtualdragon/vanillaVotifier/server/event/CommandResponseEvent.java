package co.virtualdragon.vanillaVotifier.server.event;

public class CommandResponseEvent extends AbstractMessageEvent {

	public CommandResponseEvent(String message) {
		super(message);
	}
}
