package org.R4ZXRN3T.firstpass;

import javax.swing.*;
import java.awt.*;

public class Main {
	static void main() {
		start();
	}

	/** starts everything
	 * */
	static void start() {
		Firstpass app = new Firstpass();
		app.run();
	}

	/** restart the application
	 * */
	public static void restart(Firstpass old) {
		SwingUtilities.invokeLater(() -> {
			old.kill();
			Firstpass fresh = new Firstpass();
			fresh.run();
			JFrame f = fresh.getFrame();
			if (f != null) {
				// Forcefully clear any inherited minimized/iconified state
				int st = f.getExtendedState();
				f.setExtendedState((st & JFrame.ICONIFIED) != 0 ? JFrame.NORMAL : (st & ~JFrame.ICONIFIED) | JFrame.NORMAL);
				f.setVisible(true);
				f.toFront();
				f.requestFocus();
				// Focus nudge (some Windows setups need this)
				EventQueue.invokeLater(() -> {
					f.setAlwaysOnTop(true);
					f.setAlwaysOnTop(false);
				});
			}
		});
	}
}