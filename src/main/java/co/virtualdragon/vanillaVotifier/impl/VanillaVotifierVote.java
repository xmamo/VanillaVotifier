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
