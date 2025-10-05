package org.R4ZXRN3T;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import static org.R4ZXRN3T.Tools.encodePassword;
import static org.R4ZXRN3T.Tools.generateRandomString;

public class Config {

	private static final String DEFAULT_LAF = "0";
	private static final int PASSWORD_LENGTH = 64;
	private static final int SALT_LENGTH = 16;
	private static final String DEFAULT_EXPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static final String DEFAULT_IMPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static HashMap<String, String> configList;
	private static Firstpass firstpass;

	private static boolean darkMode;
	private static boolean portableVersion;

	public static void init(Firstpass firstpassInstance) {
		firstpass = firstpassInstance;
		configList = new HashMap<>();
		readConfig("config.json");
		checkConfig();
		darkMode = new ThemeManager().setLookAndFeel(getConfig(ConfigKey.LOOK_AND_FEEL));
		portableVersion = Firstpass.class.getResource("/assets/firstpass_icon.png") != null;
	}

	public static void saveConfig() {
		writeConfig("config.json");
	}

	private static void readConfig(String path) {
		try {
			IO.println("Reading config from " + path);
			File configFile = new File(path);
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig();
				return;
			}
			String fileContent = new String(java.nio.file.Files.readAllBytes(Paths.get(path)));
			JSONObject jsonObject = new JSONObject(fileContent);
			for (String currentKey : jsonObject.keySet()) {
				String content = jsonObject.getString(currentKey);
				configList.put(currentKey, content);
				IO.println("Loaded config: " + currentKey + " = " + content);
			}
		} catch (JSONException e) {
			System.err.println("Config file is corrupted or invalid JSON: " + e.getMessage());
			setDefaultConfig();
		} catch (IOException e) {
			System.err.println("Failed to read config file: " + e.getMessage());
			JOptionPane.showMessageDialog(firstpass.getFrame(), "Error reading config.json. Program will exit. Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private static void writeConfig(String path) {
		File configFile = new File(path);
		try (FileWriter writer = new FileWriter(configFile)) {
			JSONObject jsonToSave = new JSONObject(configList);
			writer.write(jsonToSave.toString(4));
			writer.flush();
		} catch (IOException e) {
			System.err.println("Error writing config: " + e.getMessage());
		}
	}

	private static void setDefaultConfig() {
		try {
			String defaultSalt = generateRandomString(SALT_LENGTH);
			String defaultPassword = encodePassword("", defaultSalt);

			File configFile = new File("config.json");
			if (configFile.exists()) configFile.delete();
			JSONObject configJSON = getDefaultConfigJSON(defaultPassword, defaultSalt);

			FileWriter writer = new FileWriter(configFile);
			writer.write(configJSON.toString(4));
			writer.close();
			System.out.println("Default config set");
			Main.restart(firstpass);
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

	private static void setDefault(ConfigKey key) {
		try {
			File configFile = new File("config.json");
			if (!configFile.exists() || configFile.length() == 0) setDefaultConfig();
			String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("config.json")));
			JSONObject jsonObject = new JSONObject(content);

			boolean needsRestart = false;

			switch (key) {
				case ALL -> {
					setDefaultConfig();
					needsRestart = true;
				}
				case PASSWORD, SALT -> {
					String tempSalt = generateRandomString(SALT_LENGTH);
					jsonObject.put(ConfigKey.PASSWORD.toString(), encodePassword("", tempSalt));
					jsonObject.put(ConfigKey.SALT.toString(), tempSalt);
				}
				case LOOK_AND_FEEL -> {
					jsonObject.put(key.toString(), DEFAULT_LAF);
					needsRestart = true;
				}
				case LAST_EXPORT_LOCATION -> jsonObject.put(key.toString(), DEFAULT_EXPORT_LOCATION);
				case LAST_IMPORT_LOCATION -> jsonObject.put(key.toString(), DEFAULT_IMPORT_LOCATION);
				case CHECK_FOR_UPDATES -> jsonObject.put(key.toString(), "true");
			}

			try (FileWriter writer = new FileWriter(configFile)) {
				writer.write(jsonObject.toString(4));
			}

			System.out.println("Default value set for " + key);

			// refresh in-memory map (simplified)
			configList.put(key.toString(), jsonObject.optString(key.toString(), null));

			if (needsRestart) Main.restart(firstpass);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			setDefaultConfig();
			e.printStackTrace();
		}
	}

	private static void checkConfig() {
		String tempPassword = getConfig(ConfigKey.PASSWORD);
		if (tempPassword == null || tempPassword.length() != PASSWORD_LENGTH) setDefault(ConfigKey.PASSWORD);

		String tempSalt = getConfig(ConfigKey.SALT);
		if (tempSalt == null || tempSalt.length() != SALT_LENGTH) setDefault(ConfigKey.SALT);

		try {
			int tempLaF = Integer.parseInt(Objects.requireNonNull(getConfig(ConfigKey.LOOK_AND_FEEL)));
			if (tempLaF < 0 || tempLaF > 7) setDefault(ConfigKey.LOOK_AND_FEEL);
		} catch (NumberFormatException e) {
			setDefault(ConfigKey.LOOK_AND_FEEL);
		}

		String tempExportLocation = getConfig(ConfigKey.LAST_EXPORT_LOCATION);
		if (tempExportLocation == null || tempExportLocation.isEmpty()) setDefault(ConfigKey.LAST_EXPORT_LOCATION);

		String tempImportLocation = getConfig(ConfigKey.LAST_IMPORT_LOCATION);
		if (tempImportLocation == null || tempImportLocation.isEmpty()) setDefault(ConfigKey.LAST_IMPORT_LOCATION);

		String tempCheckForUpdates = getConfig(ConfigKey.CHECK_FOR_UPDATES);
		if (tempCheckForUpdates == null) setDefault(ConfigKey.CHECK_FOR_UPDATES);
		else if (!tempCheckForUpdates.equals("true") && !tempCheckForUpdates.equals("false"))
			setDefault(ConfigKey.CHECK_FOR_UPDATES);
	}

	// get values from the config.json. Initially separate methods, now combined
	public static String getConfig(ConfigKey key) {
		return configList.getOrDefault(key.toString(), null);
	}

	public static boolean getDarkMode() {
		return darkMode;
	}

	public static boolean isPortableVersion() {
		return portableVersion;
	}

	// same as with getConfig
	public static void setConfig(ConfigKey key, String value) {
		configList.put(key.toString(), value);
	}

	public enum ConfigKey {
		ALL("all"),
		PASSWORD("password"),
		SALT("salt"),
		LOOK_AND_FEEL("lookAndFeel"),
		LAST_EXPORT_LOCATION("export"),
		LAST_IMPORT_LOCATION("import"),
		CHECK_FOR_UPDATES("checkForUpdates");

		private final String value;

		ConfigKey(String value) {
			this.value = value;
		}

		@Override
		public String toString() {
			return value;
		}
	}
}