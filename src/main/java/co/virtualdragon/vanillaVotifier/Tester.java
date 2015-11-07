package co.virtualdragon.vanillaVotifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

public interface Tester {

	void testVote(Vote vote) throws GeneralSecurityException, IOException;

	void testQuery(String message) throws GeneralSecurityException, IOException;
}
