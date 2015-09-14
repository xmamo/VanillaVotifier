package co.virtualdragon.vanillaVotifier;

import co.virtualdragon.vanillaVotifier.commandSender.CommandSender;
import co.virtualdragon.vanillaVotifier.config.Config;
import co.virtualdragon.vanillaVotifier.languagePack.LanguagePack;
import co.virtualdragon.vanillaVotifier.outputWriter.OutputWriter;
import co.virtualdragon.vanillaVotifier.server.Server;

public interface VanillaVotifier {
	
	LanguagePack getLanguagePack();
	
	OutputWriter getOutputWriter();

	Config getConfig();

	CommandSender getCommandSender();

	Server getServer();
}
