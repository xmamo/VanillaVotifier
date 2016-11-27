package mamo.vanillaVotifier.event;

import org.jetbrains.annotations.NotNull;

public class RegularExpressionPatternErrorException extends AbstractExceptionEvent {
	public RegularExpressionPatternErrorException(@NotNull Exception exception) {
		super(exception);
	}
}