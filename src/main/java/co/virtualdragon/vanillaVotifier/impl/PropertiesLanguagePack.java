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

import co.virtualdragon.vanillaVotifier.LanguagePack;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.util.ResourceBundle;

public class PropertiesLanguagePack implements LanguagePack {

	private final ResourceBundle bundle;

	public PropertiesLanguagePack(String languagePackName) {
		bundle = ResourceBundle.getBundle(languagePackName);
	}

	@Override
	public String getString(String key) {
		if (bundle.containsKey(key)) {
			return bundle.getString(key);
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
					char c = (char) i;
					if (c != '\n') {
						stringBuilder.append(c);
					} else {
						stringBuilder.append(System.lineSeparator());
					}
				}
				in.close();
			} catch (IOException e) {
				// Can't happen.
			}
			return stringBuilder.toString();
		}
		return null;
	}
}
