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

import mamo.vanillaVotifier.exception.BrokenPipeException;
import mamo.vanillaVotifier.exception.InvalidRconPasswordException;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class RconCommandSender implements CommandSender {
	@NotNull protected RconConnection rconConnection;
	protected boolean loggedIn;

	public RconCommandSender(@NotNull RconConnection rconConnection) {
		this.rconConnection = rconConnection;
	}

	@NotNull
	public RconConnection getRconConnection() {
		return rconConnection;
	}

	public boolean isLoggedIn() {
		return loggedIn;
	}

	public void logIn() throws IOException, InvalidRconPasswordException {
		synchronized (rconConnection) {
			try {
				rconConnection.logIn();
				loggedIn = true;
			} catch (BrokenPipeException e) {
				rconConnection = new RconConnection(rconConnection.getInetSocketAddress(), rconConnection.getPassword());
				logIn();
			}
		}
	}

	@Override
	@NotNull
	public VotifierPacket sendCommand(@NotNull String command) throws IOException, InvalidRconPasswordException {
		synchronized (rconConnection) {
			try {
				if (!isLoggedIn()) {
					logIn();
				}
				return rconConnection.sendCommand(command);
			} catch (BrokenPipeException e) {
				rconConnection = new RconConnection(rconConnection.getInetSocketAddress(), rconConnection.getPassword());
				logIn();
				return sendCommand(command);
			}
		}
	}
}