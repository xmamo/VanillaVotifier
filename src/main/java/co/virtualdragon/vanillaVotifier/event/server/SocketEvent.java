package co.virtualdragon.vanillaVotifier.event.server;

import co.virtualdragon.vanillaVotifier.event.Event;
import java.net.Socket;

public interface SocketEvent extends Event {

	Socket getSocket();
}
