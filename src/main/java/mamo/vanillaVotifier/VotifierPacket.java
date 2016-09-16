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

public class VotifierPacket {
	protected int length;
	protected int requestId;
	@NotNull protected Type type;
	@NotNull protected String payload;

	public VotifierPacket(int requestId, @NotNull Type type, @NotNull String payload) {
		this(Integer.SIZE / 8 + Integer.SIZE / 8 + payload.length() + Byte.SIZE / 8 * 2, requestId, type, payload);
	}

	public VotifierPacket(int length, int requestId, @NotNull Type type, @NotNull String payload) {
		this.length = length;
		this.requestId = requestId;
		this.type = type;
		this.payload = payload;
	}

	public int getLength() {
		return length;
	}

	public int getRequestId() {
		return requestId;
	}

	@NotNull
	public Type getType() {
		return type;
	}

	@NotNull
	public String getPayload() {
		return payload;
	}

	public enum Type {
		COMMAND_RESPONSE(0), COMMAND(2), LOG_IN(3);

		protected int i;

		Type(int i) {
			this.i = i;
		}

		public static VotifierPacket.Type fromInt(int i) {
			if (i == 0) {
				return COMMAND_RESPONSE;
			} else if (i == 2) {
				return COMMAND;
			} else if (i == 3) {
				return LOG_IN;
			} else {
				throw new IllegalArgumentException("i has to be equal to 0, 2, or 3!");
			}
		}

		public int toInt() {
			return i;
		}
	}
}