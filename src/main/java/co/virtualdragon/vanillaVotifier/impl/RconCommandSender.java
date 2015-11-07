package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.CommandSender;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Rcon.Packet;
import co.virtualdragon.vanillaVotifier.Votifier;
import java.net.SocketException;

public class RconCommandSender implements CommandSender {

	private final Votifier votifier;

	public RconCommandSender(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public String sendCommand(String command) throws Exception {
		if (!votifier.getRcon().isConnected()) {
			votifier.getRcon().connect();
		}
		Packet packet = null;
		try {
			packet = votifier.getRcon().sendRequest(new Rcon.Packet(votifier.getRcon().getRequestId(), Rcon.Packet.Type.LOG_IN, votifier.getConfig().getRconPassword()));
		} catch (SocketException e) {
			votifier.getRcon().connect();
			packet = votifier.getRcon().sendRequest(new Rcon.Packet(votifier.getRcon().getRequestId(), Rcon.Packet.Type.LOG_IN, votifier.getConfig().getRconPassword()));
		}
		if (packet.getRequestId() != -1) {
			return votifier.getRcon().sendRequest(new Rcon.Packet(votifier.getRcon().getRequestId(), Rcon.Packet.Type.COMMAND, command)).getPayload();
		} else {
			throw new Exception("Invalid password.");
		}
	}
}
