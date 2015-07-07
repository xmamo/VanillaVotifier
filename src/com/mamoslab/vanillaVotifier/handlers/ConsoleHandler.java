package com.mamoslab.vanillaVotifier.handlers;

import com.mamoslab.vanillaVotifier.Utils;
import com.mamoslab.vanillaVotifier.VanillaVotifier;
import java.net.Socket;
import java.sql.Timestamp;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;

public class ConsoleHandler {

	private Logger LOGGER;
	private volatile boolean running;

	public ConsoleHandler() {
		LOGGER = Logger.getLogger(VanillaVotifier.class.getName());
		LOGGER.setLevel(Level.ALL);
	}

	public void start() {
		if (running) {
			return;
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				running = true;
				Scanner in = new Scanner(System.in);
				while (running) {
					String command = in.nextLine();
					if (command.equalsIgnoreCase("stop")) {
						VanillaVotifier.getConnectionHandler().stop();
						stop();
					} else if (command.equalsIgnoreCase("restart")) {
						VanillaVotifier.getConnectionHandler().stop();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// Can't happen
						}
						if (!VanillaVotifier.getConfigHandler().load() || !VanillaVotifier.getConnectionHandler().start()) {
							System.exit(0);
						}
					} else if (command.equalsIgnoreCase("genrsakeys")) {
						LOGGER.info("Generating new RSA keys...");
						VanillaVotifier.getConfigHandler().genVotifierRSAKeys();
						VanillaVotifier.getConfigHandler().save();
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							// Can't happen
						}
						LOGGER.info("RSA keys generated.");
					} else if (command.toLowerCase().startsWith("testmsg")) {
						if (!command.toLowerCase().startsWith("testmsg ")) {
							System.out.println("Wrong arguments! Correct usage: testmsg <message>.");
							continue;
						}
						testMsg(command.substring("testmsg ".length()).replaceAll("\t", "\n"));
					} else if (command.toLowerCase().startsWith("testvote")) {
						if (!command.toLowerCase().startsWith("testvote ")) {
							System.out.println("Wrong arguments! Correct usage: testvote <IGN>.");
							continue;
						}
						testMsg("VOTE\nTEST\n" + command.substring("testvote ".length()) + "\n" + VanillaVotifier.getConfigHandler().getVotifierIp() + "\n" + new Timestamp(System.currentTimeMillis()));
					} else if (command.equalsIgnoreCase("help")) {
						System.out.println(""
								+ "============================================ AVAIABLE COMMANDS ============================================" + System.lineSeparator()
								+ "genrsakeys\t\tGenerates new RSA keys for the Vanilla votifier server." + System.lineSeparator()
								+ "help\t\t\tShows all avaiable commands with usage explanation." + System.lineSeparator()
								+ "manual\t\t\tDisplays a detailed manual of Vanilla votifier." + System.lineSeparator()
								+ "restart\t\t\tRestarts the Vanilla votifier server." + System.lineSeparator()
								+ "stop\t\t\tStops the Vanilla votifier server." + System.lineSeparator()
								+ "testmsg <message>\tSends an enciphered to the Vanilla votifier itself. For debugging purposes only." + System.lineSeparator()
								+ "testvote <IGN>\t\tSends a vote as <IGN> to the Vanilla votifier itself. For debugging purposes only." + System.lineSeparator()
								+ "===========================================================================================================");
					} else if (command.equalsIgnoreCase("manual")) {
						System.out.println(""
								+ "==================================== MANUAL ====================================" + System.lineSeparator()
								+ "This is the Vanilla votifier manual. Reading this is recommended if it's the" + System.lineSeparator()
								+ "first time you're using Vanilla votifier." + System.lineSeparator()
								+ System.lineSeparator()
								+ "--------------------------------- CONFIGURATION --------------------------------" + System.lineSeparator()
								+ "The configuration for Vanilla votifier is automatically generated if not" + System.lineSeparator()
								+ "present and located in the same folder you started the program from, with name" + System.lineSeparator()
								+ "\"votifier.properties\". Since the configuration file gets read by Vanilla" + System.lineSeparator()
								+ "votifier only on startup, you may use the \"restart\" command to force the" + System.lineSeparator()
								+ "program to reload the settings." + System.lineSeparator()
								+ "Here is a short description of all settings you can configure in the file." + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"config-version\" is simply the version of the config. If Vanilla votifier gets" + System.lineSeparator()
								+ "updated, it can rely on this setting to see if the config is outdated. Please" + System.lineSeparator()
								+ "don't touch this setting!" + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"votifier.ip\" and \"votifier.port\" are the IP and the port you want Vanilla" + System.lineSeparator()
								+ "votifier to run on. Nothing special here." + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"votifier.public-rsa-key\" and \"votifier.private-rsa-key\" are the public and" + System.lineSeparator()
								+ "private RSA keys Vanilla votifier needs to work (for more information, please" + System.lineSeparator()
								+ "read the \"COMMANDS\" section). Best thing would be to leave the default ones or" + System.lineSeparator()
								+ "to generate new ones through the \"genrsakeys\" command. Otherwise, you can lookup" + System.lineSeparator()
								+ "some tools on-line to generate some RSA keys for you." + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"votifier.on-vote.mc-script\" are the Minecraft commands Vanilla votifier has to" + System.lineSeparator()
								+ "execute through RCon when somebody votes for your server. You can insert more" + System.lineSeparator()
								+ "than one command by separating them with a tabulation (\"\t\"). \"%1$s\"," + System.lineSeparator()
								+ "\"%2$s\", \"%3$s\", \"%4$s\" will be replaced with the value of the service the user" + System.lineSeparator()
								+ "has voted on, his IGN, address, and the time stamp he has voted on (for more" + System.lineSeparator()
								+ "information, please read the \"COMMANDS\" section)." + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"mc-rcon.ip\" and \"mc-rcon.port\" are the IP and the port of the RCon-enabled" + System.lineSeparator()
								+ "Minecraft server you want Vanilla votifier to send commands to. Again, nothing" + System.lineSeparator()
								+ "special here." + System.lineSeparator()
								+ System.lineSeparator()
								+ "\"mc-rcon.password\" is the password Votifier has to utilize to connect to Rcon." + System.lineSeparator()
								+ System.lineSeparator()
								+ "----------------------------------- COMMANDS -----------------------------------" + System.lineSeparator()
								+ "To start out, there are two really basic and simple commands: \"stop\" and" + System.lineSeparator()
								+ "\"restart\". There's not much to say here, they're pretty self-explanatory: \"stop\"" + System.lineSeparator()
								+ "stops the Vanilla votifier server, while \"restart\" restarts it." + System.lineSeparator()
								+ System.lineSeparator()
								+ "A more advanced command is \"genrsakeys\". All votifier sites and servers (like" + System.lineSeparator()
								+ "this one) use RSA, basically a way to send data securely between hosts. To" + System.lineSeparator()
								+ "work, RSA needs to have two keys, a public and a private one. It's not really" + System.lineSeparator()
								+ "important to know what this keys are or what they do, but it's important to know" + System.lineSeparator()
								+ "that they are needed to make votifier programs work. When you first start" + System.lineSeparator()
								+ "Vanilla votifier, the two RSA keys are generated for you, but if you want to" + System.lineSeparator()
								+ "generate new ones, use the command \"genrsakeys\"." + System.lineSeparator()
								+ System.lineSeparator()
								+ "Lastly, there are two debugging comands: \"testvote\" and \"testmsg\". Basically," + System.lineSeparator()
								+ "whenever someone votes for your server, a string of data is sent to the votifier" + System.lineSeparator()
								+ "server. The string of data looks something like this:" + System.lineSeparator()
								+ "\"VOTE	serviceName	username	address	timeStamp\". Instead of" + System.lineSeparator()
								+ "\"serviceName\" there will be the name of the site the user has voted on (i. e." + System.lineSeparator()
								+ "MCSL), \"username\", \"address\" and \"timeStamp\" will be replaced with the IGN of" + System.lineSeparator()
								+ "the user who has voted, his IP address, and the time stamp he has voted on." + System.lineSeparator()
								+ "Let's pretend that Notch has voted for our server on MCSL the day 06/07/2015 on" + System.lineSeparator()
								+ "22:40:16, and that his IP is 111.23.8.52: we could emulate that through" + System.lineSeparator()
								+ "\"testmsg VOTE	My service	Notch	127.0.0.1	2015-07-06 22:40:16\"." + System.lineSeparator()
								+ "To make things easier, a similar result can be achieved with \"testvote Notch\"." + System.lineSeparator()
								+ "================================================================================"
						);
					} else {
						System.out.println("\"" + command + "\" is not a valid command! Please type \"help\" for a list of commands.");
					}
				}
			}
		}).start();
	}

	private void testMsg(String message) {
		Cipher cipher;
		try {
			cipher = Utils.getEncryptCipher();
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while initializing RSA cipher: " + e.getMessage());
			return;
		}

		Socket socket;
		try {
			socket = new Socket(VanillaVotifier.getConfigHandler().getVotifierIp(), VanillaVotifier.getConfigHandler().getVotifierPort());
		} catch (Exception ex) {
			LOGGER.warning("Couldn't set up socket connection to " + VanillaVotifier.getConfigHandler().getVotifierIp() + ":" + VanillaVotifier.getConfigHandler().getVotifierPort() + "!");
			return;
		}

		try {
			socket.getOutputStream().write(cipher.doFinal(message.getBytes()));
			socket.getOutputStream().flush();
		} catch (IllegalBlockSizeException e) {
			if (e.getMessage().startsWith("Data must not be longer than")) {
				LOGGER.warning("Input string too long!");
			} else {
				LOGGER.warning("Unexpected exception while encrypting messgae: " + e.getMessage());
			}
			return;
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while encrypting messgae: " + e.getMessage());
			return;
		}

		try {
			socket.close();
		} catch (Exception e) {
			// Don't care
		}
	}

	public void stop() {
		running = false;
	}
}
