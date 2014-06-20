package de.haw;

import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {


    public static void main(String[] args) throws IOException {
//		NetworkInterface.getByName(args[0]);
		final String address = args[0];
		final int port = Integer.parseInt(args[1]);
		final String stationClass = args[2];

		final byte[] buffer = new byte[Packet.PAYLOAD_LENGTH];
		readFromDataSource(buffer);

		final Listener listener = new Listener(address, port);
		new Thread(listener).start();

		final ScheduledFuture<?> sender = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Sender(address, port), 1, 1, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				listener.close();
				sender.cancel(false);
			}
		});
    }

	private static void readFromDataSource(final byte[] buffer) {
		new Thread(new Runnable() {
			public void run() {
				try {
					for (;;) {
						System.in.read(buffer);
					}
				} catch (IOException e) {
					System.out.println("Ausgeflüstert");
				}
			}
		}).start();
	}
}
