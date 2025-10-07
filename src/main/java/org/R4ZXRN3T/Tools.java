package org.R4ZXRN3T;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Tools {

	private static final int HASH_ITERATIONS = 250_000;

	// Backwards compatible: single iteration
	public static String encodePassword(String initialPassword, String salt) {
		return encodePassword(initialPassword, salt, HASH_ITERATIONS);
	}

	// Multi-iteration SHA-256 hashing matching the Rust logic
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

	private static String toHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) {
			String hex = Integer.toHexString(b & 0xFF);
			if (hex.length() == 1) sb.append('0');
			sb.append(hex);
		}
		return sb.toString();
	}

	private static MessageDigest getSHA256Digest() {
		try {
			return MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
	}

	public static String generateRandomString(int length) {
		return generateRandomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	public static String generateRandomString(int length, String characterSet) {
		StringBuilder randomString = new StringBuilder();
		SecureRandom rng = new SecureRandom();
		for (int i = 0; i < length; i++)
			randomString.append(characterSet.charAt(rng.nextInt(characterSet.length())));
		return randomString.toString();
	}

	public static String validateForXML(String input) {
		return input.replace("&", "&amp;")
				.replace("<", "&lt;")
				.replace(">", "&gt;")
				.replace("\"", "&quot;")
				.replace("'", "&apos;");
	}

	public static String returnOriginalValue(String input) {
		return input.replace("&amp;", "&")
				.replace("&lt;", "<")
				.replace("&gt;", ">")
				.replace("&quot;", "\"")
				.replace("&apos;", "'");
	}
}
