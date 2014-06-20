package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender {
	private MulticastSocket socket;
	private DatagramPacket packet;
	private Packet payload;

	public Sender(final String address, final int port, char stationClass, final byte[] dataSource) throws IOException {
		payload = new Packet(stationClass);
		payload.setPayload(dataSource);
		packet = new DatagramPacket(new byte[0], 0, InetAddress.getByName(address), port);

		socket = new MulticastSocket();
		socket.setTimeToLive(1);
	}

	public void send(int slot) {
		payload.setSlot(slot);
		packet.setData(payload.getBytes());
		try {
			socket.send(packet);
			System.out.println("send: " + new String(packet.getData()));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
