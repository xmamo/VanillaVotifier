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

import java.io.File;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public class YamlConfig implements Config {
	private final File configFile;

	private boolean loaded;
	private int configVersion;
	private File logFile;
	private InetSocketAddress inetSocketAddress;
	private File publicKeyFile;
	private File privateKeyFile;
	private KeyPair keyPair;
	private ArrayList<RconConfig> rconConfigs;

	public YamlConfig(File configFile) {
		this.configFile = configFile;
	}

	@Override public void load() throws IOException, InvalidKeySpecException {

	}

	@Override public boolean isLoaded() {
		return false;
	}

	@Override public int getConfigVersion() {
		return 0;
	}

	@Override public File getLogFile() {
		return null;
	}

	@Override public void setLogFile(File location) {

	}

	@Override public InetSocketAddress getInetSocketAddress() {
		return null;
	}

	@Override public void setInetSocketAddress(InetSocketAddress inetSocketAddress) {

	}

	@Override public File getPublicKeyFile() {
		return null;
	}

	@Override public void setPublicKeyFile(File location) {

	}

	@Override public File getPrivateKeyFile() {
		return null;
	}

	@Override public void setPrivateKeyFile(File location) {

	}

	@Override public KeyPair getKeyPair() {
		return null;
	}

	@Override public void setKeyPair(KeyPair keyPair) {

	}

	@Override public void genKeyPair() {

	}

	@Override public void genKeyPair(int keySize) {

	}

	@Override public void save() throws IOException {

	}

	@Override public List<RconConfig> getRconConfigs() {
		return null;
	}
}