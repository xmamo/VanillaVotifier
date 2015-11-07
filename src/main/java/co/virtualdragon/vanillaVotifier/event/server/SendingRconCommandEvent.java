package co.virtualdragon.vanillaVotifier.event.server;

public class SendingRconCommandEvent implements RconCommandEvent {

	private final String command;

	public SendingRconCommandEvent(String command) {
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
