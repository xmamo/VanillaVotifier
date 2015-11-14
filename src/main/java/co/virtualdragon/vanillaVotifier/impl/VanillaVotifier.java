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
import co.virtualdragon.vanillaVotifier.Config.RconConfig;
import co.virtualdragon.vanillaVotifier.LanguagePack;
import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.OutputWriter;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Server;
import co.virtualdragon.vanillaVotifier.Tester;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.exception.InvalidPrivateKeyFileException;
import co.virtualdragon.vanillaVotifier.exception.InvalidPublicKeyFileException;
import co.virtualdragon.vanillaVotifier.exception.PrivateKeyFileNotFoundException;
import co.virtualdragon.vanillaVotifier.exception.PublicKeyFileNotFoundException;
import java.io.File;
import java.net.BindException;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;
import org.json.JSONException;

public class VanillaVotifier implements Votifier {

	private final PropertiesLanguagePack languagePack;
	private final ConsoleOutputWriter outputWriter;
	private final JsonConfig config;
	private final RconCommandSender commandSender;
	private final VanillaVotifierServer server;
	private final ArrayList<Rcon> rcons;
	private final VanillaVotifierTester tester;

	{
		languagePack = new PropertiesLanguagePack("co/virtualdragon/vanillaVotifier/impl/lang/lang");
		outputWriter = new ConsoleOutputWriter(this);
		config = new JsonConfig(new File("config.json"));
		commandSender = new RconCommandSender();
		server = new VanillaVotifierServer(this);
		rcons = new ArrayList<Rcon>();
		tester = new VanillaVotifierTester(this);
	}

	public static void main(String[] args) {
		String[] javaVersion = System.getProperty("java.version").split("\\.");
		if (!(javaVersion.length >= 1 && Integer.parseInt(javaVersion[0]) >= 1 && javaVersion.length >= 2 && Integer.parseInt(javaVersion[1]) >= 6)) {
			System.out.println("You need at least Java 1.6 to run this program! Current version: " + System.getProperty("java.version") + ".");
			return;
		}
		final VanillaVotifier votifier = new VanillaVotifier();
		votifier.getOutputWriter().printlnTranslation("s42");
		if (!(loadConfig(votifier) && startServer(votifier))) {
			return;
		}
		Scanner in = new Scanner(System.in);
		while (true) {
			String command;
			try {
				command = in.nextLine();
			} catch (NoSuchElementException e) {
				// Can only happen at unexpected program interruption (i. e. CTRL-C). Ignoring.
				continue;
			}
			if (command.equalsIgnoreCase("stop") || command.toLowerCase().startsWith("stop ")) {
				if (command.split(" ").length == 1) {
					stopServer(votifier);
					break;
				} else {
					votifier.getOutputWriter().printlnTranslation("s17");
				}
			} else if (command.equalsIgnoreCase("restart") || command.toLowerCase().startsWith("restart ")) {
				if (command.split(" ").length == 1) {
					Listener listener = new Listener() {
						@Override
						public void onEvent(Event event, Votifier votifier) {
							if (event instanceof ServerStoppedEvent) {
								if (loadConfig(votifier) && startServer(votifier)) {
									votifier.getServer().getListeners().remove(this);
								} else {
									System.exit(0);
								}
							}
						}
					};
					votifier.getServer().getListeners().add(listener);
					if (!stopServer(votifier)) {
						System.exit(0); // "return" somehow isn't enough.
						return;
					}
				} else {
					votifier.getOutputWriter().printlnTranslation("s32");
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
						votifier.getOutputWriter().printlnTranslation("s19");
						continue;
					}
					if (keySize < 512) {
						votifier.getOutputWriter().printlnTranslation("s51");
						continue;
					}
					if (keySize > 16384) {
						votifier.getOutputWriter().printlnTranslation("s52");
						continue;
					}
				} else {
					votifier.getOutputWriter().printlnTranslation("s20");
					continue;
				}
				votifier.getOutputWriter().printlnTranslation("s16");
				votifier.getConfig().genKeyPair(keySize);
				try {
					votifier.getConfig().save();
				} catch (Exception e) {
					votifier.getOutputWriter().printlnTranslation("s21", new SimpleEntry<String, Object>("exception", e));
				}
				votifier.getOutputWriter().printlnTranslation("s23");
			} else if (command.equalsIgnoreCase("test-vote") || command.toLowerCase().startsWith("test-vote ")) {
				String[] commandArgs = command.split(" ");
				if (commandArgs.length == 2) {
					try {
						votifier.getTester().testVote(new VanillaVotifierVote("TesterService", commandArgs[1], votifier.getConfig().getInetSocketAddress().toString()));
					} catch (Exception e) {
						votifier.getOutputWriter().printlnTranslation("s27", new SimpleEntry<String, Object>("exception", e));
					}
				} else {
					votifier.getOutputWriter().printlnTranslation("s26");
				}
			} else if (command.equalsIgnoreCase("test-query") || command.toLowerCase().startsWith("test-query ")) {
				if (command.split(" ").length >= 2) {
					try {
						votifier.getTester().testQuery(command.replaceFirst("test-query ", "").replaceAll("\t", "\n"));
					} catch (Exception e) {
						votifier.getOutputWriter().printlnTranslation("s35", new SimpleEntry<String, Object>("exception", e));
					}
				} else {
					votifier.getOutputWriter().printlnTranslation("s34");
				}
			} else if (command.equalsIgnoreCase("help") || command.toLowerCase().startsWith("help ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().printlnTranslation("s31");
				} else {
					votifier.getOutputWriter().printlnTranslation("s32");
				}
			} else if (command.equalsIgnoreCase("manual") || command.toLowerCase().startsWith("manual ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().printlnTranslation("s36");
				} else {
					votifier.getOutputWriter().printlnTranslation("s37");
				}
			} else if (command.equalsIgnoreCase("info") || command.toLowerCase().startsWith("info ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().printlnTranslation("s40");
				} else {
					votifier.getOutputWriter().printlnTranslation("s41");
				}
			} else if (command.equalsIgnoreCase("license") || command.toLowerCase().startsWith("license ")) {
				if (command.split(" ").length == 1) {
					votifier.getOutputWriter().printlnTranslation("s43");
				} else {
					votifier.getOutputWriter().printlnTranslation("s44");
				}
			} else {
				votifier.getOutputWriter().printlnTranslation("s33");
			}

		}
	}

	private static boolean loadConfig(Votifier votifier) {
		votifier.getOutputWriter().printlnTranslation("s24");
		try {
			votifier.getConfig().load();
			votifier.getRcons().clear();
			for (RconConfig rconConfig : votifier.getConfig().getRconConfigs()) {
				votifier.getRcons().add(new RconConnection(rconConfig));
			}
			votifier.getOutputWriter().printlnTranslation("s25");
			return true;
		} catch (JSONException e) {
			votifier.getOutputWriter().printlnTranslation("s45", new SimpleEntry<String, Object>("exception", e.getMessage().replaceAll("'", "\"")));
		} catch (PublicKeyFileNotFoundException e) {
			votifier.getOutputWriter().printlnTranslation("s49");
		} catch (PrivateKeyFileNotFoundException e) {
			votifier.getOutputWriter().printlnTranslation("s50");
		} catch (InvalidPublicKeyFileException e) {
			votifier.getOutputWriter().printlnTranslation("s47");
		} catch (InvalidPrivateKeyFileException e) {
			votifier.getOutputWriter().printlnTranslation("s48");
		} catch (Exception e) {
			votifier.getOutputWriter().printlnTranslation("s15", new SimpleEntry<String, Object>("exception", e));
		}
		return false;
	}

	private static boolean startServer(Votifier votifier) {
		try {
			votifier.getServer().start();
			return true;
		} catch (BindException e) {
			votifier.getOutputWriter().printlnTranslation("s38", new SimpleEntry<String, Object>("port", votifier.getConfig().getInetSocketAddress().getPort()));
		} catch (Exception e) {
			votifier.getOutputWriter().printlnTranslation("s13", new SimpleEntry<String, Object>("exception", e));
		}
		return false;
	}

	private static boolean stopServer(Votifier votifier) {
		try {
			votifier.getServer().stop();
			return true;
		} catch (Exception e) {
			votifier.getOutputWriter().printlnTranslation("s12", new SimpleEntry<String, Object>("exception", e));
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
	public List<Rcon> getRcons() {
		return rcons;
	}

	@Override
	public Tester getTester() {
		return tester;
	}
}
