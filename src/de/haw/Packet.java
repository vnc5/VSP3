package de.haw;

import java.nio.ByteBuffer;

public class Packet {
	public static final int PACKET_LENGTH = 34;
	public static final int PAYLOAD_LENGTH = 24;

	private char stationClass;
	private String payload;
	private byte slot;

	private final ByteBuffer bb = ByteBuffer.allocate(PACKET_LENGTH);


	Packet(char stationClass) {
		this.stationClass = stationClass;
	}

	public void setPayload(String payload) {
		this.payload = payload.substring(0, PAYLOAD_LENGTH);
	}

	public void setSlot(byte slot) {
		this.slot = slot;
	}

	public byte[] getBytes() {
		bb.putChar(stationClass);
		bb.put(payload.getBytes());
		bb.put(slot);
		bb.putLong(System.currentTimeMillis());
		byte[] output = bb.array();
		bb.clear();
		return output;
	}
}
