package org.R4ZXRN3T;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class Tools {

	// encodes a String with SHA-256. Only used for password check and saving
	public static String encodePassword(String initialPassword, String salt) {
		initialPassword += salt;
		MessageDigest digest;

		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		byte[] hashedPasswordAsByteArray = digest.digest(initialPassword.getBytes(StandardCharsets.UTF_8));

		// Convert bytes to hexadecimal string
		StringBuilder hexString = new StringBuilder();
		for (byte b : hashedPasswordAsByteArray) {
			String hex = Integer.toHexString(0xff & b);
			if (hex.length() == 1) {
				hexString.append('0');
			}
			hexString.append(hex);
		}
		return hexString.toString();
	}

	public static String generateRandomString(int length) {
		return generateRandomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789");
	}

	// generates a random String with a specified length. What else can I say?
	public static String generateRandomString(int length, String characterSet) {
		StringBuilder randomString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			randomString.append(characterSet.charAt((int) (Math.random() * characterSet.length())));
		}
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