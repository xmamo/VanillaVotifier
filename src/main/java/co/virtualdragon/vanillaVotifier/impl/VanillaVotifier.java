/* 
 * Copyright (C) 2015 VirtualDragon
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.CommandSender;
import co.virtualdragon.vanillaVotifier.Config;
import co.virtualdragon.vanillaVotifier.LanguagePack;
import co.virtualdragon.vanillaVotifier.OutputWriter;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Server;
import co.virtualdragon.vanillaVotifier.Tester;
import co.virtualdragon.vanillaVotifier.Votifier;
import java.io.File;
import java.net.BindException;
import java.net.SocketOptions;
import java.util.HashMap;
import java.util.Scanner;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;
import org.json.JSONException;

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
		String[] javaVersion = System.getProperty("java.version").split("\\.");
		if (!(javaVersion.length >= 1 && Integer.parseInt(javaVersion[0]) >= 1 && javaVersion.length >= 2 && Integer.parseInt(javaVersion[1]) >= 6)) {
			System.err.println("You need at least Java 1.6 to run this program! Current version: " + System.getProperty("java.version") + ".");
			return;
		}
		VanillaVotifier votifier = new VanillaVotifier();
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s42"));
		if (!(loadConfig(votifier) && startServer(votifier))) {
			return;
		}
		Scanner in = new Scanner(System.in);
		while (true) {
			String command = in.nextLine();
			if (command.equalsIgnoreCase("stop") || command.toLowerCase().startsWith("stop ")) {
				if (command.split(" ").length == 1) {
					stopServer(votifier);
					break;
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s17"));
				}
			} else if (command.equalsIgnoreCase("restart") || command.toLowerCase().startsWith("restart ")) {
				if (command.split(" ").length == 1) {
					if (!stopServer(votifier)) {
						System.exit(0); // "return" somehow isn't enough.
						return;
					}
					try {
						Thread.sleep(SocketOptions.SO_TIMEOUT);
					} catch (InterruptedException e) {
						// Can't happen.
					}
					if (!(loadConfig(votifier) && startServer(votifier))) {
						System.exit(0); // "return" somehow isn't enough.
						return;
					}
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
				if (command.split(" ").length >= 2) {
					try {
						votifier.getTester().testQuery(command.replaceFirst("test-query ", "").replaceAll("\t", "\n"));
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
			} else if (command.equalsIgnoreCase("manual") || command.toLowerCase().startsWith("manual ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s36"));
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s37"));
				}
			} else if (command.equalsIgnoreCase("info") || command.toLowerCase().startsWith("info ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s40"));
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s41"));
				}
			} else if (command.equalsIgnoreCase("license") || command.toLowerCase().startsWith("license ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s43"));
				} else {
					votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s44"));
				}
			} else {
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s33"));
			}
		}
	}

	private static boolean loadConfig(Votifier votifier) {
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s24"));
		try {
			votifier.getConfig().load();
		} catch (JSONException e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", e.getMessage().replaceAll("'", "\""));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s45")));
			return false;
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s15")));
			return false;
		}
		votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s25"));
		return true;
	}

	private static boolean stopServer(Votifier votifier) {
		try {
			votifier.getServer().stop();
			return true;
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s12")));
		}
		return false;
	}

	private static boolean startServer(Votifier votifier) {
		try {
			votifier.getServer().start();
			return true;
		} catch (BindException e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("port", votifier.getConfig().getInetSocketAddress().getPort() + "");
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s38")));
		} catch (Exception e) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(e));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s13")));
		}
		return false;
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
