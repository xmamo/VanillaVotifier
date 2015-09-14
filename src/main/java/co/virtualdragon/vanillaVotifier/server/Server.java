package co.virtualdragon.vanillaVotifier.server;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.event.Event;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Set;

public interface Server {

	void start() throws IOException, GeneralSecurityException;

	void stop() throws IOException;

	boolean isRunning();

	Set<Listener> getListeners();

	void notifyListeners(Event event);
}
