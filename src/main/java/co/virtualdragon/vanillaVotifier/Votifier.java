package co.virtualdragon.vanillaVotifier;

public interface Votifier {

	LanguagePack getLanguagePack();

	OutputWriter getOutputWriter();

	Config getConfig();

	CommandSender getCommandSender();

	Server getServer();
	
	Rcon getRcon();

	Tester getTester();
}
