package com.mamoslab.vanillaVotifier;

import com.mamoslab.vanillaVotifier.handlers.ConfigHandler;
import com.mamoslab.vanillaVotifier.handlers.ConnectionHandler;
import com.mamoslab.vanillaVotifier.handlers.ConsoleHandler;
import java.io.File;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

public class VanillaVotifier {

	private static final Logger LOGGER;

	private static ConfigHandler configHandler;
	private static ConnectionHandler connectionHandler;
	private static ConsoleHandler consoleHandler;

	static {
		Locale.setDefault(Locale.ENGLISH);
		System.setProperty("java.util.logging.SimpleFormatter.format", "[%4$s] %5$s" + System.lineSeparator());

		LOGGER = Logger.getLogger(VanillaVotifier.class.getName());
		LOGGER.setLevel(Level.ALL);

		configHandler = new ConfigHandler(new File("votifier.properties"));
		connectionHandler = new ConnectionHandler();
		consoleHandler = new ConsoleHandler();
	}

	public static void main(String[] args) {
		consoleHandler.start();
		
		if (!configHandler.load() || !connectionHandler.start()) {
			System.exit(0);
		}		
	}

	public static ConfigHandler getConfigHandler() {
		return configHandler;
	}

	public static ConnectionHandler getConnectionHandler() {
		return connectionHandler;
	}

	public static ConsoleHandler getConsoleHandler() {
		return consoleHandler;
	}
}
