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

import org.apache.commons.lang3.text.StrTokenizer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Map;

public class ShellCommandSender implements CommandSender {
	@Override
	@NotNull
	public Process sendCommand(@NotNull String command) throws IOException {
		return sendCommand(command, null);
	}

	@NotNull
	public Process sendCommand(@NotNull String command, @Nullable Map<String, String> environment) throws IOException {
		ProcessBuilder processBuilder = new ProcessBuilder(new StrTokenizer(command).getTokenArray());
		if (environment != null) {
			processBuilder.environment().putAll(environment);
		}
		return processBuilder.start();
	}
}