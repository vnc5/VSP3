package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Sender implements Runnable {
	private MulticastSocket socket;
	private DatagramPacket packet;

	public Sender(final String address, final int port) throws IOException {
		byte[] msg = "asd".getBytes();
		packet = new DatagramPacket(msg, 3, InetAddress.getByName(address), port);

		socket = new MulticastSocket();
		socket.setTimeToLive(1);
	}

	public void run() {
		try {
			socket.send(packet);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
