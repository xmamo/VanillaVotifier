package co.virtualdragon.vanillaVotifier;

import co.virtualdragon.vanillaVotifier.commandSender.CommandSender;
import co.virtualdragon.vanillaVotifier.commandSender.RconCommandSender;
import co.virtualdragon.vanillaVotifier.config.Config;
import co.virtualdragon.vanillaVotifier.config.JsonConfig;
import co.virtualdragon.vanillaVotifier.languagePack.LanguagePack;
import co.virtualdragon.vanillaVotifier.languagePack.PropertiesLanguagePack;
import co.virtualdragon.vanillaVotifier.outputWriter.ConsoleOutputWriter;
import co.virtualdragon.vanillaVotifier.outputWriter.OutputWriter;
import co.virtualdragon.vanillaVotifier.server.Server;
import co.virtualdragon.vanillaVotifier.server.VanillaVotifierServer;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifierStandalone implements VanillaVotifier {

	private PropertiesLanguagePack languagePack;
	private ConsoleOutputWriter outputWriter;
	private JsonConfig config;
	private RconCommandSender commandSender;
	private VanillaVotifierServer server;

	{
		languagePack = new PropertiesLanguagePack("co/virtualdragon/vanillaVotifier/lang/lang");
		outputWriter = new ConsoleOutputWriter();
		config = new JsonConfig(new File("config.json"));
		commandSender = new RconCommandSender(this);
		server = new VanillaVotifierServer(this);
	}

	public static void main(String[] args) {
		VanillaVotifierStandalone votifier = new VanillaVotifierStandalone();
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s24"));
		try {
			votifier.getConfig().load();
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", e.getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s15")));
			return;
		}
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s25"));
		try {
			votifier.getServer().start();
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", e.getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s13")));
		}
		Scanner in = new Scanner(System.in);
		while (true) {
			String command = in.nextLine();
			if (command.equalsIgnoreCase("stop") || command.toLowerCase().startsWith("stop ")) {
				if (command.split(" ").length == 1) {
					try {
						votifier.getServer().stop();
					} catch (Exception e) {
						HashMap<String, String> substitutions = new HashMap<String, String>();
						substitutions.put("exception", e.getMessage());
						votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s12")));
					}
					break;
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s17"));
				}
			} else if (command.equalsIgnoreCase("gen-key-pair") || command.startsWith("gen-key-pair ")) {
				String[] commandArgs = command.split(" ");
				int keySize;
				if (commandArgs.length == 1) {
					keySize = 2048;
				} else if (commandArgs.length == 2) {
					try {
						keySize = Integer.parseInt(commandArgs[1]);
					} catch (Exception e) {
						votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s19"));
						continue;
					}
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s20"));
					continue;
				}
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s16"));
				try {
					votifier.getConfig().genKeyPair(keySize);
				} catch (Exception e) {
					HashMap<String, String> substitutions = new HashMap<String, String>();
					substitutions.put("exception", e.getMessage());
					votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s18")));
				}
				try {
					votifier.getConfig().save();
				} catch (Exception e) {
					HashMap<String, String> substitutions = new HashMap<String, String>();
					substitutions.put("exception", e.getMessage());
					votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s21")));
				}
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s23"));
			}
		}
	}

	@Override
	public LanguagePack getLanguagePack() {
		return languagePack;
	}

	@Override
	public OutputWriter getOutputWriter() {
		return outputWriter;
	}

	@Override
	public Config getConfig() {
		return config;
	}

	@Override
	public CommandSender getCommandSender() {
		return commandSender;
	}

	@Override
	public Server getServer() {
		return server;
	}
}
