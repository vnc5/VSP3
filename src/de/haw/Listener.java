package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.nio.ByteBuffer;
import java.util.HashSet;

public class Listener implements Runnable {
	public static final int FRAME_LENGTH = 1000;
	public static final int SLOTS_PER_FRAME = 25;
	public static final double SLOT_LENGTH = FRAME_LENGTH / (double) SLOTS_PER_FRAME;

	private InetAddress address;
	private MulticastSocket socket;
	private DatagramPacket packet;

	private long frameStart;
	private int packetCount = 0; // of A classes per frame
	private long accTime = 0; // accumulated timestamps of A classes per frame
	private byte lastSlot = 0;
	private byte[] lastPacket = new byte[Packet.PACKET_LENGTH];
	public HashSet<Byte> usedSlots = new HashSet<Byte>(); // in next frame

	public Listener(final String address, final int port) throws IOException {
		this.address = InetAddress.getByName(address);
		packet = new DatagramPacket(new byte[Packet.PACKET_LENGTH], Packet.PACKET_LENGTH);
		socket = new MulticastSocket(port);
		socket.joinGroup(this.address);
	}

	public void run() {
		startFrame();
		try {
			for (;;) {
				socket.receive(packet);
				byte slot = getCurrentSlot();
				byte[] data = packet.getData();

				if (slot != lastSlot && lastSlot != 0) {
					process(lastPacket);
				}

				System.arraycopy(data, 0, lastPacket, 0, Packet.PACKET_LENGTH);
				lastSlot = slot;
				System.out.println("received: " + new String(data));
			}
		} catch (IOException e) {
			System.out.println("Socket closed");
		}
	}

	private void process(byte[] data) {
		char stationClass = (char) data[Packet.CLASS_INDEX];
		byte slot = data[Packet.SLOT_INDEX];
		long timestamp = ByteBuffer.wrap(data).getLong(Packet.TIMESTAMP_INDEX);

		if (stationClass == 'A') {
			packetCount++;
			accTime += timestamp;
		}

		usedSlots.add(slot);
	}

	public void startFrame() {
		frameStart = Main.getTime();
		packetCount = 0;
		accTime = 0;
		lastSlot = 0;
		usedSlots.clear();
	}

	public void setFrameStart(long timestamp) {
		frameStart = timestamp;
	}

	private byte getCurrentSlot() {
		long now = Main.getTime();
		return (byte) (Math.floor((now - frameStart) / SLOT_LENGTH) + 1);
	}

	public void close() {
		try {
			System.out.println("Leaving group");
			socket.leaveGroup(address);
			socket.close();
		} catch (IOException e) {
			// idc
		}
	}
}
