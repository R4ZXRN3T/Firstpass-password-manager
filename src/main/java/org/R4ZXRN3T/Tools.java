package org.R4ZXRN3T;

import org.R4ZXRN3T.Files.ConfigKey;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Objects;

class Tools {

	private static final String DEFAULT_LAF = "0";
	private static final int PASSWORD_LENGTH = 44;
	private static final int SALT_LENGTH = 16;
	private static final String DEFAULT_EXPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static final String DEFAULT_IMPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();

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
		return Base64.getEncoder().encodeToString(hashedPasswordAsByteArray);
	}

	public static String generateRandomString(int length) {
		return generateRandomString(length, "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+[{]};:'<A,<.>/?");
	}

	// generates a random String with a specified length. What else can I say?
	public static String generateRandomString(int length, String characterSet) {
		StringBuilder randomString = new StringBuilder();
		for (int i = 0; i < length; i++) {
			randomString.append(characterSet.charAt((int) (Math.random() * characterSet.length())));
		}
		return randomString.toString();
	}

	// restarts the program. Used if the settings menu needs to save and restart
	public static void restart() {
		try {
			String jarPath = new File(Main.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getAbsolutePath();
			if (!jarPath.endsWith(".jar")) jarPath = "./Firstpass.jar";
			System.out.println("Restarting with JAR path: " + jarPath);

			// Use an array to execute the command
			new ProcessBuilder("java", "-jar", jarPath).inheritIO().start();

			System.exit(0);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void setDefaultConfig() {
		try {
			String defaultSalt = generateRandomString(SALT_LENGTH);
			String defaultPassword = encodePassword("", defaultSalt);

			File configFile = new File("config.json");
			if (configFile.exists()) {
				configFile.delete();
			}
			JSONObject configJSON = getDefaultConfigJSON(defaultPassword, defaultSalt);

			FileWriter writer = new FileWriter(configFile);
			writer.write(configJSON.toString(4));
			writer.close();
			System.out.println("Default config set");
			restart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static JSONObject getDefaultConfigJSON(String defaultPassword, String defaultSalt) {
		JSONObject configString = new JSONObject();
		configString.put(ConfigKey.PASSWORD.toString(), defaultPassword);
		configString.put(ConfigKey.SALT.toString(), defaultSalt);
		configString.put(ConfigKey.LOOK_AND_FEEL.toString(), DEFAULT_LAF);
		configString.put(ConfigKey.LAST_EXPORT_LOCATION.toString(), DEFAULT_EXPORT_LOCATION);
		configString.put(ConfigKey.LAST_IMPORT_LOCATION.toString(), DEFAULT_IMPORT_LOCATION);
		configString.put(ConfigKey.CHECK_FOR_UPDATES.toString(), "true");
		return configString;
	}

	public static String getDefaultConfigValue(ConfigKey key) {
		switch (key) {
			case PASSWORD:
				return encodePassword("", generateRandomString(SALT_LENGTH));
			case SALT:
				return generateRandomString(SALT_LENGTH);
			case LOOK_AND_FEEL:
				return DEFAULT_LAF;
			case LAST_EXPORT_LOCATION:
				return DEFAULT_EXPORT_LOCATION;
			case LAST_IMPORT_LOCATION:
				return DEFAULT_IMPORT_LOCATION;
			case CHECK_FOR_UPDATES:
				return "true";
			default:
				return null;
		}
	}

	public static void setDefault(ConfigKey key) {
		try {
			File configFile = new File("config.json");
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig();
			}
			String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("config.json")));
			JSONObject jsonObject = new JSONObject(content);
			switch (key) {
				case ALL:
					setDefaultConfig();
					return;
				case PASSWORD:
					String tempSalt = generateRandomString(SALT_LENGTH);
					jsonObject.put(ConfigKey.PASSWORD.toString(), encodePassword("", tempSalt));
					jsonObject.put(ConfigKey.SALT.toString(), tempSalt);
					break;
				case SALT:
					key = ConfigKey.PASSWORD;
				case LOOK_AND_FEEL:
					jsonObject.put(key.toString(), DEFAULT_LAF);
					break;
				case LAST_EXPORT_LOCATION:
					jsonObject.put(key.toString(), DEFAULT_EXPORT_LOCATION);
					break;
				case LAST_IMPORT_LOCATION:
					jsonObject.put(key.toString(), DEFAULT_IMPORT_LOCATION);
					break;
				case CHECK_FOR_UPDATES:
					jsonObject.put(key.toString(), "true");
					break;
				default:
					break;
			}
			FileWriter writer = new FileWriter(configFile);
			writer.write(jsonObject.toString(4));
			writer.close();
			System.out.println("Default value set for " + key);
			restart();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			setDefaultConfig();
			e.printStackTrace();
		}
	}

	public static void checkConfig() {
		String tempPassword = Files.getConfig(ConfigKey.PASSWORD);
		if (tempPassword == null || tempPassword.length() != PASSWORD_LENGTH) setDefault(ConfigKey.PASSWORD);

		String tempSalt = Files.getConfig(ConfigKey.SALT);
		if (tempSalt == null || tempSalt.length() != SALT_LENGTH) setDefault(ConfigKey.SALT);

		try {
			int tempLaF = Integer.parseInt(Objects.requireNonNull(Files.getConfig(ConfigKey.LOOK_AND_FEEL)));
			if (tempLaF < 0 || tempLaF > 7)
				setDefault(ConfigKey.LOOK_AND_FEEL);
		} catch (NumberFormatException e) {
			setDefault(ConfigKey.LOOK_AND_FEEL);
		}

		String tempExportLocation = Files.getConfig(ConfigKey.LAST_EXPORT_LOCATION);
		if (tempExportLocation == null || tempExportLocation.isEmpty()) setDefault(ConfigKey.LAST_EXPORT_LOCATION);

		String tempImportLocation = Files.getConfig(ConfigKey.LAST_IMPORT_LOCATION);
		if (tempImportLocation == null || tempImportLocation.isEmpty()) setDefault(ConfigKey.LAST_IMPORT_LOCATION);

		String tempCheckForUpdates = Files.getConfig(ConfigKey.CHECK_FOR_UPDATES);
		if (tempCheckForUpdates == null) setDefault(ConfigKey.CHECK_FOR_UPDATES);
		else if (!tempCheckForUpdates.equals("true") && !tempCheckForUpdates.equals("false"))
			setDefault(ConfigKey.CHECK_FOR_UPDATES);
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