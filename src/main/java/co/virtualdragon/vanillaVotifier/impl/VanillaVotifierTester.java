package co.virtualdragon.vanillaVotifier.impl;

import co.virtualdragon.vanillaVotifier.Tester;
import co.virtualdragon.vanillaVotifier.Vote;
import co.virtualdragon.vanillaVotifier.Votifier;
import co.virtualdragon.vanillaVotifier.util.RsaUtils;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;

public class VanillaVotifierTester implements Tester {

	private final Votifier votifier;

	public VanillaVotifierTester(Votifier votifier) {
		this.votifier = votifier;
	}

	@Override
	public void testVote(Vote vote) throws GeneralSecurityException, IOException {
		String message = "VOTE\n";
		if (vote.getServiceName() != null) {
			message += vote.getServiceName();
		}
		message += "\n";
		if (vote.getUserName() != null) {
			message += vote.getUserName();
		}
		message += "\n";
		if (vote.getAddress() != null) {
			message += vote.getAddress();
		}
		message += "\n";
		if (vote.getTimeStamp() != null) {
			message += vote.getTimeStamp();
		}
		testQuery(message);
	}

	@Override
	public void testQuery(String message) throws GeneralSecurityException, IOException {
		Cipher cipher = RsaUtils.getEncryptCipher(votifier.getConfig().getKeyPair().getPublic());
		Socket socket = new Socket(votifier.getConfig().getInetSocketAddress().getAddress(), votifier.getConfig().getInetSocketAddress().getPort());
		socket.getOutputStream().write(cipher.doFinal(message.getBytes()));
		socket.getOutputStream().flush();
		socket.close();
	}
}
