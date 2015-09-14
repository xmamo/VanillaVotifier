package co.virtualdragon.vanillaVotifier.server.event;

public abstract class AbstractRconCommandEvent implements RconCommandEvent {

	private String command;

	public AbstractRconCommandEvent(String command) {
		this.command = command;
	}

	@Override
	public String getCommand() {
		return command;
	}
}
