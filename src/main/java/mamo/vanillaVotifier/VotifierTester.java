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

import java.io.IOException;
import java.net.Socket;
import java.net.SocketOptions;
import java.security.GeneralSecurityException;
import javax.crypto.Cipher;
import mamo.vanillaVotifier.util.RsaUtils;

public class VotifierTester {
	private final VanillaVotifier votifier;

	public VotifierTester(VanillaVotifier votifier) {
		this.votifier = votifier;
	}

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

	public void testQuery(String message) throws GeneralSecurityException, IOException {
		synchronized (votifier.getConfig()) {
			Cipher cipher = RsaUtils.getEncryptCipher(votifier.getConfig().getKeyPair().getPublic());
			Socket socket = new Socket(votifier.getConfig().getInetSocketAddress().getAddress(), votifier.getConfig().getInetSocketAddress().getPort());
			socket.setSoTimeout(SocketOptions.SO_TIMEOUT);
			socket.getOutputStream().write(cipher.doFinal(message.getBytes()));
			socket.getOutputStream().flush();
			socket.close();
		}
	}
}