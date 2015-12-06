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

import co.virtualdragon.vanillaVotifier.Votifier;
import java.util.Map.Entry;
import org.apache.commons.lang3.exception.ExceptionUtils;
import co.virtualdragon.vanillaVotifier.Logger;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileWriter;

public class VanillaVotifierLogger implements Logger {

	private final Votifier votifier;
	private final StringBuffer buffer;

	private BufferedWriter logWriter;

	{
		buffer = new StringBuffer();
	}

	public VanillaVotifierLogger(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public void print(Object object) {
		String string;
		if (!(object instanceof Throwable)) {
			string = object.toString();
		} else {
			string = ExceptionUtils.getStackTrace((Throwable) object);
		}
		synchronized (System.out) {
			System.out.print(string);
		}
		initWriterIfInitialized();
		if (logWriter != null) {
			synchronized (logWriter) {
				buffer.append(string);
				try {
					logWriter.write(buffer.toString());
					logWriter.flush();
				} catch (Exception e) {
					// Ignoring.
				}
				buffer.setLength(0);
			}
		} else {
			buffer.append(string);
		}
	}

	@Override
	public void println(Object object) {
		print(object + System.lineSeparator());
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

	private void initWriterIfInitialized() {
		if (logWriter == null && votifier.getConfig().isLoaded()) {
			try {
				logWriter = new BufferedWriter(new FileWriter(votifier.getConfig().getLogFile()));
			} catch (Exception e) {
				// Ignoring.
			}
		}
	}
}
