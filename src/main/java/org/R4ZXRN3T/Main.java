package org.R4ZXRN3T;

import javax.swing.*;

public class Main {
	static void main() {
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