package org.R4ZXRN3T.firstpass;

import de.mkammerer.argon2.Argon2;
import de.mkammerer.argon2.Argon2Factory;

import java.security.SecureRandom;

public class Tools {

	// Argon2id parameters – tune to taste (memory in KiB)
	private static final int ARGON2_ITERATIONS = 3;
	private static final int ARGON2_MEMORY_KIB = 65_536; // 64 MiB
	private static final int ARGON2_PARALLELISM = 1;

	/**
	 * Hash a password with Argon2id.
	 * The {@code salt} is used as a pepper (appended to the password before hashing)
	 * so that the stored salt value still contributes to security.
	 * The Argon2 library embeds its own cryptographic salt in the returned hash string.
	 *
	 * @param password The plaintext password
	 * @return The full Argon2id encoded hash string (e.g. {@code $argon2id$v=19$…})
	 */
	public static String encodePassword(String password) {
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
		char[] input = password.toCharArray();
		try {
			return argon2.hash(ARGON2_ITERATIONS, ARGON2_MEMORY_KIB, ARGON2_PARALLELISM, input);
		} finally {
			argon2.wipeArray(input);
		}
	}

	/**
	 * Verify a plaintext password against a stored Argon2id hash.
	 *
	 * @param password   The plaintext password to check
	 * @param storedHash The Argon2id encoded hash string from storage
	 * @return {@code true} if the password matches the stored hash
	 */
	public static boolean verifyPassword(String password, String storedHash) {
		if (storedHash == null || !storedHash.startsWith("$argon2")) return false;
		Argon2 argon2 = Argon2Factory.create(Argon2Factory.Argon2Types.ARGON2id);
		char[] input = password.toCharArray();
		try {
			return argon2.verify(storedHash, input);
		} finally {
			argon2.wipeArray(input);
		}
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
}