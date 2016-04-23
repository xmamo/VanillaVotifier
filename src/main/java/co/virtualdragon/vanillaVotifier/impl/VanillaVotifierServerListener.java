/*
 * Copyright (C) 2015 Matteo Morena
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation, either version 3 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program. If not, see <http://www.gnu.org/licenses/>.
 */
package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.event.server.ComunicationExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionClosedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionEstablishedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ConnectionInputStreamCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.DecryptInputExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.InvalidRequestEvent;
import co.virtualdragon.vanillaVotifier.event.server.RconCommandResponseEvent;
import co.virtualdragon.vanillaVotifier.event.server.RconExceptionEvent;
import co.virtualdragon.vanillaVotifier.event.server.SendingRconCommandEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerAwaitingTaskCompletionEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStartingEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.event.server.ServerStoppingEvent;
import co.virtualdragon.vanillaVotifier.event.server.VoteEvent;
import co.virtualdragon.vanillaVotifier.exception.InvalidRconPasswordException;
import java.net.ConnectException;
import java.util.AbstractMap.SimpleEntry;

public class VanillaVotifierServerListener implements Listener {

	@Override
	public void onEvent(Event event, Votifier votifier) {
		if (event instanceof ServerStartingEvent) {
			votifier.getLogger().printlnTranslation("s1");
		} else if (event instanceof ServerStartedEvent) {
			votifier.getLogger().printlnTranslation("s2");
		} else if (event instanceof ConnectionEstablishedEvent) {
			ConnectionEstablishedEvent connectionEstablishedEvent = (ConnectionEstablishedEvent) event;
			votifier.getLogger().printlnTranslation("s3",
					new SimpleEntry<String, Object>("ip", connectionEstablishedEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", connectionEstablishedEvent.getSocket().getPort()));
		} else if (event instanceof VoteEvent) {
			VoteEvent voteEvent = (VoteEvent) event;
			votifier.getLogger().printlnTranslation("s4",
					new SimpleEntry<String, Object>("ip", voteEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", voteEvent.getSocket().getPort()),
					new SimpleEntry<String, Object>("service-name", voteEvent.getVote().getServiceName()),
					new SimpleEntry<String, Object>("user-name", voteEvent.getVote().getUserName()),
					new SimpleEntry<String, Object>("address", voteEvent.getVote().getAddress()),
					new SimpleEntry<String, Object>("time-stamp", voteEvent.getVote().getTimeStamp()));
		} else if (event instanceof SendingRconCommandEvent) {
			SendingRconCommandEvent sendingRconCommandEvent = (SendingRconCommandEvent) event;
			votifier.getLogger().printlnTranslation("s5",
					new SimpleEntry<String, Object>("ip", sendingRconCommandEvent.getRcon().getRconConfig().getInetSocketAddress().getAddress().toString()),
					new SimpleEntry<String, Object>("port", sendingRconCommandEvent.getRcon().getRconConfig().getInetSocketAddress().getPort()),
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
		} else if (event instanceof InvalidRequestEvent) {
			InvalidRequestEvent invalidRequestEvent = (InvalidRequestEvent) event;
			votifier.getLogger().printlnTranslation("s8",
					new SimpleEntry<String, Object>("ip", invalidRequestEvent.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", invalidRequestEvent.getSocket().getPort()),
					new SimpleEntry<String, Object>("message", invalidRequestEvent.getMessage().replaceAll("\n", "\t")));
		} else if (event instanceof ConnectionInputStreamCloseExceptionEvent) {
			ConnectionInputStreamCloseExceptionEvent socketInputStreamCloseException = (ConnectionInputStreamCloseExceptionEvent) event;
			votifier.getLogger().printlnTranslation("s9",
					new SimpleEntry<String, Object>("ip", socketInputStreamCloseException.getSocket().getInetAddress().getHostAddress()),
					new SimpleEntry<String, Object>("port", socketInputStreamCloseException.getSocket().getPort()),
					new SimpleEntry<String, Object>("exception", socketInputStreamCloseException.getException()));
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
		} else if (event instanceof ComunicationExceptionEvent) {
			votifier.getLogger().printlnTranslation("s29", new SimpleEntry<String, Object>("exception", ((ComunicationExceptionEvent) event).getException()));
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
