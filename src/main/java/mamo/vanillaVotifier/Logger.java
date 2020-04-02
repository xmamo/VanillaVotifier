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

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map.Entry;

public class Logger {
	@NotNull protected VanillaVotifier votifier;
	@NotNull protected StringBuffer buffer = new StringBuffer();
	private BufferedWriter logWriter;
	private SimpleDateFormat formatter = new SimpleDateFormat("[HH:mm:ss]");

	public Logger(@NotNull VanillaVotifier votifier) {
		this.votifier = votifier;
	}

	public void print(@Nullable Object object) {
		String timestamp = formatter.format(new Date());
		String string = timestamp + " " + toString(object);
		synchronized (votifier.getWriter()) {
			try {
				votifier.getWriter().write(string);
				votifier.getWriter().flush();
			} catch (Exception e) { // IOException
				// Can't happen.
			}
		}
		initWriterIfVotifierIsLoaded();
		if (logWriter != null) {
			synchronized (logWriter) {
				buffer.append(string);
				try {
					logWriter.write(buffer.toString());
					buffer.setLength(0);
					logWriter.flush();
				} catch (Exception e) {
					// Ignoring.
				}
			}
		} else {
			buffer.append(string);
		}
	}

	public void println(@Nullable Object object) {
		print(toString(object) + System.getProperty("line.separator"));
	}

	@NotNull
	public String toString(@Nullable Object object) {
		if (object == null) {
			return "";
		}
		if (!(object instanceof Throwable)) {
			return object.toString();
		} else {
			return ExceptionUtils.getStackTrace((Throwable) object);
		}
	}

	public void printTranslation(@NotNull String key, @Nullable Entry<String, Object>... substitutions) {
		print(votifier.getLanguagePack().getString(key, substitutions));
	}

	public void printlnTranslation(@NotNull String key, @Nullable Entry<String, Object>... substitutions) {
		println(votifier.getLanguagePack().getString(key, substitutions));
	}

	private void initWriterIfVotifierIsLoaded() {
		if (logWriter == null && votifier.getConfig().getLogFile() != null) {
			try {
				logWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(votifier.getConfig().getLogFile())));
			} catch (Exception e) {
				// FileNotFoundException, UnsupportedEncodingException: ignoring.
			}
		}
	}
}