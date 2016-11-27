/*
 * Copyright (C) 2016  Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package mamo.vanillaVotifier;

import mamo.vanillaVotifier.event.*;
import mamo.vanillaVotifier.exception.InvalidRconPasswordException;
import mamo.vanillaVotifier.utils.RsaUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.ConnectException;
import java.util.AbstractMap.SimpleEntry;

public class VotifierServerListener implements Listener {
	@NotNull protected VanillaVotifier votifier;

	public VotifierServerListener(@NotNull VanillaVotifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public void onEvent(@NotNull Event event) {
		if (event instanceof ServerStartingEvent) {
			votifier.getLogger().printlnTranslation("s1");
		} else if (event instanceof ServerStartedEvent) {
			votifier.getLogger().printlnTranslation("s2", new SimpleEntry<String, Object>("key", RsaUtils.keyToString(votifier.getConfig().getKeyPair().getPublic())));
		} else if (event instanceof ConnectionEstablishedEvent) {
			ConnectionEstablishedEvent connectionEstablishedEvent = (ConnectionEstablishedEvent) event;
			votifier.getLogger().printlnTranslation("s3",
					new SimpleEntry<String, Object>("ip", connectionEstablishedEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", connectionEstablishedEvent.getSocket().getPort()));
		} else if (event instanceof VoteEventVotifier) {
			VoteEventVotifier voteEvent = (VoteEventVotifier) event;
			votifier.getLogger().printlnTranslation("s4",
					new SimpleEntry<String, Object>("ip", voteEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", voteEvent.getSocket().getPort()),
					new SimpleEntry<String, Object>("service-name", "\"" + StringEscapeUtils.escapeJava(voteEvent.getVote().getServiceName()) + "\""),
					new SimpleEntry<String, Object>("user-name", "\"" + StringEscapeUtils.escapeJava(voteEvent.getVote().getUserName()) + "\""),
					new SimpleEntry<String, Object>("address", "\"" + StringEscapeUtils.escapeJava(voteEvent.getVote().getAddress()) + "\""),
					new SimpleEntry<String, Object>("timestamp", "\"" + StringEscapeUtils.escapeJava(voteEvent.getVote().getTimeStamp()) + "\""));
		} else if (event instanceof SendingRconCommandEvent) {
			SendingRconCommandEvent sendingRconCommandEvent = (SendingRconCommandEvent) event;
			votifier.getLogger().printlnTranslation("s5",
					new SimpleEntry<String, Object>("ip", sendingRconCommandEvent.getRconConnection().getInetSocketAddress().getAddress().getHostName()),
					new SimpleEntry<String, Object>("port", sendingRconCommandEvent.getRconConnection().getInetSocketAddress().getPort()),
					new SimpleEntry<String, Object>("command", sendingRconCommandEvent.getCommand()));
		} else if (event instanceof RconCommandResponseEvent) {
			RconCommandResponseEvent commandResponseEvent = ((RconCommandResponseEvent) event);
			if (commandResponseEvent.getMessage() != null && !commandResponseEvent.getMessage().isEmpty()) {
				votifier.getLogger().printlnTranslation("s6", new SimpleEntry<String, Object>("response", commandResponseEvent.getMessage()));
			} else {
				votifier.getLogger().printlnTranslation("s53");
			}
		} else if (event instanceof RconExceptionEvent) {
			Exception exception = ((RconExceptionEvent) event).getException();
			if (exception instanceof InvalidRconPasswordException) {
				votifier.getLogger().printlnTranslation("s7");
			} else if (exception instanceof ConnectException) {
				votifier.getLogger().printlnTranslation("s39");
			} else {
				votifier.getLogger().printlnTranslation("s28", new SimpleEntry<String, Object>("exception", exception));
			}
		} else if (event instanceof SendingShellCommandEvent) {
			votifier.getLogger().printlnTranslation("s59", new SimpleEntry<String, Object>("command", ((SendingShellCommandEvent) event).getCommand()));
		} else if (event instanceof ShellCommandSentEvent) {
			votifier.getLogger().printlnTranslation("s60");
		} else if (event instanceof ShellCommandExceptionEvent) {
			Exception exception = ((ShellCommandExceptionEvent) event).getException();
			if (exception instanceof IOException) {
				votifier.getLogger().printlnTranslation("s61");
			} else {
				votifier.getLogger().printlnTranslation("s62", new SimpleEntry<String, Object>("exception", exception));
			}
		} else if (event instanceof InvalidRequestEvent) {
			InvalidRequestEvent invalidRequestEvent = (InvalidRequestEvent) event;
			StringBuilder message = new StringBuilder();
			String[] sections = invalidRequestEvent.getMessage().split("\n");
			for (int i = 0; i < sections.length - 1; i++) {
				message.append("\"").append(StringEscapeUtils.escapeJava(sections[i])).append("\" ");
			}
			message.append("\"").append(StringEscapeUtils.escapeJava(sections[sections.length - 1])).append("\"");
			votifier.getLogger().printlnTranslation("s8",
					new SimpleEntry<String, Object>("ip", invalidRequestEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", invalidRequestEvent.getSocket().getPort()),
					new SimpleEntry<String, Object>("message", message));
		} else if (event instanceof ConnectionClosedEvent) {
			ConnectionClosedEvent connectionClosedEvent = (ConnectionClosedEvent) event;
			votifier.getLogger().printlnTranslation("s10",
					new SimpleEntry<String, Object>("ip", connectionClosedEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", connectionClosedEvent.getSocket().getPort()));
		} else if (event instanceof ConnectionCloseExceptionEvent) {
			ConnectionCloseExceptionEvent connectionCloseException = (ConnectionCloseExceptionEvent) event;
			votifier.getLogger().printlnTranslation("s11",
					new SimpleEntry<String, Object>("ip", connectionCloseException.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", connectionCloseException.getSocket().getPort()),
					new SimpleEntry<String, Object>("exception", connectionCloseException.getException()));
		} else if (event instanceof DecryptInputExceptionEvent) {
			votifier.getLogger().printlnTranslation("s46");
		} else if (event instanceof RegularExpressionPatternErrorException) {
			votifier.getLogger().printlnTranslation("s65", new SimpleEntry<String, Object>("exception", ((RegularExpressionPatternErrorException) event).getException().getMessage()));
		} else if (event instanceof ReadTimedOutExceptionEvent) {
			ReadTimedOutExceptionEvent readTimedOutExceptionEvent = (ReadTimedOutExceptionEvent) event;
			votifier.getLogger().printlnTranslation("s64",
					new SimpleEntry<String, Object>("ip", readTimedOutExceptionEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", readTimedOutExceptionEvent.getSocket().getPort()));
		} else if (event instanceof CommunicationExceptionEvent) {
			votifier.getLogger().printlnTranslation("s29", new SimpleEntry<String, Object>("exception", ((CommunicationExceptionEvent) event).getException()));
		} else if (event instanceof ConnectionEstablishExceptionEvent) {
			votifier.getLogger().printlnTranslation("s30", new SimpleEntry<String, Object>("exception", ((ConnectionEstablishExceptionEvent) event).getException()));
		} else if (event instanceof ServerStoppingEvent) {
			votifier.getLogger().printlnTranslation("s22");
		} else if (event instanceof ServerAwaitingTaskCompletionEvent) {
			votifier.getLogger().printlnTranslation("s54");
		} else if (event instanceof ServerStoppedEvent) {
			votifier.getLogger().printlnTranslation("s14");
		}
	}
}