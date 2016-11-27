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

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VoteAction {
	@NotNull protected CommandSender commandSender;
	@NotNull protected List<String> commands;
	@NotNull protected HashMap<String, String> regexReplacements;

	public VoteAction(@NotNull CommandSender commandSender, @Nullable List<String> commands, @Nullable HashMap<String, String> regexReplacements) {
		if (commands == null) {
			commands = new ArrayList<String>();
		}
		if (regexReplacements == null) {
			regexReplacements = new HashMap<String, String>();
		}
		this.commandSender = commandSender;
		this.commands = commands;
		this.regexReplacements = regexReplacements;
	}

	@NotNull
	public CommandSender getCommandSender() {
		return commandSender;
	}

	@NotNull
	public List<String> getCommands() {
		return commands;
	}

	@NotNull
	public HashMap<String, String> getRegexReplacements() {
		return regexReplacements;
	}
}