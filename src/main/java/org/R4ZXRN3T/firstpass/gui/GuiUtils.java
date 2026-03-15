package org.R4ZXRN3T.firstpass.gui;

import javax.swing.*;
import java.awt.*;

public class GuiUtils {
	/**
	 * Show a toast message on the given parent window
	 *
	 * @param parent     The parent window
	 * @param message    The message to show
	 * @param durationMs The duration to show the message in milliseconds
	 * @param darkMode   Whether to use dark mode colors
	 */
	public static void showToast(Window parent, String message, int durationMs, boolean darkMode) {
		showToast(parent, message, durationMs, darkMode, 200);
	}

	/**
	 * Show a toast message on the given parent window with a vertical offset
	 *
	 * @param parent     The parent window
	 * @param message    The message to show
	 * @param durationMs The duration to show the message in milliseconds
	 * @param darkMode   Whether to use dark mode colors
	 * @param yOffset    The vertical offset from the bottom of the parent window
	 */
	public static void showToast(Window parent, String message, int durationMs, boolean darkMode, int yOffset) {
		if (parent == null) return;

		JWindow toast = new JWindow(parent);

		int arc = 16;
		JPanel content = getJPanel(message, darkMode, arc);

		toast.setContentPane(content);
		toast.pack();

		// Apply a rounded shape to the window (keeps the window opaque but rounded)
		toast.setShape(new java.awt.geom.RoundRectangle2D.Float(0, 0, toast.getWidth(), toast.getHeight(), arc, arc));

		int x = parent.getX() + (parent.getWidth() - toast.getWidth()) / 2;
		int y = parent.getY() + parent.getHeight() - toast.getHeight() - yOffset;
		toast.setLocation(x, y);
		toast.setAlwaysOnTop(true);
		toast.setFocusableWindowState(false);

		toast.setVisible(true);

		int fadeDelay = 50;
		int fadeSteps = 10;
		int stay = Math.max(0, durationMs - fadeDelay * fadeSteps);

		// stay timer -> then fade timer (fade uses window visibility; avoid window.setOpacity to preserve ClearType)
		new Timer(stay, e -> {
			((Timer) e.getSource()).stop();
			Timer fade = new Timer(fadeDelay, null);
			final int[] step = {0};
			fade.addActionListener(_ -> {
				step[0]++;
				if (step[0] >= fadeSteps) {
					fade.stop();
					toast.setVisible(false);
					toast.dispose();
				} else {
					float opacity = 1.0f - (float) step[0] / fadeSteps;
					toast.setOpacity(Math.max(0.0f, Math.min(1.0f, opacity)));
				}
			});
			fade.start();
		}).start();
	}

	/**
	 * Create a JPanel with rounded corners and anti-aliased text
	 *
	 * @param message  The message to display
	 * @param darkMode Whether to use dark mode colors
	 * @param arc      The corner arc size
	 * @return The created JPanel
	 */
	private static JPanel getJPanel(String message, boolean darkMode, int arc) {
		Color bgColor = darkMode ? new Color(50, 50, 50) : new Color(240, 240, 240);
		Color fgColor = darkMode ? Color.WHITE : Color.BLACK;

		// Custom panel that paints a rounded opaque background and enables AA for text
		JPanel content = new JPanel() {
			@Override
			protected void paintComponent(Graphics g) {
				Graphics2D g2 = (Graphics2D) g.create();
				try {
					g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
					g2.setColor(bgColor);
					g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
				} finally {
					g2.dispose();
				}
			}

			@Override
			public boolean isOpaque() {
				// Keep panel opaque so the window can remain opaque (preserves ClearType)
				return true;
			}
		};

		content.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
		content.setLayout(new GridBagLayout());

		JLabel label = new JLabel(message);
		label.setForeground(fgColor);
		label.setOpaque(false); // background drawn by parent
		content.add(label);
		return content;
	}
}