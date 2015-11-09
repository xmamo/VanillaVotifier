/* 
 * Copyright (C) 2015 VirtualDragon
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
