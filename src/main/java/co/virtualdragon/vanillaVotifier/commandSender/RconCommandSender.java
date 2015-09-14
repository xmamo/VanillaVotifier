package co.virtualdragon.vanillaVotifier.commandSender;

import co.virtualdragon.vanillaVotifier.VanillaVotifier;
import java.net.SocketTimeoutException;
import net.sourceforge.rconed.Rcon;
import net.sourceforge.rconed.exception.BadRcon;
import net.sourceforge.rconed.exception.ResponseEmpty;

public class RconCommandSender implements CommandSender {
	
	private VanillaVotifier votifier;

	public RconCommandSender(VanillaVotifier votifier) {
		this.votifier = votifier;
	}
	
	@Override
	public String sendCommand(String command) throws SocketTimeoutException, BadRcon, ResponseEmpty {
		return Rcon.send(votifier.getConfig().getInetSocketAddress().getPort(), votifier.getConfig().getRconInetSocketAddress().getHostString(), votifier.getConfig().getRconInetSocketAddress().getPort(), votifier.getConfig().getRconPassword(), command);
	}
}
