package co.virtualdragon.vanillaVotifier.server.event;

public class SendingRconCommandEvent extends AbstractRconCommandEvent {

	public SendingRconCommandEvent(String command) {
		super(command);
	}
}
