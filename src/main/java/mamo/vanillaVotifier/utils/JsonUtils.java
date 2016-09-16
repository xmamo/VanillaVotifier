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

package mamo.vanillaVotifier.utils;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

public class JsonUtils {
	@NotNull
	public static String jsonToPrettyString(@NotNull JSONObject jsonObject) {
		String[] prettyStringLines = jsonObject.toString(1).split("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]");
		String prettyString = "";
		for (String prettyStringLine : prettyStringLines) {
			for (int i = 0; i < prettyStringLine.length(); i++) {
				if (prettyStringLine.charAt(i) == ' ') {
					prettyStringLine = prettyStringLine.replaceFirst(" ", "\t");
				} else { // If it's not a space, therefore I don't need to replace with tabs anymore,
					break;
				}
			}
			prettyString += prettyStringLine + System.getProperty("line.separator");
		}
		return prettyString.substring(0, prettyString.length() - System.getProperty("line.separator").length());
	}

	public static boolean merge(@NotNull JSONObject from, JSONObject to) {
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
}