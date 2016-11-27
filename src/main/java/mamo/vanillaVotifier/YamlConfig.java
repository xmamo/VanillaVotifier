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

package mamo.vanillaVotifier;

import mamo.vanillaVotifier.utils.TimestampUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YamlConfig extends AbstractConfig {
	@SuppressWarnings("deprecation") @Nullable protected JsonConfig jsonConfig;

	public YamlConfig(@NotNull File configFile) {
		this(configFile, null);
	}

	public YamlConfig(@NotNull File configFile, @SuppressWarnings("deprecation") @Nullable JsonConfig jsonConfig) {
		super(configFile);
		this.jsonConfig = jsonConfig;
	}

	@Override
	public void load() throws IOException, InvalidKeySpecException {
		loadFromConfigFile();
		if (jsonConfig != null) {
			loadFromOldJsonConfig();
		}
	}

	protected void loadFromOldJsonConfig() throws IOException, InvalidKeySpecException {
		jsonConfig.load();
		inetSocketAddress = jsonConfig.getInetSocketAddress();
		keyPair = jsonConfig.getKeyPair();
		voteActions.clear();
		voteActions.addAll(jsonConfig.getVoteActions());
		save();
	}

	protected void loadFromConfigFile() throws IOException, InvalidKeySpecException {
		if (!configFile.exists()) {
			InputStream in = YamlConfig.class.getResourceAsStream("config.yaml");
			copyDefaultConfig(in);
			in.close();
		}

		BufferedReader reader = new BufferedReader(new FileReader(configFile));
		Map<String, Object> yamlConfig = (Map<String, Object>) new Yaml().load(reader);
		reader.close();

		configVersion = (Integer) yamlConfig.get("config-version");
		logDirectory = new File((String) yamlConfig.get("log-directory"));
		if (!logDirectory.exists()) {
			logDirectory.mkdirs();
		}
		logFile = new File(logDirectory, TimestampUtils.getTimestamp() + ".log");
		inetSocketAddress = new InetSocketAddress((String) ((Map) yamlConfig.get("server")).get("ip"), (Integer) ((Map) yamlConfig.get("server")).get("port"));
		publicKeyFile = new File((String) ((Map) yamlConfig.get("key-pair-files")).get("public"));
		privateKeyFile = new File((String) ((Map) yamlConfig.get("key-pair-files")).get("private"));
		loadKeyPair();
		voteActions.clear();
		for (Map<String, Object> commandSenderConfig : (List<Map<String, Object>>) yamlConfig.get("on-vote")) {
			if (((String) commandSenderConfig.get("action")).equalsIgnoreCase("rcon")) {
				Map<String, Object> server = (Map<String, Object>) commandSenderConfig.get("server");
				voteActions.add(new VoteAction(new RconCommandSender(new RconConnection(new InetSocketAddress((String) server.get("ip"), (Integer) server.get("port")), (String) server.get("password"))), (List<String>) commandSenderConfig.get("commands"), (HashMap<String, String>) commandSenderConfig.get("regex-replace")));
			} else if (((String) commandSenderConfig.get("action")).equalsIgnoreCase("shell")) {
				voteActions.add(new VoteAction(new ShellCommandSender(), (List<String>) commandSenderConfig.get("commands"), (HashMap<String, String>) commandSenderConfig.get("regex-replace")));
			}
		}
	}

	@Override
	public void save() throws IOException {
		HashMap<String, Object> yamlConfig = new HashMap<String, Object>();
		yamlConfig.put("config-version", getConfigVersion());
		yamlConfig.put("log-directory", getLogDirectory().getPath());
		yamlConfig.put("server", new HashMap<String, Object>() {{
			put("ip", getInetSocketAddress().getAddress().getHostName());
			put("port", getInetSocketAddress().getPort());
		}});
		yamlConfig.put("key-pair-files", new HashMap<String, Object>() {{
			put("public", getPublicKeyFile().getPath());
			put("private", getPrivateKeyFile().getPath());
		}});
		yamlConfig.put("on-vote", new ArrayList<HashMap<String, Object>>() {{
			for (final VoteAction voteAction : getVoteActions()) {
				add(new HashMap<String, Object>() {{
					if (voteAction.getCommandSender() instanceof RconCommandSender) {
						final RconCommandSender commandSender = (RconCommandSender) voteAction.getCommandSender();
						put("type", "rcon");
						put("server", new HashMap<String, Object>() {{
							put("ip", commandSender.getRconConnection().getInetSocketAddress().getAddress().getHostName());
							put("port", commandSender.getRconConnection().getInetSocketAddress().getPort());
							put("password", commandSender.getRconConnection().getPassword());
						}});
					}
					if (voteAction.getCommandSender() instanceof ShellCommandSender) {
						put("type", "shell");
					}
					put("commands", voteAction.getCommands());
				}});
			}
		}});

		BufferedWriter out = new BufferedWriter(new FileWriter(getConfigFile()));
		new Yaml().dump(yamlConfig, out);
		out.flush();
		out.close();
		saveKeyPair();
	}
}