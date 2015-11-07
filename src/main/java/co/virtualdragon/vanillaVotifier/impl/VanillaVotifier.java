package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.CommandSender;
import co.virtualdragon.vanillaVotifier.Config;
import co.virtualdragon.vanillaVotifier.LanguagePack;
import co.virtualdragon.vanillaVotifier.OutputWriter;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.server.Server;
import co.virtualdragon.vanillaVotifier.Tester;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.impl.server.VanillaVotifierServer;
import java.io.File;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifier implements Votifier {

	private final PropertiesLanguagePack languagePack;
	private final ConsoleOutputWriter outputWriter;
	private final JsonConfig config;
	private final RconCommandSender commandSender;
	private final VanillaVotifierServer server;
	private final RconConnection rcon;
	private final VanillaVotifierTester tester;

	{
		languagePack = new PropertiesLanguagePack("co/virtualdragon/vanillaVotifier/impl/lang/lang");
		outputWriter = new ConsoleOutputWriter();
		config = new JsonConfig(new File("config.json"));
		commandSender = new RconCommandSender(this);
		server = new VanillaVotifierServer(this);
		rcon = new RconConnection(this);
		tester = new VanillaVotifierTester(this);
	}

	public static void main(String[] args) {
		VanillaVotifier votifier = new VanillaVotifier();
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s24"));
		try {
			votifier.getConfig().load();
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s15")));
			return;
		}
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s25"));
		start(votifier);
		Scanner in = new Scanner(System.in);
		while (true) {
			String command = in.nextLine();
			if (command.equalsIgnoreCase("stop") || command.toLowerCase().startsWith("stop ")) {
				if (command.split(" ").length == 1) {
					stop(votifier);
					break;
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s17"));
				}
			} else if (command.equalsIgnoreCase("restart") || command.toLowerCase().startsWith("restart ")) {
				if (command.split(" ").length == 1) {
					stop(votifier);
					start(votifier);
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s32"));
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
					substitutions.put("exception", ExceptionUtils.getStackTrace(e));
					votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s18")));
				}
				try {
					votifier.getConfig().save();
				} catch (Exception e) {
					HashMap<String, String> substitutions = new HashMap<String, String>();
					substitutions.put("exception", ExceptionUtils.getStackTrace(e));
					votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s21")));
				}
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s23"));
			} else if (command.equalsIgnoreCase("test-vote") || command.toLowerCase().startsWith("test-vote ")) {
				String[] commandArgs = command.split(" ");
				if (commandArgs.length == 2) {
					try {
						votifier.getTester().testVote(new VanillaVotifierVote("TesterService", commandArgs[1], votifier.getConfig().getInetSocketAddress().toString()));
					} catch (Exception e) {
						HashMap<String, String> substitutions = new HashMap<String, String>();
						substitutions.put("exception", ExceptionUtils.getStackTrace(e));
						votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s27")));
					}
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s26"));
				}
			} else if (command.equalsIgnoreCase("test-query") || command.toLowerCase().startsWith("test-query ")) {
				String[] commandArgs = command.split(" ");
				if (commandArgs.length == 2) {
					try {
						votifier.getTester().testQuery(commandArgs[1].replaceAll("\t", "\n"));
					} catch (Exception e) {
						HashMap<String, String> substitutions = new HashMap<String, String>();
						substitutions.put("exception", ExceptionUtils.getStackTrace(e));
						votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s35")));
					}
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s34"));
				}
			} else if (command.equalsIgnoreCase("help") || command.toLowerCase().startsWith("help ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s31"));
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s32"));
				}
			} else if (command.equalsIgnoreCase("help") || command.toLowerCase().startsWith("manual ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s36"));
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s37"));
				}
			} else {
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s33"));
			}
		}
	}

	private static void stop(Votifier votifier) {
		try {
			votifier.getServer().stop();
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s12")));
		}
	}

	private static void start(Votifier votifier) {
		try {
			votifier.getServer().start();
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s13")));
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

	@Override
	public Rcon getRcon() {
		return rcon;
	}

	@Override
	public Tester getTester() {
		return tester;
	}
}
