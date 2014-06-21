package de.haw;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

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

	/**
	 * Fire and forget.
	 * @param slot
	 * @param delay
	 */
	public void send(final int slot, final long delay) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				try {
					Thread.sleep(delay);

					payload.setSlot(slot);
					packet.setData(payload.getBytes());
					try {
						socket.send(packet);
						System.out.println("send next slot: " + slot);
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
