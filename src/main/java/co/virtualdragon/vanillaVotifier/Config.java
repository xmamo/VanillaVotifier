package co.virtualdragon.vanillaVotifier;

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

public interface Config {

	void load() throws IOException, InvalidKeySpecException;

	boolean isLoaded();

	int getConfigVersion();

	InetSocketAddress getInetSocketAddress();

	void setInetSocketAddress(InetSocketAddress inetSocketAddress);

	File getPublicKeyFile();

	void setPublicKeyFile(File location);

	File getPrivateKeyFile();

	void setPrivateKeyFile(File location);

	KeyPair getKeyPair();

	void setKeyPair(KeyPair keyPair);

	void genKeyPair();

	void genKeyPair(int keySize);

	List<String> getCommands();

	void setCommands(List<String> commands);

	InetSocketAddress getRconInetSocketAddress();

	void setRconInetSocketAddress(InetSocketAddress inetSocketAddress);

	String getRconPassword();

	void setRconPassword(String password);

	void save() throws IOException;
}
