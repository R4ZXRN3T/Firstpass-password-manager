package org.R4ZXRN3T;

import javax.swing.*;

public class Main {
	public static void main(String[] args) {
		start();
	}

	static void start() {
		Firstpass app = new Firstpass();
		app.run();
	}

	public static void restart(Firstpass old) {
		SwingUtilities.invokeLater(() -> {
			old.kill();
			Firstpass fresh = new Firstpass();
			fresh.run();
		});
	}
}