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

import mamo.vanillaVotifier.utils.JsonUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

@Deprecated
public class JsonConfig extends AbstractConfig {
	public JsonConfig(@NotNull File configFile) {
		super(configFile);
		logDirectory = logDirectory.getParentFile();
	}

	@Override
	public synchronized void load() throws IOException, InvalidKeySpecException {
		if (!configFile.exists()) {
			copyDefaultConfig(YamlConfig.class.getResourceAsStream("config.json"));
		}
		@SuppressWarnings("deprecation") BufferedInputStream in = new BufferedInputStream(JsonConfig.class.getResourceAsStream("config.json"));
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
		logFile = new File(config.getString("log-file"));
		inetSocketAddress = new InetSocketAddress(config.getString("ip"), config.getInt("port"));
		publicKeyFile = new File(config.getJSONObject("key-pair-files").getString("public"));
		privateKeyFile = new File(config.getJSONObject("key-pair-files").getString("private"));
		loadKeyPair();
		voteActions.clear();
		for (int i = 0; i < config.getJSONArray("rcon-list").length(); i++) {
			JSONObject jsonObject = config.getJSONArray("rcon-list").getJSONObject(i);
			voteActions.add(new VoteAction(new RconCommandSender(new RconConnection(new InetSocketAddress(jsonObject.getString("ip"), jsonObject.getInt("port")), jsonObject.getString("password"))), (List<String>) jsonObject.get("commands")));
		}
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

	@Override
	public synchronized void save() throws IOException {
		JSONObject config = new JSONObject();
		config.put("config-version", getConfigVersion());
		config.put("log-file", getLogFile().getPath());
		config.put("ip", getInetSocketAddress().getAddress().toString());
		config.put("port", getInetSocketAddress().getPort());
		config.put("key-pair-files", new JSONObject() {
			{
				put("public", getPublicKeyFile().getPath());
				put("private", getPrivateKeyFile().getPath());
			}
		});
		config.put("rcon-list", new JSONArray() {
			{
				for (final VoteAction voteAction : getVoteActions()) {
					final RconCommandSender commandSender = (RconCommandSender) voteAction.getCommandSender();
					put(new JSONObject() {
						{
							put("ip", commandSender.getRconConnection().getInetSocketAddress().getAddress().toString());
							put("port", commandSender.getRconConnection().getInetSocketAddress().getPort());
							put("password", commandSender.getRconConnection().getPassword());
							put("commands", voteAction.getCommands());
						}
					});
				}
			}
		});
		BufferedWriter out = new BufferedWriter(new FileWriter(configFile));
		out.write(JsonUtils.jsonToPrettyString(config));
		out.flush();
		out.close();
		saveKeyPair();
	}
}