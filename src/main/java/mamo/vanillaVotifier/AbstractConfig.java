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
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.List;
import mamo.vanillaVotifier.util.RsaUtils;

public abstract class AbstractConfig implements Config {
	protected File configFile;
	protected boolean loaded;
	protected int configVersion;
	@Deprecated protected File logFile;
	protected File logDirectory;
	protected InetSocketAddress inetSocketAddress;
	protected File publicKeyFile;
	protected File privateKeyFile;
	protected KeyPair keyPair;
	protected ArrayList<RconConfig> rconConfigs;

	@Override
	public synchronized boolean isLoaded() {
		return loaded;
	}

	@Override
	public synchronized int getConfigVersion() {
		checkState();
		return configVersion;
	}

	@Override
	public synchronized File getLogFile() {
		checkState();
		return logFile;
	}

	@Override
	public synchronized void setLogFile(File location) {
		checkState();
		if (location == null) {
			location = new File("log.log");
		}
		logFile = location;
	}

	@Override
	public File getLogDirectory() {
		checkState();
		return logDirectory;
	}

	@Override
	public void setLogDirectory(File location) {
		checkState();
		if (location == null) {
			location = new File("logs");
		}
		logDirectory = location;
	}

	@Override
	public synchronized InetSocketAddress getInetSocketAddress() {
		checkState();
		return inetSocketAddress;
	}

	@Override
	public synchronized void setInetSocketAddress(InetSocketAddress inetSocketAddress) {
		checkState();
		if (inetSocketAddress == null) {
			inetSocketAddress = new InetSocketAddress("127.0.0.1", 8192);
		}
		this.inetSocketAddress = inetSocketAddress;
	}

	@Override
	public synchronized File getPublicKeyFile() {
		checkState();
		return publicKeyFile;
	}

	@Override
	public synchronized void setPublicKeyFile(File location) {
		checkState();
		if (location == null) {
			location = new File("public.pem");
		}
		publicKeyFile = location;
	}

	@Override
	public synchronized File getPrivateKeyFile() {
		checkState();
		return privateKeyFile;
	}

	@Override
	public synchronized void setPrivateKeyFile(File location) {
		checkState();
		if (location == null) {
			location = new File("private.pem");
		}
		privateKeyFile = location;
	}

	@Override
	public synchronized KeyPair getKeyPair() {
		checkState();
		return keyPair;
	}

	@Override
	public synchronized void setKeyPair(KeyPair keyPair) {
		checkState();
		if (keyPair == null) {
			keyPair = RsaUtils.genKeyPair(2048);
		}
		this.keyPair = keyPair;
	}

	@Override
	public synchronized void genKeyPair() {
		genKeyPair(2048);
	}

	@Override
	public synchronized void genKeyPair(int keySize) {
		setKeyPair(RsaUtils.genKeyPair(keySize));
	}

	@Override
	public synchronized List<RconConfig> getRconConfigs() {
		return rconConfigs;
	}

	private void checkState() {
		if (!isLoaded()) {
			throw new IllegalStateException("Config isn't loaded yet!");
		}
	}
}