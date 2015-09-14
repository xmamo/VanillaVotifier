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
			prettyString += prettyStringLine + "\n";
		}
		return prettyString.substring(0, prettyString.length() - 1);
	}
}
