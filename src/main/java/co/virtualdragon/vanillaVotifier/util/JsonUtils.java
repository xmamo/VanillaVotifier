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
package co.virtualdragon.vanillaVotifier.util;

import org.json.JSONObject;

public class JsonUtils {

	public static String jsonToPrettyString(JSONObject jsonObject) {
		String[] prettyStringLines = jsonObject.toString(1).split("\n");
		String prettyString = "";
		for (String prettyStringLine : prettyStringLines) {
			for (int i = 0; i < prettyStringLine.length(); i++) {
				if (prettyStringLine.charAt(i) == ' ') {
					prettyStringLine = prettyStringLine.replaceFirst(" ", "\t");
				} else { // If it's not a space, therefore I don't need to replace with tabs anymore,
					break;
				}
			}
			prettyString += prettyStringLine + System.lineSeparator();
		}
		return prettyString.substring(0, prettyString.length() - System.lineSeparator().length());
	}
}
