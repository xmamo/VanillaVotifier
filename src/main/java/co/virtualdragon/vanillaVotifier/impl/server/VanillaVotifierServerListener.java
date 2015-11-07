package co.virtualdragon.vanillaVotifier.impl.server;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.impl.server.event.CommandResponseEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ComunicationExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ConnectionCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ConnectionClosedEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ConnectionEstablishExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ConnectionEstablishedEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ConnectionInputStreamCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.InvalidRequestEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.RconExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.SendingRconCommandEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ServerCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ServerStartedEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ServerStartingEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.ServerStoppingEvent;
import co.virtualdragon.vanillaVotifier.impl.server.event.VoteEvent;
import java.util.HashMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifierServerListener implements Listener {

	@Override
	public void onEvent(Event event, Votifier votifier) {
		if (event instanceof ServerStartingEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s1"));
		} else if (event instanceof ServerStartedEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s2"));
		} else if (event instanceof ConnectionEstablishedEvent) {
			ConnectionEstablishedEvent connectionEstablishedEvent = (ConnectionEstablishedEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", connectionEstablishedEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", connectionEstablishedEvent.getSocket().getPort() + "");
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s3")));
		} else if (event instanceof VoteEvent) {
			VoteEvent voteEvent = (VoteEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", voteEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", voteEvent.getSocket().getPort() + "");
			substitutions.put("service-name", voteEvent.getVote().getServiceName());
			substitutions.put("user-name", voteEvent.getVote().getUserName());
			substitutions.put("address", voteEvent.getVote().getAddress());
			substitutions.put("time-stamp", voteEvent.getVote().getTimeStamp());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s4")));
		} else if (event instanceof SendingRconCommandEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("command", ((SendingRconCommandEvent) event).getCommand());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s5")));
		} else if (event instanceof CommandResponseEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("response", ((CommandResponseEvent) event).getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s6")));
		} else if (event instanceof RconExceptionEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			Exception exception = ((RconExceptionEvent) event).getException();
			if (exception.getMessage() != null && exception.getMessage().equals("Invalid password.")) {
				votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s7"));
			} else {
				substitutions.put("exception", ExceptionUtils.getStackTrace(exception));
				votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s28")));
			}
		} else if (event instanceof InvalidRequestEvent) {
			InvalidRequestEvent invalidRequestEvent = (InvalidRequestEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", invalidRequestEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", invalidRequestEvent.getSocket().getPort() + "");
			substitutions.put("message", invalidRequestEvent.getMessage().replaceAll("\n", "\t"));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s8")));
		} else if (event instanceof ConnectionInputStreamCloseExceptionEvent) {
			ConnectionInputStreamCloseExceptionEvent socketInputStreamCloseException = (ConnectionInputStreamCloseExceptionEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", socketInputStreamCloseException.getSocket().getInetAddress().toString());
			substitutions.put("port", socketInputStreamCloseException.getSocket().getPort() + "");
			substitutions.put("exception", ExceptionUtils.getStackTrace(socketInputStreamCloseException.getException()));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s9")));
		} else if (event instanceof ConnectionClosedEvent) {
			ConnectionClosedEvent connectionClosedEvent = (ConnectionClosedEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", connectionClosedEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", connectionClosedEvent.getSocket().getPort() + "");
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s10")));
		} else if (event instanceof ConnectionCloseExceptionEvent) {
			ConnectionCloseExceptionEvent connectionCloseException = (ConnectionCloseExceptionEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", connectionCloseException.getSocket().getInetAddress().toString());
			substitutions.put("port", connectionCloseException.getSocket().getPort() + "");
			substitutions.put("exception", ExceptionUtils.getStackTrace(connectionCloseException.getException()));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s11")));
		} else if (event instanceof ComunicationExceptionEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(((ComunicationExceptionEvent) event).getException()));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s29")));
		} else if (event instanceof ConnectionEstablishExceptionEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(((ConnectionEstablishExceptionEvent) event).getException()));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s30")));
		} else if (event instanceof ServerCloseExceptionEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ExceptionUtils.getStackTrace(((ServerCloseExceptionEvent) event).getException()));
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s12")));
		} else if (event instanceof ServerStoppingEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s22"));
		} else if (event instanceof ServerStoppedEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s14"));
		}
	}
}
