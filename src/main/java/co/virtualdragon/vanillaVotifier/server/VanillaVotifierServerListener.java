package co.virtualdragon.vanillaVotifier.server;

import co.virtualdragon.vanillaVotifier.Listener;
import co.virtualdragon.vanillaVotifier.VanillaVotifier;
import co.virtualdragon.vanillaVotifier.event.Event;
import co.virtualdragon.vanillaVotifier.server.event.BadRconEvent;
import co.virtualdragon.vanillaVotifier.server.event.CommandResponseEvent;
import co.virtualdragon.vanillaVotifier.server.event.ConnectionCloseException;
import co.virtualdragon.vanillaVotifier.server.event.ConnectionClosedEvent;
import co.virtualdragon.vanillaVotifier.server.event.ConnectionEstablishedEvent;
import co.virtualdragon.vanillaVotifier.server.event.ConnectionInputStreamCloseException;
import co.virtualdragon.vanillaVotifier.server.event.InvalidRequestEvent;
import co.virtualdragon.vanillaVotifier.server.event.SendingRconCommandEvent;
import co.virtualdragon.vanillaVotifier.server.event.ServerCloseExceptionEvent;
import co.virtualdragon.vanillaVotifier.server.event.ServerStartedEvent;
import co.virtualdragon.vanillaVotifier.server.event.ServerStartingEvent;
import co.virtualdragon.vanillaVotifier.server.event.ServerStoppedEvent;
import co.virtualdragon.vanillaVotifier.server.event.ServerStoppingEvent;
import co.virtualdragon.vanillaVotifier.server.event.VoteEvent;
import java.util.HashMap;
import org.apache.commons.lang3.text.StrSubstitutor;

public class VanillaVotifierServerListener implements Listener {

	@Override
	public void onEvent(Event event, VanillaVotifier votifier) {
		if (event instanceof ServerStartingEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s1"));
		} else if (event instanceof ServerStartedEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s2"));
		} else if (event instanceof VoteEvent) {
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
			substitutions.put("service-name", voteEvent.getServiceName());
			substitutions.put("user-name", voteEvent.getUserName());
			substitutions.put("address", voteEvent.getAddress());
			substitutions.put("time-stamp", voteEvent.getTimeStamp());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s4")));
		} else if (event instanceof SendingRconCommandEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("command", ((SendingRconCommandEvent) event).getCommand());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s5")));
		} else if (event instanceof CommandResponseEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("response", ((CommandResponseEvent) event).getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s6")));
		} else if (event instanceof BadRconEvent) {
			votifier.getOutputWriter().print(votifier.getLanguagePack().getString("s7"));
		} else if (event instanceof InvalidRequestEvent) {
			InvalidRequestEvent invalidRequestEvent = (InvalidRequestEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", invalidRequestEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", invalidRequestEvent.getSocket().getPort() + "");
			substitutions.put("message", invalidRequestEvent.getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s8")));
		} else if (event instanceof ConnectionInputStreamCloseException) {
			ConnectionInputStreamCloseException socketInputStreamCloseException = (ConnectionInputStreamCloseException) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", socketInputStreamCloseException.getSocket().getInetAddress().toString());
			substitutions.put("port", socketInputStreamCloseException.getSocket().getPort() + "");
			substitutions.put("exception", socketInputStreamCloseException.getException().getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s9")));
		} else if (event instanceof ConnectionClosedEvent) {
			ConnectionClosedEvent connectionClosedEvent = (ConnectionClosedEvent) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", connectionClosedEvent.getSocket().getInetAddress().toString());
			substitutions.put("port", connectionClosedEvent.getSocket().getPort() + "");
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s10")));
		} else if (event instanceof ConnectionCloseException) {
			ConnectionCloseException connectionCloseException = (ConnectionCloseException) event;
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("ip", connectionCloseException.getSocket().getInetAddress().toString());
			substitutions.put("port", connectionCloseException.getSocket().getPort() + "");
			substitutions.put("exception", connectionCloseException.getException().getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s11")));
		} else if (event instanceof ServerCloseExceptionEvent) {
			HashMap<String, String> substitutions = new HashMap<String, String>();
			substitutions.put("exception", ((ServerCloseExceptionEvent) event).getException().getMessage());
			votifier.getOutputWriter().println(new StrSubstitutor(substitutions).replace(votifier.getLanguagePack().getString("s12")));
		} else if (event instanceof ServerStoppingEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s22"));
		} else if (event instanceof ServerStoppedEvent) {
			votifier.getOutputWriter().println(votifier.getLanguagePack().getString("s14"));
		}
	}
}
