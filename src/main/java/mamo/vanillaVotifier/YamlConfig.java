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
		Map config = (Map) new Yaml().load(reader);
		reader.close();

		boolean save = false;
		if (!config.containsKey("config-version")) {
			config.put("config-version", 4);
			save = true;
		}
		if (!config.containsKey("log-directory")) {
			config.put("log-directory", 4);
			save = true;
		}
		if (!config.containsKey("server")) {
			config.put("server", new HashMap());
			save = true;
		}
		Map serverSubconfig = (Map) config.get("server");
		if (!serverSubconfig.containsKey("ip")) {
			serverSubconfig.put("ip", "0.0.0.0");
			save = true;
		}
		if (!serverSubconfig.containsKey("port")) {
			serverSubconfig.put("port", 8192);
			save = true;
		}
		if (!config.containsKey("key-pair-files")) {
			config.put("key-pair-files", new HashMap());
			save = true;
		}
		Map keyPairFilesSubconfig = (Map) config.get("key-pair-files");
		if (!keyPairFilesSubconfig.containsKey("public")) {
			keyPairFilesSubconfig.put("public", "public.pem");
			save = true;
		}
		if (!keyPairFilesSubconfig.containsKey("private")) {
			keyPairFilesSubconfig.put("private", "private.pem");
			save = true;
		}
		if (!config.containsKey("on-vote")) {
			config.put("on-vote", new ArrayList<Map>());
			save = true;
		}
		List onVoteSubconfig = (List) config.get("on-vote");
		for (int i = 0; i < onVoteSubconfig.size(); i++) {
			Map voteActionSubconfig = (Map) onVoteSubconfig.get(i);
			if (voteActionSubconfig.containsKey("action")) {
				String voteActionType = (String) voteActionSubconfig.get("action");
				if (voteActionType.equalsIgnoreCase("rcon")) {
					if (!voteActionSubconfig.containsKey("server")) {
						voteActionSubconfig.put("server", new HashMap());
						save = true;
					}
					Map voteActionServerSubconfig = (Map) voteActionSubconfig.get("server");
					if (!voteActionServerSubconfig.containsKey("ip")) {
						voteActionServerSubconfig.put("ip", "0.0.0.0");
						save = true;
					}
					if (!voteActionServerSubconfig.containsKey("port")) {
						voteActionServerSubconfig.put("port", 25575);
						save = true;
					}
					if (!voteActionServerSubconfig.containsKey("password")) {
						voteActionServerSubconfig.put("password", "password");
						save = true;
					}
					if (!voteActionSubconfig.containsKey("commands")) {
						voteActionSubconfig.put("commands", new ArrayList());
						save = true;
					}
					if (!voteActionSubconfig.containsKey("regex-replace")) {
						voteActionSubconfig.put("regex-replace", new HashMap());
						save = true;
					}
				}
				if (voteActionType.equalsIgnoreCase("shell")) {
					if (!voteActionSubconfig.containsKey("commands")) {
						voteActionSubconfig.put("commands", new ArrayList());
						save = true;
					}
					if (!voteActionSubconfig.containsKey("regex-replace")) {
						voteActionSubconfig.put("regex-replace", new HashMap());
						save = true;
					}
				}
			}
		}

		configVersion = (Integer) config.get("config-version");
		logDirectory = new File((String) config.get("log-directory"));
		if (!logDirectory.exists()) {
			logDirectory.mkdirs();
		}
		logFile = new File(logDirectory, TimestampUtils.getTimestamp() + ".log");
		inetSocketAddress = new InetSocketAddress((String) serverSubconfig.get("ip"), (Integer) serverSubconfig.get("port"));
		publicKeyFile = new File((String) keyPairFilesSubconfig.get("public"));
		privateKeyFile = new File((String) keyPairFilesSubconfig.get("private"));
		loadKeyPair();
		voteActions.clear();
		for (int i = 0; i < onVoteSubconfig.size(); i++) {
			Map voteActionSubconfig = (Map) onVoteSubconfig.get(i);
			if (((String) voteActionSubconfig.get("action")).equalsIgnoreCase("rcon")) {
				Map voteActionServerSubconfig = (Map) voteActionSubconfig.get("server");
				voteActions.add(new VoteAction(new RconCommandSender(new RconConnection(new InetSocketAddress((String) voteActionServerSubconfig.get("ip"), (Integer) voteActionServerSubconfig.get("port")), (String) voteActionServerSubconfig.get("password"))), (List) voteActionSubconfig.get("commands"), (HashMap) voteActionSubconfig.get("regex-replace")));
			} else if (((String) voteActionSubconfig.get("action")).equalsIgnoreCase("shell")) {
				voteActions.add(new VoteAction(new ShellCommandSender(), (List) voteActionSubconfig.get("commands"), (HashMap) voteActionSubconfig.get("regex-replace")));
			}
		}

		if (save) {
			save();
		}
	}

	@Override
	public void save() throws IOException {
		HashMap yamlConfig = new HashMap();
		yamlConfig.put("config-version", getConfigVersion());
		yamlConfig.put("log-directory", getLogDirectory().getPath());
		yamlConfig.put("server", new HashMap() {{
			put("ip", getInetSocketAddress().getAddress().getHostName());
			put("port", getInetSocketAddress().getPort());
		}});
		yamlConfig.put("key-pair-files", new HashMap() {{
			put("public", getPublicKeyFile().getPath());
			put("private", getPrivateKeyFile().getPath());
		}});
		yamlConfig.put("on-vote", new ArrayList<Map>() {{
			for (final VoteAction voteAction : getVoteActions()) {
				add(new HashMap() {{
					if (voteAction.getCommandSender() instanceof RconCommandSender) {
						final RconCommandSender commandSender = (RconCommandSender) voteAction.getCommandSender();
						put("action", "rcon");
						put("server", new HashMap() {{
							put("ip", commandSender.getRconConnection().getInetSocketAddress().getAddress().getHostName());
							put("port", commandSender.getRconConnection().getInetSocketAddress().getPort());
							put("password", commandSender.getRconConnection().getPassword());
						}});
					}
					if (voteAction.getCommandSender() instanceof ShellCommandSender) {
						put("action", "shell");
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