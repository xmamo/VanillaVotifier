package co.virtualdragon.vanillaVotifier.server.event;

import java.net.Socket;

public abstract class AbstractRequestEvent extends AbstractMessageEvent implements RequestEvent {

	private Socket socket;
	private String serviceName;
	private String userName;
	private String address;
	private String timeStamp;

	public AbstractRequestEvent(Socket socket, String message, String serviceName, String userName, String address, String timeStamp) {
		super(message);
		this.socket = socket;
		this.serviceName = serviceName;
		this.userName = userName;
		this.address = address;
		this.timeStamp = timeStamp;
	}

	@Override
	public Socket getSocket() {
		return socket;
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
