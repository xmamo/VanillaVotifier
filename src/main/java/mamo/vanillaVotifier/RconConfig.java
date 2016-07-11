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

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class RconConfig {
	private InetSocketAddress inetSocketAddress;
	private String password;
	private CopyOnWriteArrayList<String> commands;

	{
		commands = new CopyOnWriteArrayList<String>();
	}

	public RconConfig(InetSocketAddress inetSocketAddress, String password) {
		this.inetSocketAddress = inetSocketAddress;
		this.password = password;
	}

	public synchronized InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

	public synchronized void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
		if (inetSocketAddress == null) {
			inetSocketAddress = new InetSocketAddress("127.0.0.1", 8192);
		}
		this.inetSocketAddress = inetSocketAddress;
	}

	public synchronized String getPassword() {
		return password;
	}

	public synchronized void setPassword(String password) {
		this.password = password;
	}

	public List<String> getCommands() {
		return commands;
	}
}