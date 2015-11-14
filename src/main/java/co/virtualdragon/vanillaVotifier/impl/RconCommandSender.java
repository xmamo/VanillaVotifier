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

import co.virtualdragon.vanillaVotifier.CommandSender;
import co.virtualdragon.vanillaVotifier.Rcon;
import co.virtualdragon.vanillaVotifier.Rcon.VanillaVotifierPacket;
import co.virtualdragon.vanillaVotifier.exception.InvalidRconPasswordException;
import java.io.IOException;

public class RconCommandSender implements CommandSender {

	private boolean loggedIn;

	@Override
	public String sendCommand(Rcon rcon, String command) throws IOException, InvalidRconPasswordException {
		synchronized (rcon.getRconConfig()) {
			if (!rcon.isConnected()) {
				rcon.connect();
				loggedIn = false;
			}
			if (!loggedIn) {
				if (rcon.sendRequest(new VanillaVotifierPacket(rcon.getRequestId(), VanillaVotifierPacket.Type.LOG_IN, rcon.getRconConfig().getPassword())).getRequestId() != -1) {
					loggedIn = true;
				} else {
					throw new InvalidRconPasswordException();
				}
			}
			return rcon.sendRequest(new VanillaVotifierPacket(rcon.getRequestId(), VanillaVotifierPacket.Type.COMMAND, command)).getPayload();
		}
	}
}
