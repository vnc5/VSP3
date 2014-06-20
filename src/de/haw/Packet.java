package de.haw;

import java.nio.ByteBuffer;

public class Packet {
	public static final int PACKET_LENGTH = 34;
	public static final int PAYLOAD_LENGTH = 24;

	public static final int CLASS_INDEX = 0;
	public static final int SLOT_INDEX = 25;
	public static final int TIMESTAMP_INDEX = 26;

	private char stationClass;
	private byte[] payload;
	private byte slot;

	private final ByteBuffer bb = ByteBuffer.allocate(PACKET_LENGTH);

	Packet(char stationClass) {
		this.stationClass = stationClass;
	}

	public void setPayload(byte[] payload) {
		this.payload = payload;
	}

	public void setSlot(int slot) {
		this.slot = (byte) slot;
	}

	public byte[] getBytes() {
		bb.putChar(stationClass);
		bb.put(payload);
		bb.put(slot);
		bb.putLong(Main.getTime());
		byte[] output = bb.array();
		bb.clear();
		return output;
	}
}
