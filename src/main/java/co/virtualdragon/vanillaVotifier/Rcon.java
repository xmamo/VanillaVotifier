package co.virtualdragon.vanillaVotifier;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface Rcon {

	int getRequestId();
	
	void connect() throws IOException;
	
	boolean isConnected();

	Packet logIn(String password) throws UnsupportedEncodingException, IOException;

	Packet sendRequest(Packet request) throws UnsupportedEncodingException, IOException;

	public static class Packet {

		private int length;
		private int requestId;
		private Type type;
		private String payload;

		public Packet(int requestId, Type type, String payload) {
			this(Integer.SIZE / 8 + Integer.SIZE / 8 + payload.length() + Byte.SIZE / 8 * 2, requestId, type, payload);
		}

		public Packet(int length, int requestId, Type type, String payload) {
			this.length = length;
			this.requestId = requestId;
			this.type = type;
			this.payload = payload;
		}

		public int getLength() {
			return length;
		}

		public void setLength(int length) {
			this.length = length;
		}

		public int getRequestId() {
			return requestId;
		}

		public void setRequestId(int requestId) {
			this.requestId = requestId;
		}

		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public String getPayload() {
			return payload;
		}

		public void setPayload(String payload) {
			this.payload = payload;
		}

		@Override
		public String toString() {
			return length + "\t" + requestId + "\t" + type.toInt() + "\t" + payload;
		}

		public static enum Type {

			COMMAND_RESPONSE(0), COMMAND(2), LOG_IN(3);

			private int i;

			private Type(int i) {
				this.i = i;
			}

			public int toInt() {
				return i;
			}

			public static Type fromInt(int i) {
				if (i == 0) {
					return COMMAND_RESPONSE;
				} else if (i == 2) {
					return COMMAND;
				} else if (i == 3) {
					return LOG_IN;
				} else {
					throw new IllegalArgumentException("i has to be equal to 0, 2, or 3!");
				}
			}
		}
	}
}
