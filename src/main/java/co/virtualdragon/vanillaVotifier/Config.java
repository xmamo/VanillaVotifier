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
import java.util.concurrent.CopyOnWriteArrayList;

public interface Config {

	void load() throws IOException, InvalidKeySpecException;

	boolean isLoaded();

	int getConfigVersion();

	File getLogFile();

	void setLogFile(File location);

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

	void save() throws IOException;

	List<RconConfig> getRconConfigs();

	public static interface RconConfig {

		InetSocketAddress getInetSocketAddress();

		void setInetSocketAddress(InetSocketAddress inetSocketAddress);

		String getPassword();

		void setPassword(String password);

		List<String> getCommands();
	}

	public static class VanillaVotifierRconConfig implements RconConfig {

		private InetSocketAddress inetSocketAddress;
		private String password;
		private CopyOnWriteArrayList<String> commands;

		{
			commands = new CopyOnWriteArrayList<String>();
		}

		public VanillaVotifierRconConfig(InetSocketAddress inetSocketAddress, String password) {
			this.inetSocketAddress = inetSocketAddress;
			this.password = password;
		}

		@Override
		public synchronized InetSocketAddress getInetSocketAddress() {
			return inetSocketAddress;
		}

		@Override
		public synchronized void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
			if (inetSocketAddress == null) {
				inetSocketAddress = new InetSocketAddress("127.0.0.1", 8192);
			}
			this.inetSocketAddress = inetSocketAddress;
		}

		@Override
		public synchronized String getPassword() {
			return password;
		}

		@Override
		public synchronized void setPassword(String password) {
			this.password = password;
		}

		@Override
		public List<String> getCommands() {
			return commands;
		}
	}
}
