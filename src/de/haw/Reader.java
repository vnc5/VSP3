package de.haw;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Reader implements Runnable {
	public final char[] buffer = new char[24];

	public void run() {
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
			for (;;) {
				in.read(buffer);
			}
		} catch (IOException e) {
			System.out.println("Datenquelle kaputt");
		}
	}
}
