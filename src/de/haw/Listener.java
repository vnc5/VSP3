package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

public class Listener implements Runnable {
	private InetAddress address;
	private MulticastSocket socket;
	private DatagramPacket packet;

	public Listener(final String address, final int port) throws IOException {
		this.address = InetAddress.getByName(address);
		packet = new DatagramPacket(new byte[Packet.PACKET_LENGTH], Packet.PACKET_LENGTH);
		socket = new MulticastSocket(port);
		socket.joinGroup(this.address);
	}

	public void run() {
		try {
			for (;;) {
				socket.receive(packet);
				System.out.println("received: " + new String(packet.getData()));
			}
		} catch (IOException e) {
			System.out.println("Socket closed");
		}
	}

	public void close() {
		try {
			System.out.println("Leaving group");
			socket.leaveGroup(address);
			socket.close();
		} catch (IOException e) {

		}
	}
}
