package co.virtualdragon.vanillaVotifier.server.event;

import co.virtualdragon.vanillaVotifier.event.Event;
import java.net.Socket;

public interface SocketEvent extends Event {

	Socket getSocket();
}
