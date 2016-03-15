/*
 * Copyright (C) 2015 Matteo Morena
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

import co.virtualdragon.vanillaVotifier.LanguagePack;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.ResourceBundle;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

public class PropertiesLanguagePack implements LanguagePack {

	private final ResourceBundle bundle;

	public PropertiesLanguagePack(String languagePackName) {
		bundle = ResourceBundle.getBundle(languagePackName);
	}

	@Override
	public String getString(String key) {
		return getString(key, new Entry[]{});
	}

	@Override
	public String getString(String key, Entry<String, Object>... substitutions) {
		String string = null;
		if (bundle.containsKey(key)) {
			string = bundle.getString(key).replaceAll("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]", System.getProperty("line.separator"));
		} else if (bundle.containsKey(key + "-location")) {
			String resource = getString(key + "-location");
			if (resource == null) {
				return null;
			}
			if (!resource.startsWith("/")) {
				resource = "/co/virtualdragon/vanillaVotifier/impl/lang/" + resource;
			}
			BufferedInputStream in;
			in = new BufferedInputStream(PropertiesLanguagePack.class.getResourceAsStream(resource));
			StringBuilder stringBuilder = new StringBuilder();
			int i;
			try {
				while ((i = in.read()) != -1) {
					stringBuilder.append((char) i);
				}
				in.close();
			} catch (IOException e) {
				// Can't happen.
			}
			string = stringBuilder.toString().replaceAll("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]", System.getProperty("line.separator"));
		}
		if (substitutions == null || string == null) {
			return string;
		} else {
			HashMap<String, Object> substitutionsMap = new HashMap<String, Object>();
			for (Entry<String, Object> substitution : substitutions) {
				if (!(substitution.getValue() instanceof Throwable)) {
					substitutionsMap.put(substitution.getKey(), substitution.getValue());
				} else {
					substitutionsMap.put(substitution.getKey(), ExceptionUtils.getStackTrace((Throwable) substitution.getValue()));
				}
			}
			return new StrSubstitutor(substitutionsMap).replace(string);
		}
	}
}
