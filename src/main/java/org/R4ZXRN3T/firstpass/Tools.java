package org.R4ZXRN3T.firstpass;

import javax.swing.*;
import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Tools {

	private static final int HASH_ITERATIONS = 250_000;

	/**
	 * Encode a password using SHA-256 with 250,000 iterations and a salt
	 *
	 * @param initialPassword The password to hash
	 * @param salt            Thesalt to use
	 * @return The hashed password
	 */
	public static String encodePassword(String initialPassword, String salt) {
		return encodePassword(initialPassword, salt, HASH_ITERATIONS);
	}

	/**
	 * Encode a password using SHA-256 with a specified number of iterations and a salt
	 *
	 * @param initialPassword The password to hash
	 * @param salt            The salt to use
	 * @param iterations      The number of iterations
	 * @return The hashed password
	 */
	public static String encodePassword(String initialPassword, String salt, int iterations) {
		if (iterations <= 0) return initialPassword;
		MessageDigest digest = getSHA256Digest();
		String current = initialPassword;
		for (int i = 0; i < iterations; i++) {
			digest.reset();
			byte[] hashed = digest.digest((current + salt).getBytes(StandardCharsets.UTF_8));
			current = toHex(hashed);
		}
		return current;
	}

	/**
	 * Convert a byte array to a hexadecimal string
	 *
	 * @param bytes The byte array
	 * @return The hexadecimal string
	 */
	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * Get a SHA-256 MessageDigest instance
	 *
	 * @return The MessageDigest instance
	 */
	private static MessageDigest getSHA256Digest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Generate a random string of specified length using alphanumeric characters
	 *
	 * @param length The length of the string
	 * @return The generated random string
	 */
	public static String generateRandomString(int length) {
		return generateRandomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	/**
	 * Generate a random string of specified length using the provided character set
	 *
	 * @param length       The length of the string
	 * @param characterSet The set of characters to use
	 * @return The generated random string
	 */
	public static String generateRandomString(int length, String characterSet) {
		StringBuilder randomString = new StringBuilder();
		SecureRandom rng = new SecureRandom();
		for (int i = 0; i < length; i++)
			randomString.append(characterSet.charAt(rng.nextInt(characterSet.length())));
		return randomString.toString();
	}

	/**
	 * Validate and escape special characters for XML
	 *
	 * @param input The input string
	 * @return The escaped string
	 */
	public static String validateForXML(String input) {
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	/**
	 * Return the original string by unescaping XML special characters
	 *
	 * @param input The escaped string
	 * @return The original string
	 */
	public static String returnOriginalValue(String input) {
		return input.replace("&amp;", "&")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;", "\"")
				.replace("&apos;", "'");
	}

	/**
	 * Compare two version strings
	 *
	 * @param v1 The first version string
	 * @param v2 The second version string
	 * @return if v1 > v2 returns 1 <br>
	 * if v1 < v2 returns -1 <br>
	 * if v1 == v2 returns 0
	 */
	public static int compareVersion(String v1, String v2) {
		String[] parts1 = v1.split("\\.");
		String[] parts2 = v2.split("\\.");
		int length = Math.max(parts1.length, parts2.length);
		for (int i = 0; i < length; i++) {
			int p1 = i < parts1.length ? Integer.parseInt(parts1[i]) : 0;
			int p2 = i < parts2.length ? Integer.parseInt(parts2[i]) : 0;
			if (p1 != p2) {
				return p1 > p2 ? 1 : -1;
			}
		}
		return 0; // versions are equal
	}

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
			fade.addActionListener(ev -> {
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