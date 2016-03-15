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
package co.virtualdragon.vanillaVotifier;

import java.util.Map.Entry;

public interface Logger {

	void print(Object object);

	void println(Object object);

	void printTranslation(String key);

	void printTranslation(String key, Entry<String, Object>... replacements);

	void printlnTranslation(String key);

	void printlnTranslation(String key, Entry<String, Object>... replacements);
}
