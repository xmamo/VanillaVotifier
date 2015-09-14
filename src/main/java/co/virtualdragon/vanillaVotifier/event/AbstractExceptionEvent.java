package co.virtualdragon.vanillaVotifier.event;

public abstract class AbstractExceptionEvent implements ExceptionEvent {

	private Exception exception;

	public AbstractExceptionEvent(Exception exception) {
		this.exception = exception;
	}

	@Override
	public Exception getException() {
		return exception;
	}
}
