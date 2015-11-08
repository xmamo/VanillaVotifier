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
