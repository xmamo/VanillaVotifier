/* 
 * Copyright (C) 2015 Matteo Morena
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

import co.virtualdragon.vanillaVotifier.Vote;
import java.sql.Timestamp;
import java.util.Calendar;

public class VanillaVotifierVote implements Vote {

	private final String serviceName;
	private final String userName;
	private final String address;
	private final String timeStamp;

	public VanillaVotifierVote(String serviceName, String userName, String address) {
		this(serviceName, userName, address, new Timestamp(Calendar.getInstance().getTime().getTime()).toString());
	}

	public VanillaVotifierVote(String serviceName, String userName, String address, String timeStamp) {
		this.serviceName = serviceName;
		this.userName = userName;
		this.address = address;
		this.timeStamp = timeStamp;
	}

	@Override
	public String getServiceName() {
		return serviceName;
	}

	@Override
	public String getUserName() {
		return userName;
	}

	@Override
	public String getAddress() {
		return address;
	}

	@Override
	public String getTimeStamp() {
		return timeStamp;
	}
}
