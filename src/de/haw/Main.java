package de.haw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class Main {

	private static final String ADDRESS = "225.10.1.2";
	private static final int PORT = 15008;

    public static void main(String[] args) throws IOException {
		final Reader reader = new Reader();
		new Thread(reader).start();

		final Listener listener = new Listener(ADDRESS, PORT);
		new Thread(listener).start();

		final ScheduledFuture<?> sender = Executors.newSingleThreadScheduledExecutor().scheduleAtFixedRate(new Sender(ADDRESS, PORT), 1, 1, TimeUnit.SECONDS);

		Runtime.getRuntime().addShutdownHook(new Thread() {
			public void run() {
				listener.close();
				sender.cancel(false);
			}
		});
    }
}
