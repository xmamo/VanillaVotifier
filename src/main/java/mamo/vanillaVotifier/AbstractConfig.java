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

import mamo.vanillaVotifier.exception.InvalidPrivateKeyFileException;
import mamo.vanillaVotifier.exception.InvalidPublicKeyFileException;
import mamo.vanillaVotifier.exception.PrivateKeyFileNotFoundException;
import mamo.vanillaVotifier.exception.PublicKeyFileNotFoundException;
import mamo.vanillaVotifier.utils.RsaUtils;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.bouncycastle.util.io.pem.PemWriter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.*;
import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractConfig implements Config {
	@NotNull protected File configFile;
	protected int configVersion;
	@Nullable protected File logFile;
	@Nullable protected File logDirectory;
	@Nullable protected InetSocketAddress inetSocketAddress;
	@Nullable protected File publicKeyFile;
	@Nullable protected File privateKeyFile;
	@Nullable protected KeyPair keyPair;
	@NotNull protected ArrayList<VoteAction> voteActions = new ArrayList<VoteAction>();

	public AbstractConfig(@NotNull File configFile) {
		this.configFile = configFile;
	}

	@Override
	public synchronized int getConfigVersion() {
		return configVersion;
	}

	@Override
	@Nullable
	public synchronized File getLogFile() {
		return logFile;
	}

	@Override
	public synchronized void setLogFile(@NotNull File location) {
		logFile = location;
	}

	@Override
	@Nullable
	public File getLogDirectory() {
		return logDirectory;
	}

	@Override
	public void setLogDirectory(@NotNull File location) {
		logDirectory = location;
	}

	@Override
	@Nullable
	public synchronized InetSocketAddress getInetSocketAddress() {
		return inetSocketAddress;
	}

	@Override
	public synchronized void setInetSocketAddress(@NotNull InetSocketAddress inetSocketAddress) {
		this.inetSocketAddress = inetSocketAddress;
	}

	@Override
	@Nullable
	public synchronized File getPublicKeyFile() {
		return publicKeyFile;
	}

	@Override
	public synchronized void setPublicKeyFile(@NotNull File location) {
		publicKeyFile = location;
	}

	@Override
	@Nullable
	public synchronized File getPrivateKeyFile() {
		return privateKeyFile;
	}

	@Override
	public synchronized void setPrivateKeyFile(@NotNull File location) {
		privateKeyFile = location;
	}

	@Override
	@Nullable
	public synchronized KeyPair getKeyPair() {
		return keyPair;
	}

	@Override
	public synchronized void setKeyPair(@NotNull KeyPair keyPair) {
		this.keyPair = keyPair;
	}

	@Override
	public synchronized void generateKeyPair() {
		generateKeyPair(2048);
	}

	@Override
	public synchronized void generateKeyPair(int keySize) {
		setKeyPair(RsaUtils.genKeyPair(keySize));
	}

	@Override
	@NotNull
	public List<VoteAction> getVoteActions() {
		return voteActions;
	}

	protected void copyDefaultConfig(InputStream in) throws IOException {
		BufferedReader reader = new BufferedReader(new InputStreamReader(in));
		StringBuilder stringBuilder = new StringBuilder();
		int i;
		while ((i = reader.read()) != -1) {
			stringBuilder.append((char) i);
		}
		reader.close();
		BufferedWriter writer = new BufferedWriter(new FileWriter(configFile));
		for (char c : stringBuilder.toString().replaceAll("\\u000D\\u000A|[\\u000A\\u000B\\u000C\\u000D\\u0085\\u2028\\u2029]", System.getProperty("line.separator")).toCharArray()) {
			writer.write((int) c);
		}
		writer.flush();
		writer.close();
	}

	protected void loadKeyPair() throws IOException, InvalidKeySpecException {
		if (!publicKeyFile.exists() && !privateKeyFile.exists()) {
			generateKeyPair();
			saveKeyPair();
		}
		if (!publicKeyFile.exists()) {
			throw new PublicKeyFileNotFoundException();
		}
		if (!privateKeyFile.exists()) {
			throw new PrivateKeyFileNotFoundException();
		}
		PemReader publicKeyPemReader = new PemReader(new BufferedReader(new FileReader(publicKeyFile)));
		PemReader privateKeyPemReader = new PemReader(new BufferedReader(new FileReader(privateKeyFile)));
		PemObject publicPemObject = publicKeyPemReader.readPemObject();
		if (publicPemObject == null) {
			throw new InvalidPublicKeyFileException();
		}
		PemObject privatePemObject = privateKeyPemReader.readPemObject();
		if (privatePemObject == null) {
			throw new InvalidPrivateKeyFileException();
		}
		keyPair = new KeyPair(RsaUtils.bytesToPublicKey(publicPemObject.getContent()), RsaUtils.bytesToPrivateKey(privatePemObject.getContent()));
		publicKeyPemReader.close();
		privateKeyPemReader.close();
	}

	@Override
	public synchronized void saveKeyPair() throws IOException {
		PemWriter publicPemWriter = new PemWriter(new BufferedWriter(new FileWriter(getPublicKeyFile())));
		publicPemWriter.writeObject(new PemObject("PUBLIC KEY", getKeyPair().getPublic().getEncoded()));
		publicPemWriter.flush();
		publicPemWriter.close();
		PemWriter privatePemWriter = new PemWriter(new BufferedWriter(new FileWriter(getPrivateKeyFile())));
		privatePemWriter.writeObject(new PemObject("RSA PRIVATE KEY", getKeyPair().getPrivate().getEncoded()));
		privatePemWriter.flush();
		privatePemWriter.close();
	}
}