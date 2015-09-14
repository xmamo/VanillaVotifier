package co.virtualdragon.vanillaVotifier.util;

import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import javax.crypto.Cipher;

public class RsaUtils {

	public static KeyPair genKeyPair(int keySize) {
		try {
			KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
			keyPairGenerator.initialize(keySize);
			return keyPairGenerator.genKeyPair();
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
		return null;
	}

	public static PublicKey bytesToPublicKey(byte[] bytes) throws InvalidKeySpecException {
		try {
			X509EncodedKeySpec keySpec = new X509EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePublic(keySpec);
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
		return null;
	}

	public static PrivateKey bytesToPrivateKey(byte[] bytes) throws InvalidKeySpecException {
		try {
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(bytes);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			return kf.generatePrivate(keySpec);
		} catch (NoSuchAlgorithmException e) {
			// Can't happen
		}
		return null;
	}

	public static Cipher getEncryptCipher(PublicKey key) throws InvalidKeyException {
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			return cipher;
		} catch (GeneralSecurityException e) { // NoSuchAlgorithmException and NoSuchPaddingException
			// Can't happen
		}
		return null;
	}

	public static Cipher getDecryptCipher(PrivateKey key) throws InvalidKeyException {
		try {
			Cipher cipher;
			cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, key);
			return cipher;
		} catch (GeneralSecurityException e) { // NoSuchAlgorithmException and NoSuchPaddingException
			// Can't happen
		}
		return null;
	}
}
