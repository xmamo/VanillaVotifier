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
import co.virtualdragon.vanillaVotifier.Rcon.Packet;
import co.virtualdragon.vanillaVotifier.Votifier;
import java.net.SocketException;

public class RconCommandSender implements CommandSender {

	private final Votifier votifier;

	public RconCommandSender(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public String sendCommand(Rcon rcon, String command) throws Exception {
		if (!rcon.isConnected()) {
			rcon.connect();
		}
		Packet packet = null;
		try {
			packet = rcon.sendRequest(new Packet(rcon.getRequestId(), Packet.Type.LOG_IN, rcon.getRconConfig().getPassword()));
		} catch (SocketException e) {
			rcon.connect();
			packet = rcon.sendRequest(new Packet(rcon.getRequestId(), Packet.Type.LOG_IN, rcon.getRconConfig().getPassword()));
		}
		if (packet.getRequestId() != -1) {
			return rcon.sendRequest(new Packet(rcon.getRequestId(), Packet.Type.COMMAND, command)).getPayload();
		} else {
			throw new Exception("Invalid password.");
		}
	}
}
