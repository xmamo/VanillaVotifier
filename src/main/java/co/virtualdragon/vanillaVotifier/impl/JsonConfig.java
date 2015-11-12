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
	private ArrayList<RconConfig> rconConfigs;

	public JsonConfig(File configFile) {
		this.configFile = configFile;
	}

	@Override
	public void load() throws Exception {
		if (!configFile.exists()) {
			BufferedInputStream in = new BufferedInputStream(JsonConfig.class.getResourceAsStream("config.json"));
			BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(configFile));
			int i;
			while ((i = in.read()) != -1) {
				if (i != '\n') {
					out.write(i);
				} else {
					for (int j = 0; j < System.lineSeparator().length(); j++) {
						out.write(System.lineSeparator().charAt(j));
					}
				}
			}
			out.flush();
			out.close();
		}
		BufferedInputStream in = new BufferedInputStream(JsonConfig.class.getResourceAsStream("config.json"));
		JSONObject defaultConfig = new JSONObject(new JSONTokener(in));
		in.close();
		JSONObject config = new JSONObject(new JSONTokener(new BufferedInputStream(new FileInputStream(configFile))));
		boolean save = JsonUtils.merge(defaultConfig, config);
		configVersion = config.getInt("config-version");
		if (configVersion == 2) {
			v2ToV3(config);
			configVersion = 3;
			save = true;
		}
		inetSocketAddress = new InetSocketAddress(config.getString("ip"), config.getInt("port"));
		publicKeyFile = new File(config.getJSONObject("key-pair-files").getString("public"));
		privateKeyFile = new File(config.getJSONObject("key-pair-files").getString("private"));
		if (!publicKeyFile.exists() && !privateKeyFile.exists()) {
			KeyPair keyPair = RsaUtils.genKeyPair();
			PemWriter publicPemWriter = new PemWriter(new BufferedWriter(new FileWriter(publicKeyFile)));
			publicPemWriter.writeObject(new PemObject("PUBLIC KEY", keyPair.getPublic().getEncoded()));
			publicPemWriter.flush();
			publicPemWriter.close();
			PemWriter privatePemWriter = new PemWriter(new BufferedWriter(new FileWriter(privateKeyFile)));
			privatePemWriter.writeObject(new PemObject("RSA PRIVATE KEY", keyPair.getPrivate().getEncoded()));
			privatePemWriter.flush();
			privatePemWriter.close();
		}
		if (!publicKeyFile.exists()) {
			throw new Exception("Can't find public key file!");
		}
		if (!privateKeyFile.exists()) {
			throw new Exception("Can't find private key file!");
		}
		PemReader publicKeyPemReader = new PemReader(new BufferedReader(new FileReader(publicKeyFile)));
		PemReader privateKeyPemReader = new PemReader(new BufferedReader(new FileReader(privateKeyFile)));
		PemObject publicPemObject = publicKeyPemReader.readPemObject();
		if (publicPemObject == null) {
			throw new Exception("Invalid public key file!");
		}
		PemObject privatePemObject = privateKeyPemReader.readPemObject();
		if (privatePemObject == null) {
			throw new Exception("Invalid private key file!");
		}
		keyPair = new KeyPair(RsaUtils.bytesToPublicKey(publicPemObject.getContent()), RsaUtils.bytesToPrivateKey(privatePemObject.getContent()));
		publicKeyPemReader.close();
		privateKeyPemReader.close();
		rconConfigs = new ArrayList<RconConfig>();
		for (int i = 0; i < config.getJSONArray("rcon-list").length(); i++) {
			JSONObject jsonObject = config.getJSONArray("rcon-list").getJSONObject(i);
			JsonRconConfig rconConfig = new JsonRconConfig(new InetSocketAddress(jsonObject.getString("ip"), jsonObject.getInt("port")), jsonObject.getString("password"));
			for (int j = 0; j < jsonObject.getJSONArray("commands").length(); j++) {
				rconConfig.getCommands().add(jsonObject.getJSONArray("commands").getString(j));
			}
			rconConfigs.add(rconConfig);
		}
		loaded = true;
		if (save) {
			save();
		}
	}

	private void v2ToV3(JSONObject jsonObject) {
		if (!jsonObject.has("commands")) {
			jsonObject.put("commands", new JSONArray());
		}
		if (!jsonObject.has("rcon-list")) {
			jsonObject.put("rcon-list", new JSONArray() {
				{
					put(new JSONObject());
				}
			});
		}
		jsonObject.getJSONArray("rcon-list").put(jsonObject.get("rcon-list"));
		jsonObject.getJSONArray("rcon-list").getJSONObject(0).put("commands", jsonObject.get("commands"));
		jsonObject.remove("commands");
		jsonObject.put("config-version", 3);
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
		genKeyPair(2048);
	}

	@Override
	public void genKeyPair(int keySize) {
		setKeyPair(RsaUtils.genKeyPair(keySize));
	}

	@Override
	public List<RconConfig> getRconConfigs() {
		return rconConfigs;
	}

	@Override
	public void save() throws IOException {
		checkState();
		JSONObject config = new JSONObject();
		config.put("config-version", getConfigVersion());
		config.put("ip", getInetSocketAddress().getHostString());
		config.put("port", getInetSocketAddress().getPort());
		config.put("key-pair-files", new JSONObject() {
			{
				put("public", getPublicKeyFile().getPath());
				put("private", getPrivateKeyFile().getPath());
			}
		});
		config.put("rcon-list", new JSONArray() {
			{
				for (final RconConfig rconConfig : rconConfigs) {
					put(new JSONObject() {
						{
							put("ip", rconConfig.getInetSocketAddress().getHostString());
							put("port", rconConfig.getInetSocketAddress().getPort());
							put("password", rconConfig.getPassword());
							put("commands", rconConfig.getCommands());
						}
					});
				}
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
		publicPemWriter.writeObject(new PemObject("PUBLIC KEY", getKeyPair().getPublic().getEncoded()));
		publicPemWriter.flush();
		publicPemWriter.close();
		PemWriter privatePemWriter = new PemWriter(new BufferedWriter(new FileWriter(getPrivateKeyFile())));
		privatePemWriter.writeObject(new PemObject("RSA PRIVATE KEY", getKeyPair().getPrivate().getEncoded()));
		privatePemWriter.flush();
		privatePemWriter.close();
	}

	public static class JsonRconConfig extends AbstractRconConfig {

		public JsonRconConfig(InetSocketAddress inetSocketAddress, String password) {
			super(inetSocketAddress, password);
		}

		public JsonRconConfig(InetSocketAddress inetSocketAddress, String password, ArrayList<String> commands) {
			super(inetSocketAddress, password, commands);
		}
	}
}
