package co.virtualdragon.vanillaVotifier;

import co.virtualdragon.vanillaVotifier.server.Server;

public interface Votifier {

	LanguagePack getLanguagePack();

	OutputWriter getOutputWriter();

	Config getConfig();

	CommandSender getCommandSender();

	Server getServer();
	
	Rcon getRcon();

	Tester getTester();
}
