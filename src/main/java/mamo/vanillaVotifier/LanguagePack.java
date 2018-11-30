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

import mamo.vanillaVotifier.utils.SubstitutionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map.Entry;
import java.util.ResourceBundle;

public class LanguagePack {
	@NotNull protected String languagePackPath;
	@NotNull protected ResourceBundle bundle;

	public LanguagePack(@NotNull String languagePackPath, @NotNull String languagePackName) {
		if (languagePackPath.endsWith("/")) {
			languagePackPath = languagePackPath.substring(0, languagePackPath.length() - 1);
		}
		this.languagePackPath = languagePackPath;
		bundle = ResourceBundle.getBundle(languagePackPath + "/" + languagePackName);
	}

	@Nullable
	public String getString(@NotNull String key, @Nullable Entry<String, Object>... substitutions) {
		String string = null;
		if (bundle.containsKey(key)) {
			string = bundle.getString(key).replaceAll("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]", System.getProperty("line.separator"));
		} else if (bundle.containsKey(key + "-location")) {
			String resource = getString(key + "-location");
			if (resource == null) {
				return null;
			}
			if (!resource.startsWith("/")) {
				resource = "/" + languagePackPath + "/" + resource;
			}
			BufferedReader in = new BufferedReader(new InputStreamReader(LanguagePack.class.getResourceAsStream(resource)));
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

		if (string == null) {
			return string;
		} else {
			return SubstitutionUtils.buildStrSubstitutor(substitutions).replace(string);
		}
	}
}