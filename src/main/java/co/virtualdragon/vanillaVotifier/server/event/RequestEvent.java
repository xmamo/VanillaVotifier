package co.virtualdragon.vanillaVotifier.server.event;

public interface RequestEvent extends MessageEvent, SocketEvent {

	String getServiceName();

	String getUserName();

	String getAddress();

	String getTimeStamp();
}
