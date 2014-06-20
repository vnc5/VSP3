package de.haw;

import java.io.IOException;
import java.util.Random;

public class Main {
	public static long timeDelta = 0;

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
		sender.send(new Random().nextInt(Listener.SLOTS_PER_FRAME + 1));
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
