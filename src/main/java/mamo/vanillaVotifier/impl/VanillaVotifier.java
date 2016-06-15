/*
 * Copyright (C) 2016  Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mamo.vanillaVotifier.impl;

import mamo.vanillaVotifier.*;
import mamo.vanillaVotifier.event.Event;
import mamo.vanillaVotifier.event.server.ServerStoppedEvent;
import mamo.vanillaVotifier.exception.InvalidPrivateKeyFileException;
import mamo.vanillaVotifier.exception.InvalidPublicKeyFileException;
import mamo.vanillaVotifier.exception.PrivateKeyFileNotFoundException;
import mamo.vanillaVotifier.exception.PublicKeyFileNotFoundException;
import org.json.JSONException;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.BindException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

public class VanillaVotifier implements Votifier {
	private final PropertiesLanguagePack languagePack;
	private final VanillaVotifierLogger logger;
	private final File configFile;
	private final JsonConfig config;
	private final RconCommandSender commandSender;
	private final VanillaVotifierServer server;
	private final ArrayList<Rcon> rcons;
	private final VanillaVotifierTester tester;

	private boolean reportExceptions;

	{
		languagePack = new PropertiesLanguagePack("mamo/vanillaVotifier/impl/lang/lang");
		configFile = new File("config.json");
		config = new JsonConfig(configFile);
		logger = new VanillaVotifierLogger(this);
		commandSender = new RconCommandSender();
		server = new VanillaVotifierServer(this);
		rcons = new ArrayList<Rcon>();
		tester = new VanillaVotifierTester(this);
	}

	public VanillaVotifier() {
		this(false);
	}

	public VanillaVotifier(boolean reportExceptions) {
		this.reportExceptions = reportExceptions;
	}

	public static void main(String[] args) {
		String[] javaVersion = System.getProperty("java.version").split("\\.");
		if (!(javaVersion.length >= 1 && Integer.parseInt(javaVersion[0]) >= 1 && javaVersion.length >= 2 && Integer.parseInt(javaVersion[1]) >= 6)) {
			System.out.println(("You need at least Java 1.6 to run this program! Current version: " + System.getProperty("java.version") + "."));
			return;
		}

		VanillaVotifier votifier = new VanillaVotifier();
		for (String arg : args) {
			if (arg.equalsIgnoreCase("-report-exceptions")) {
				votifier.reportExceptions = true;
			} else if (arg.equalsIgnoreCase("-help")) {
				votifier.getLogger().printlnTranslation("s58");
				return;
			} else {
				votifier.getLogger().printlnTranslation("s55", new SimpleEntry<String, Object>("option", arg));
				return;
			}
		}
		votifier.getLogger().printlnTranslation("s42");
		if (!(loadConfig(votifier) && startServer(votifier))) {
			return;
		}
		Scanner in = new Scanner(System.in);

		while (true) {
			String command;
			try {
				command = in.nextLine();
			} catch (NoSuchElementException e) {
				// NoSuchElementException: Can only happen at unexpected program interruption (i. e. CTRL+C). Ignoring.
				continue;
			} catch (Exception e) {
				votifier.getLogger().printlnTranslation("s57", new SimpleEntry<String, Object>("exception", e));
				if (!stopServer(votifier)) {
					System.exit(0); // "return" somehow isn't enough.
				}
				return;
			}
			if (command.equalsIgnoreCase("stop") || command.toLowerCase().startsWith("stop ")) {
				if (command.split(" ").length == 1) {
					stopServer(votifier);
					break;
				} else {
					votifier.getLogger().printlnTranslation("s17");
				}
			} else if (command.equalsIgnoreCase("restart") || command.toLowerCase().startsWith("restart ")) {
				if (command.split(" ").length == 1) {
					Listener listener = new Listener() {
						@Override
						public void onEvent(Event event, Votifier votifier) {
							if (event instanceof ServerStoppedEvent) {
								if (loadConfig((VanillaVotifier) votifier) && startServer((VanillaVotifier) votifier)) {
									votifier.getServer().getListeners().remove(this);
								} else {
									System.exit(0);
								}
							}
						}
					};
					votifier.getServer().getListeners().add(listener);
					if (!stopServer(votifier)) { // Kill the process if the server doesn't stop
						System.exit(0); // "return" somehow isn't enough.
						return;
					}
				} else {
					votifier.getLogger().printlnTranslation("s56");
				}
			} else if (command.equalsIgnoreCase("gen-key-pair") || command.startsWith("gen-key-pair ")) {
				String[] commandArgs = command.split(" ");
				int keySize;
				if (commandArgs.length == 1) {
					keySize = 2048;
				} else if (commandArgs.length == 2) {
					try {
						keySize = Integer.parseInt(commandArgs[1]);
					} catch (NumberFormatException e) {
						votifier.getLogger().printlnTranslation("s19");
						continue;
					}
					if (keySize < 512) {
						votifier.getLogger().printlnTranslation("s51");
						continue;
					}
					if (keySize > 16384) {
						votifier.getLogger().printlnTranslation("s52");
						continue;
					}
				} else {
					votifier.getLogger().printlnTranslation("s20");
					continue;
				}
				votifier.getLogger().printlnTranslation("s16");
				votifier.getConfig().genKeyPair(keySize);
				try {
					votifier.getConfig().save();
				} catch (Exception e) {
					votifier.getLogger().printlnTranslation("s21", new SimpleEntry<String, Object>("exception", e));
				}
				votifier.getLogger().printlnTranslation("s23");
			} else if (command.equalsIgnoreCase("test-vote") || command.toLowerCase().startsWith("test-vote ")) {
				String[] commandArgs = command.split(" ");
				if (commandArgs.length == 2) {
					try {
						votifier.getTester().testVote(new VanillaVotifierVote("TesterService", commandArgs[1], votifier.getConfig().getInetSocketAddress().getAddress().getHostName()));
					} catch (Exception e) { // GeneralSecurityException, IOException
						votifier.getLogger().printlnTranslation("s27", new SimpleEntry<String, Object>("exception", e));
					}
				} else {
					votifier.getLogger().printlnTranslation("s26");
				}
			} else if (command.equalsIgnoreCase("test-query") || command.toLowerCase().startsWith("test-query ")) {
				if (command.split(" ").length >= 2) {
					try {
						votifier.getTester().testQuery(command.replaceFirst("test-query ", "").replaceAll("\t", "\n"));
					} catch (Exception e) { // GeneralSecurityException, IOException
						votifier.getLogger().printlnTranslation("s35", new SimpleEntry<String, Object>("exception", e));
					}
				} else {
					votifier.getLogger().printlnTranslation("s34");
				}
			} else if (command.equalsIgnoreCase("help") || command.toLowerCase().startsWith("help ")) {
				if (command.split(" ").length == 1) {
					votifier.getLogger().printlnTranslation("s31");
				} else {
					votifier.getLogger().printlnTranslation("s32");
				}
			} else if (command.equalsIgnoreCase("manual") || command.toLowerCase().startsWith("manual ")) {
				if (command.split(" ").length == 1) {
					votifier.getLogger().printlnTranslation("s36");
				} else {
					votifier.getLogger().printlnTranslation("s37");
				}
			} else if (command.equalsIgnoreCase("info") || command.toLowerCase().startsWith("info ")) {
				if (command.split(" ").length == 1) {
					votifier.getLogger().printlnTranslation("s40");
				} else {
					votifier.getLogger().printlnTranslation("s41");
				}
			} else if (command.equalsIgnoreCase("license") || command.toLowerCase().startsWith("license ")) {
				if (command.split(" ").length == 1) {
					votifier.getLogger().printlnTranslation("s43");
				} else {
					votifier.getLogger().printlnTranslation("s44");
				}
			} else {
				votifier.getLogger().printlnTranslation("s33");
			}
		}
	}

	private static boolean loadConfig(VanillaVotifier votifier) {
		votifier.getLogger().printlnTranslation("s24");
		try {
			votifier.getConfig().load();
			votifier.getRcons().clear();
			for (Config.RconConfig rconConfig : votifier.getConfig().getRconConfigs()) {
				votifier.getRcons().add(new RconConnection(rconConfig));
			}
			votifier.getLogger().printlnTranslation("s25");
			return true;
		} catch (JSONException e) {
			votifier.getLogger().printlnTranslation("s45", new SimpleEntry<String, Object>("exception", e.getMessage().replaceAll("'", "\"")));
		} catch (PublicKeyFileNotFoundException e) {
			votifier.getLogger().printlnTranslation("s49");
		} catch (PrivateKeyFileNotFoundException e) {
			votifier.getLogger().printlnTranslation("s50");
		} catch (InvalidPublicKeyFileException e) {
			votifier.getLogger().printlnTranslation("s47");
		} catch (InvalidPrivateKeyFileException e) {
			votifier.getLogger().printlnTranslation("s48");
		} catch (FileNotFoundException e) {
			if (votifier.configFile.exists()) {
				votifier.getLogger().printlnTranslation("s18");
			} else {
				votifier.getLogger().printlnTranslation("s15", new SimpleEntry<String, Object>("exception", e));
			}
		} catch (Exception e) {
			votifier.getLogger().printlnTranslation("s15", new SimpleEntry<String, Object>("exception", e));
		}
		return false;
	}

	private static boolean startServer(VanillaVotifier votifier) {
		try {
			votifier.getServer().start();
			return true;
		} catch (BindException e) {
			votifier.getLogger().printlnTranslation("s38", new SimpleEntry<String, Object>("port", votifier.getConfig().getInetSocketAddress().getPort()));
		} catch (Exception e) {
			votifier.getLogger().printlnTranslation("s13", new SimpleEntry<String, Object>("exception", e));
		}
		return false;
	}

	private static boolean stopServer(VanillaVotifier votifier) {
		try {
			votifier.getServer().stop();
			return true;
		} catch (Exception e) {
			votifier.getLogger().printlnTranslation("s12", new SimpleEntry<String, Object>("exception", e));
		}
		return false;
	}

	@Override
	public LanguagePack getLanguagePack() {
		return languagePack;
	}

	@Override
	public Logger getLogger() {
		return logger;
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
	public List<Rcon> getRcons() {
		return rcons;
	}

	@Override
	public Tester getTester() {
		return tester;
	}

	@Override
	public boolean areExceptionsReported() {
		return reportExceptions;
	}
}