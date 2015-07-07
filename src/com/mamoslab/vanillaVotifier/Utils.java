package com.mamoslab.vanillaVotifier;

import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.Cipher;

public class Utils {
	
	private static final Logger LOGGER;
	
	static {
		LOGGER = Logger.getLogger(Utils.class.getName());
		LOGGER.setLevel(Level.ALL);
	}

	public static String keyToString(Key key) {
		return new String(Base64.getEncoder().encode(key.getEncoded()));
	}

	public static PublicKey stringToPublicKey(String string) throws Exception {
		try {
			byte[] bytes = Base64.getDecoder().decode(string);
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
		return null;
	}

	public static PrivateKey stringToPrivateKey(String string) throws Exception {
		try {
			byte[] bytes = Base64.getDecoder().decode(string);
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
		return null;
	}

	public static Cipher getEncryptCipher() throws Exception {
		Cipher cipher;
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, VanillaVotifier.getConfigHandler().getVotifierRSAPublicKey());
		return cipher;
	}

	public static Cipher getDecryptCipher() throws Exception {
		Cipher cipher;
		cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.DECRYPT_MODE, VanillaVotifier.getConfigHandler().getVotifierRSAPrivateKey());
		return cipher;
	}
}
