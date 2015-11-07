package co.virtualdragon.vanillaVotifier.impl.server.event;

public class SendingRconCommandEvent extends AbstractRconCommandEvent {

	public SendingRconCommandEvent(String command) {
		super(command);
	}
}
