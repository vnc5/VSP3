package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;

public class Sender {
	private final MulticastSocket socket;
	private final DatagramPacket packet;
	private final Packet payload;

	public Sender(final String address, final int port, char stationClass,
			final byte[] dataSource, NetworkInterface networkInterface) throws IOException {
		payload = new Packet(stationClass);
		payload.setPayload(dataSource);
		packet = new DatagramPacket(new byte[0], 0,
				InetAddress.getByName(address), port);

		socket = new MulticastSocket();
        socket.setNetworkInterface(networkInterface);
		socket.setTimeToLive(1);
	}

	public void send(final int slot) {
		payload.setSlot(slot);
		packet.setData(payload.getBytes());
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
