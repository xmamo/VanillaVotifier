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

import mamo.vanillaVotifier.utils.TimestampUtils;
import org.jetbrains.annotations.NotNull;

public class Vote {
	@NotNull protected String serviceName;
	@NotNull protected String userName;
	@NotNull protected String address;
	@NotNull protected String timeStamp;

	public Vote(String serviceName, String userName, String address) {
		this(serviceName, userName, address, TimestampUtils.getTimestamp());
	}

	public Vote(@NotNull String serviceName, @NotNull String userName, @NotNull String address, @NotNull String timeStamp) {
		this.serviceName = serviceName;
		this.userName = userName;
		this.address = address;
		this.timeStamp = timeStamp;
	}

	@NotNull
	public String getServiceName() {
		return serviceName;
	}

	@NotNull
	public String getUserName() {
		return userName;
	}

	@NotNull
	public String getAddress() {
		return address;
	}

	@NotNull
	public String getTimeStamp() {
		return timeStamp;
	}
}