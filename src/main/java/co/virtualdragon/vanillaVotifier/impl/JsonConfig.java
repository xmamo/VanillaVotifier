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

import co.virtualdragon.vanillaVotifier.Config;
import co.virtualdragon.vanillaVotifier.util.JsonUtils;
import co.virtualdragon.vanillaVotifier.util.RsaUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

public class JsonConfig implements Config {

	private final File configFile;

	private boolean loaded;
	private int configVersion;
	private InetSocketAddress inetSocketAddress;
	private File publicKeyFile;
	private File privateKeyFile;
	private KeyPair keyPair;
	private ArrayList<String> commands;
	private InetSocketAddress rconInetSocketAddress;
	private String rconPassword;

	public JsonConfig(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public void load() throws IOException, InvalidKeySpecException {
		if (!configFile.exists()) {
			BufferedInputStream in = new BufferedInputStream(JsonConfig.class.getResourceAsStream("config.json"));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
			int i;
			while ((i = in.read()) != -1) {
				out.write(i);
			}
			out.flush();
			out.close();
		}
		BufferedInputStream in = new BufferedInputStream(JsonConfig.class.getResourceAsStream("config.json"));
		JSONObject defaultConfig = new JSONObject(new JSONTokener(in));
		in.close();
		JSONObject config = new JSONObject(new JSONTokener(new BufferedInputStream(new FileInputStream(configFile))));
		boolean save = merge(defaultConfig, config);
		configVersion = config.getInt("config-version");
		inetSocketAddress = new InetSocketAddress(config.getString("ip"), config.getInt("port"));
		publicKeyFile = new File(config.getJSONObject("key-pair-files").getString("public"));
		privateKeyFile = new File(config.getJSONObject("key-pair-files").getString("private"));
		if (!publicKeyFile.exists() || !privateKeyFile.exists()) {
			KeyPair keyPair = RsaUtils.genKeyPair(2024);
			PemWriter publicPemWriter = new PemWriter(new BufferedWriter(new FileWriter(publicKeyFile)));
			publicPemWriter.writeObject(new PemObject("RSA PUBLIC KEY", keyPair.getPublic().getEncoded()));
			publicPemWriter.flush();
			publicPemWriter.close();
			PemWriter privatePemWriter = new PemWriter(new BufferedWriter(new FileWriter(privateKeyFile)));
			privatePemWriter.writeObject(new PemObject("RSA PUBLIC KEY", keyPair.getPrivate().getEncoded()));
			privatePemWriter.flush();
			privatePemWriter.close();
		}
		PemReader publicKeyPemReader = new PemReader(new BufferedReader(new FileReader(publicKeyFile)));
		PemReader privateKeyPemReader = new PemReader(new BufferedReader(new FileReader(privateKeyFile)));
		keyPair = new KeyPair(RsaUtils.bytesToPublicKey(publicKeyPemReader.readPemObject().getContent()), RsaUtils.bytesToPrivateKey(privateKeyPemReader.readPemObject().getContent()));
		publicKeyPemReader.close();
		privateKeyPemReader.close();
		commands = new ArrayList<String>();
		JSONArray commandsJson = config.getJSONArray("commands");
		for (int i = 0; i < commandsJson.length(); i++) {
			commands.add(commandsJson.getString(i));
		}
		rconInetSocketAddress = new InetSocketAddress(config.getJSONObject("rcon").getString("ip"), config.getJSONObject("rcon").getInt("port"));
		rconPassword = config.getJSONObject("rcon").getString("password");
		loaded = true;
		if (save) {
			save();
		}
	}

	private boolean merge(JSONObject from, JSONObject to) {
		boolean updated = false;
		for (Object keyObject : from.keySet()) {
			String key = (String) keyObject;
			if (!to.has(key)) {
				to.put(key, from.get(key));
				updated = true;
			}
			if (from.get(key) instanceof JSONObject) {
				if (merge(from.getJSONObject(key), to.getJSONObject(key))) {
					updated = true;
				}
			}
		}
		return updated;
	}

	private void checkState() {
		if (!isLoaded()) {
			throw new IllegalStateException("Config isn't loaded yet!");
		}
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public int getConfigVersion() {
		checkState();
		return configVersion;
	}

	@Override
	public InetSocketAddress getInetSocketAddress() {
		checkState();
		return inetSocketAddress;
	}

	@Override
	public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
		checkState();
		if (inetSocketAddress == null) {
			inetSocketAddress = new InetSocketAddress("127.0.0.1", 8192);
		}
		this.inetSocketAddress = inetSocketAddress;
	}

	@Override
	public File getPublicKeyFile() {
		checkState();
		return publicKeyFile;
	}

	@Override
	public void setPublicKeyFile(File location) {
		checkState();
		if (location == null) {
			location = new File("public.pem");
		}
		publicKeyFile = location;
	}

	@Override
	public File getPrivateKeyFile() {
		checkState();
		return privateKeyFile;
	}

	@Override
	public void setPrivateKeyFile(File location) {
		checkState();
		if (location == null) {
			location = new File("private.pem");
		}
		privateKeyFile = location;
	}

	@Override
	public KeyPair getKeyPair() {
		checkState();
		return keyPair;
	}

	@Override
	public void setKeyPair(KeyPair keyPair) {
		checkState();
		if (keyPair == null) {
			keyPair = RsaUtils.genKeyPair(2048);
		}
		this.keyPair = keyPair;
	}

	@Override
	public void genKeyPair() {
		checkState();
		genKeyPair(2048);
	}

	@Override
	public void genKeyPair(int keySize) {
		checkState();
		this.keyPair = RsaUtils.genKeyPair(keySize);
	}

	@Override
	public List<String> getCommands() {
		checkState();
		return commands;
	}

	@Override
	public void setCommands(List<String> commands) {
		checkState();
		if (commands == null) {
			commands = new ArrayList<String>();
		}
		this.commands.clear();
		this.commands.addAll(commands);
	}

	@Override
	public InetSocketAddress getRconInetSocketAddress() {
		checkState();
		return rconInetSocketAddress;
	}

	@Override
	public void setRconInetSocketAddress(InetSocketAddress inetSocketAddress) {
		checkState();
		if (inetSocketAddress == null) {
			inetSocketAddress = new InetSocketAddress("127.0.0.1", 25575);
		}
		rconInetSocketAddress = inetSocketAddress;
	}

	@Override
	public String getRconPassword() {
		checkState();
		return rconPassword;
	}

	@Override
	public void setRconPassword(String password) {
		checkState();
		rconPassword = password;
	}

	@Override
	public void save() throws IOException {
		checkState();
		JSONObject config = new JSONObject();
		config.put("config-version", getConfigVersion());
		config.put("ip", getInetSocketAddress().getHostString());
		config.put("port", getInetSocketAddress().getPort());
		config.put("key-pair-files", new HashMap<String, Object>() {
			{
				put("public", getPublicKeyFile().getPath());
				put("private", getPrivateKeyFile().getPath());
			}
		});
		config.put("commands", getCommands());
		config.put("rcon", new HashMap<String, Object>() {
			{
				put("ip", getRconInetSocketAddress().getHostString());
				put("port", getRconInetSocketAddress().getPort());
				put("password", getRconPassword());
			}
		});
		String configString = JsonUtils.jsonToPrettyString(config);
		BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
		for (char c : configString.toCharArray()) {
			out.write(c);
		}
		out.flush();
		out.close();
		PemWriter publicPemWriter = new PemWriter(new BufferedWriter(new FileWriter(getPublicKeyFile())));
		publicPemWriter.writeObject(new PemObject("RSA PUBLIC KEY", getKeyPair().getPublic().getEncoded()));
		publicPemWriter.flush();
		publicPemWriter.close();
		PemWriter privatePemWriter = new PemWriter(new BufferedWriter(new FileWriter(getPrivateKeyFile())));
		privatePemWriter.writeObject(new PemObject("RSA PUBLIC KEY", getKeyPair().getPrivate().getEncoded()));
		privatePemWriter.flush();
		privatePemWriter.close();
	}
}
