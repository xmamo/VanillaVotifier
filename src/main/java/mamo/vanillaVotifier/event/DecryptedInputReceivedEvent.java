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

package mamo.vanillaVotifier.event;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class DecryptedInputReceivedEvent implements SocketEvent, MessageEvent {
	@NotNull protected Socket socket;
	@NotNull protected String message;

	public DecryptedInputReceivedEvent(@NotNull Socket socket, @NotNull String message) {
		this.socket = socket;
		this.message = message;
	}

	@Override
	@NotNull
	public Socket getSocket() {
		return socket;
	}

	@Override
	@NotNull
	public String getMessage() {
		return message;
	}
}