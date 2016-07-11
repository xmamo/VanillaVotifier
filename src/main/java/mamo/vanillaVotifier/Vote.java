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

import java.sql.Timestamp;
import java.util.Calendar;

public class Vote {
	private final String serviceName;
	private final String userName;
	private final String address;
	private final String timeStamp;
	private final String empty;

	public Vote(String serviceName, String userName, String address) {
		this(serviceName, userName, address, new Timestamp(Calendar.getInstance().getTime().getTime()).toString(), null);
	}

	public Vote(String serviceName, String userName, String address, String timeStamp, String empty) {
		this.serviceName = serviceName;
		this.userName = userName;
		this.address = address;
		this.timeStamp = timeStamp;
		this.empty = empty;
	}

	public String getServiceName() {
		return serviceName;
	}

	public String getUserName() {
		return userName;
	}

	public String getAddress() {
		return address;
	}

	public String getTimeStamp() {
		return timeStamp;
	}

	public String getEmpty() {
		return empty;
	}
}