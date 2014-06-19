package de.haw;

import java.io.IOException;

public class Reader implements Runnable {
	public final byte[] buffer = new byte[24];

	public void run() {
		try {
			for (;;) {
				System.in.read(buffer);
			}
		} catch (IOException e) {
			System.out.println("Datenquelle kaputt");
		}
	}
}
