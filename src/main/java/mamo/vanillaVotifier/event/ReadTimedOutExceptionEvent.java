package mamo.vanillaVotifier.event;

import org.jetbrains.annotations.NotNull;

import java.net.Socket;

public class ReadTimedOutExceptionEvent extends AbstractExceptionEvent implements SocketEvent {
	protected @NotNull Socket socket;

	public ReadTimedOutExceptionEvent(@NotNull Socket socket, @NotNull Exception exception) {
		super(exception);
		this.socket = socket;
	}

	@Override
	@NotNull
	public Socket getSocket() {
		return socket;
	}
}