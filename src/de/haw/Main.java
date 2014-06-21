package de.haw;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class Main implements Runnable {
	public static long timeDelta = 0;

	private static Random rnd = new Random();

	private static String address;
	private static int port;
	private static char stationClass;
	private static Listener listener;
	private static Sender sender;

    public static void main(String[] args) throws IOException {
//		NetworkInterface.getByName(args[0]);
		address = args[0];
		port = Integer.parseInt(args[1]);
		stationClass = args[2].charAt(0);

		final byte[] buffer = new byte[Packet.PAYLOAD_LENGTH];
		readFromDataSource(buffer);

		listener = new Listener(address, port);
		sender = new Sender(address, port, stationClass, buffer);


		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				listener.close();
			}
		});
    }

	public static long getTime() {
		return System.currentTimeMillis() + timeDelta;
	}

	private static void start() {
		new Thread(listener).start();
		new Thread(new Main()).start();
	}

	public void run() {
		try {
			// Sleep till next Slot.
			long timeOffsetInSlot = (long) ((Listener.SLOT_LENGTH / 2) - ((getTime() % Listener.FRAME_LENGTH) % Listener.SLOT_LENGTH));
			if (timeOffsetInSlot >= 0) {
				Thread.sleep(timeOffsetInSlot);
			} else {
				Thread.sleep((long) (Listener.SLOT_LENGTH + timeOffsetInSlot));
			}

			int slot = getRandomUnusedSlot();
			sender.send(slot, 0);

			for (;;) {
				// Sleep till next Frame.
				Thread.sleep(Listener.FRAME_LENGTH - (getTime() % Listener.FRAME_LENGTH));

				// Get average time delta from A classes
				timeDelta = listener.endFrame();

				long timeToSend = Math.round(slot * Listener.SLOT_LENGTH + Listener.SLOT_LENGTH / 2) + timeDelta;
				if (timeToSend > 0) {
					slot = getRandomUnusedSlot();
					sender.send(slot, timeToSend);
				}
				listener.startFrame();
			}
		} catch (InterruptedException e) {
			// idc
		}
	}

	private static int getRandomUnusedSlot() {
		HashSet<Byte> diff = new HashSet<Byte>();
		for (int i = 0; i < Listener.SLOTS_PER_FRAME; i++) {
			diff.add((byte) (i + 1));
		}
		diff.removeAll(listener.usedSlots);
		return diff.toArray(new Byte[diff.size()])[rnd.nextInt(diff.size())];
	}

	private static void readFromDataSource(final byte[] buffer) {
		new Thread(new Runnable() {
			private boolean empty = true;

			public void run() {
				try {
					for (;;) {
						System.in.read(buffer);
						if (empty) {
							empty = false;
							start();
						}
					}
				} catch (IOException e) {
					System.out.println("Ausgeflüstert");
				}
			}
		}).start();
	}
}
