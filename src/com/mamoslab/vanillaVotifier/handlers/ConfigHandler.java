package com.mamoslab.vanillaVotifier.handlers;

import com.mamoslab.vanillaVotifier.Utils;
import com.mamoslab.vanillaVotifier.VanillaVotifier;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ConfigHandler {

	private static final Logger LOGGER;
	private File configFile;
	private Properties config;

	static {
		LOGGER = Logger.getLogger(ConfigHandler.class.getName());
		LOGGER.setLevel(Level.ALL);
	}

	public ConfigHandler(File settingsFile) {
		this.configFile = settingsFile;
	}

	public void load() {
		if (configFile.exists() && configFile.isDirectory()) {
			Logger.getLogger(VanillaVotifier.class.getName()).severe("There seems to be a directory named like the settings file Vanilla votifier would need! Please rename or delete it.");
			System.exit(0);
		}
		if (!configFile.exists()) {
			try {
				configFile.createNewFile();
			} catch (IOException ex) {
				LOGGER.severe("Couldn't access settings file!");
				System.exit(0);
			}
		}
		config = new Properties();
		try {
			config.load(new BufferedReader(new InputStreamReader(new FileInputStream(configFile), "UTF-8")));
		} catch (IOException ex) {
			LOGGER.severe("Couldn't access settings file!");
			System.exit(0);
		}
		boolean save = false;
		if (!config.containsKey("config-version")) {
			config.setProperty("config-version", "1");
			save = true;
		}
		if (!config.containsKey("votifier.ip")) {
			LOGGER.warning("\"votifier.ip\" not set: setting to default value.");
			config.setProperty("votifier.ip", "127.0.0.1");
			save = true;
		}
		if (!config.containsKey("votifier.port")) {
			LOGGER.warning("\"votifier.port\" not set: setting to default value.");
			config.setProperty("votifier.port", "8192");
			save = true;
		}
		if (!config.containsKey("votifier.public-rsa-key") || !config.containsKey("votifier.private-rsa-key")) {
			LOGGER.warning("RSA keys not set: generating.");
			genVotifierRSAKeys();
			save = true;
		}
		if (!config.containsKey("votifier.on-vote.mc-script")) {
			config.setProperty("votifier.on-vote.mc-script", "");
			save = true;
		}
		if (!config.containsKey("mc-rcon.ip")) {
			LOGGER.warning("\"mc-rcon.ip\" not set: setting to default value.");
			config.setProperty("mc-rcon.ip", "127.0.0.1");
			save = true;
		}
		if (!config.containsKey("mc-rcon.port")) {
			LOGGER.warning("\"mc-rcon.port\" not set: setting to default value.");
			config.setProperty("mc-rcon.port", "25575");
			save = true;
		}
		if (!config.containsKey("mc-rcon.password")) {
			config.setProperty("mc-rcon.password", "");
			save = true;
		}
		if (save) {
			save();
		}
		if (!config.containsKey("mc-rcon.password") || config.getProperty("mc-rcon.password") == null || config.getProperty("mc-rcon.password").isEmpty()) {
			LOGGER.severe("\"mc-rcon.password\" not set! Please configure it in the config!");
			System.exit(0);
		}
	}

	public void save() {
		try {
			config.store(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(configFile), "UTF-8")), null);
		} catch (Exception e) {
			LOGGER.warning("Unexpected exception while saving config: " + e.getMessage());
		}
	}

	public int getConfigigurationVersion() {
		try {
			return Integer.parseInt(config.getProperty("config-version"));
		} catch (NumberFormatException e) {
			LOGGER.severe("Inalid \"config-version\" setting: aborting!");
			System.exit(0);
			return -1;
		}
	}

	public String getVotifierIp() {
		String ip = config.getProperty("votifier.ip");
		if (ip == null || ip.isEmpty()) {
			LOGGER.severe("Inalid \"votifier.ip\" setting: aborting!");
			System.exit(0);
		}
		return ip;
	}

	public void setVotifierIp(String ip) {
		if (ip == null || ip.isEmpty()) {
			ip = "127.0.0.1";
		}
		config.setProperty("votifier.ip", ip);
		save();
	}

	public int getVotifierPort() {
		try {
			return Integer.parseInt(config.getProperty("votifier.port"));
		} catch (NumberFormatException e) {
			LOGGER.severe("Inalid \"votifier.port\" setting: aborting!");
			System.exit(0);
			return -1;
		}
	}

	public void setVotifierPort(int port) {
		config.setProperty("votifier.port", port + "");
		save();
	}

	public PublicKey getVotifierRSAPublicKey() {
		try {
			return Utils.stringToPublicKey(config.getProperty("votifier.public-rsa-key"));
		} catch (Exception e) {
			LOGGER.severe("Inalid \"votifier.public-rsa-key\" setting: aborting!");
			System.exit(0);
			return null;
		}
	}

	public PrivateKey getVotifierRSAPrivateKey() {
		try {
			return Utils.stringToPrivateKey(config.getProperty("votifier.private-rsa-key"));
		} catch (Exception e) {
			LOGGER.severe("Inalid \"votifier.private-rsa-key\" setting: aborting!");
			System.exit(0);
			return null;
		}
	}

	public void genVotifierRSAKeys() {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(2024);
			KeyPair keyPair = keyPairGenerator.generateKeyPair();
			config.setProperty("votifier.public-rsa-key", Utils.keyToString(keyPair.getPublic()));
			config.setProperty("votifier.private-rsa-key", Utils.keyToString(keyPair.getPrivate()));
			save();
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
	}
	
	public String getVotifierOnVoteMcScript() {
		return config.getProperty("votifier.on-vote.mc-script");
	}

	public String getMcRconIp() {
		String ip = config.getProperty("mc-rcon.ip");
		if (ip == null || ip.isEmpty()) {
			LOGGER.severe("Inalid \"mc-rcon.ip\" setting: aborting!");
			System.exit(0);
			return null;
		}
		return ip;
	}

	public void setMcRconIp(String ip) {
		if (ip == null || ip.isEmpty()) {
			ip = "127.0.0.1";
		}
		config.setProperty("mc-rcon.ip", ip);
		save();
	}

	public int getMcRconPort() {
		try {
			return Integer.parseInt(config.getProperty("mc-rcon.port"));
		} catch (NumberFormatException e) {
			LOGGER.severe("Inalid \"mc-rcon.port\" setting: aborting!");
			System.exit(0);
			return -1;
		}
	}

	public void setMcRconPort(int port) {
		config.setProperty("mc-rcon.port", port + "");
		save();
	}
	
	public String getMcRconPassword() {
		return config.getProperty("mc-rcon.password");
	}
	
	public void setMcRconPassword(String password) {
		config.setProperty("mc-rcon.password", password);
		save();
	}
}
