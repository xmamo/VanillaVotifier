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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Map.Entry;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class Logger {
	private final VanillaVotifier votifier;
	private final StringBuffer buffer;

	private BufferedWriter logWriter;

	{
		buffer = new StringBuffer();
	}

	public Logger(VanillaVotifier votifier) {
		this.votifier = votifier;
	}

	public void print(Object object) {
		String string = toString(object);
		synchronized (System.out) {
			System.out.print(string);
		}
		initWriterIfVotifierIsLoaded();
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

	public void println(Object object) {
		print(toString(object) + System.getProperty("line.separator"));
	}

	protected String toString(Object object) {
		String string;
		if (!(object instanceof Throwable)) {
			string = object.toString();
		} else {
			string = ExceptionUtils.getStackTrace((Throwable) object);
		}
		return string;
	}

	public void printTranslation(String key) {
		printTranslation(key, new Entry[]{});
	}

	public void printTranslation(String key, Entry<String, Object>... substitutions) {
		print(votifier.getLanguagePack().getString(key, substitutions));
	}

	public void printlnTranslation(String key) {
		printlnTranslation(key, new Entry[]{});
	}

	public void printlnTranslation(String key, Entry<String, Object>... substitutions) {
		println(votifier.getLanguagePack().getString(key, substitutions));
	}

	private void initWriterIfVotifierIsLoaded() {
		if (logWriter == null && votifier.getConfig().isLoaded()) {
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(votifier.getConfig().getLogFile())));
			} catch (Exception e) {
				// FileNotFoundException, UnsupportedEncodingException: ignoring.
			}
		}
	}
}