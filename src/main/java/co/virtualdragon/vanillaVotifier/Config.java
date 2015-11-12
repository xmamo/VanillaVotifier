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
import java.util.ArrayList;
import java.util.List;

public interface Config {

	void load() throws Exception;

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

	void save() throws IOException;

	List<RconConfig> getRconConfigs();

	public static interface RconConfig {

		InetSocketAddress getInetSocketAddress();

		void setInetSocketAddress(InetSocketAddress inetSocketAddress);

		String getPassword();

		void setPassword(String password);

		List<String> getCommands();
	}

	public static class AbstractRconConfig implements RconConfig {

		private InetSocketAddress inetSocketAddress;
		private String password;
		private ArrayList<String> commands;

		public AbstractRconConfig(InetSocketAddress inetSocketAddress, String password) {
			this(inetSocketAddress, password, new ArrayList<String>());
		}

		public AbstractRconConfig(InetSocketAddress inetSocketAddress, String password, ArrayList<String> commands) {
			this.inetSocketAddress = inetSocketAddress;
			this.password = password;
			if (commands == null) {
				commands = new ArrayList<String>();
			}
			this.commands = commands;
		}

		@Override
		public InetSocketAddress getInetSocketAddress() {
			return inetSocketAddress;
		}

		@Override
		public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
			if (inetSocketAddress == null) {
				inetSocketAddress = new InetSocketAddress("127.0.0.1", 8192);
			}
			this.inetSocketAddress = inetSocketAddress;
		}

		@Override
		public String getPassword() {
			return password;
		}

		@Override
		public void setPassword(String password) {
			this.password = password;
		}

		@Override
		public ArrayList<String> getCommands() {
			return commands;
		}
	}
}
