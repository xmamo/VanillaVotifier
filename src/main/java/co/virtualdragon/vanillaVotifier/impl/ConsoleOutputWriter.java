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

import co.virtualdragon.vanillaVotifier.OutputWriter;
import co.virtualdragon.vanillaVotifier.Votifier;
import java.util.Map.Entry;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ConsoleOutputWriter implements OutputWriter {

	private final Votifier votifier;

	public ConsoleOutputWriter(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public void print(Object object) {
		synchronized (System.out) {
			if (!(object instanceof Throwable)) {
				System.out.print(object);
			} else {
				System.out.print(ExceptionUtils.getStackTrace((Throwable) object));
			}
		}
	}

	@Override
	public void println(Object object) {
		synchronized (System.out) {
			if (!(object instanceof Throwable)) {
				System.out.println(object);
			} else {
				System.out.println(ExceptionUtils.getStackTrace((Throwable) object));
			}
		}
	}

	@Override
	public void printTranslation(String key) {
		printTranslation(key, new Entry[]{});
	}

	@Override
	public void printTranslation(String key, Entry<String, Object>... substitutions) {
		print(votifier.getLanguagePack().getString(key, substitutions));
	}

	@Override
	public void printlnTranslation(String key) {
		printlnTranslation(key, new Entry[]{});
	}

	@Override
	public void printlnTranslation(String key, Entry<String, Object>... substitutions) {
		println(votifier.getLanguagePack().getString(key, substitutions));
	}
}
