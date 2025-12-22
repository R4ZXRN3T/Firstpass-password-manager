package org.R4ZXRN3T.firstpass;

import static org.R4ZXRN3T.firstpass.Tools.encodePassword;
import static org.R4ZXRN3T.firstpass.Tools.generateRandomString;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import javax.swing.*;

public class Config {

	public static final int SALT_LENGTH = 16;
	private static final String DEFAULT_LAF = "0";
	private static final int PASSWORD_LENGTH = 64;
	private static final String DEFAULT_EXPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static final String DEFAULT_IMPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static HashMap<String, String> configList;
	private static Firstpass firstpass;
	private static boolean darkMode;
	private static Boolean portableVersion;
	public static final String CONFIG_PATH = String.valueOf(getConfigFilePath());
	public static final String LOG_PATH = CONFIG_PATH.replace("config.json", "logs/log.txt");
	private static final Logger logger = new Logger(LOG_PATH);

	public static void init(Firstpass firstpassInstance) {
		firstpass = firstpassInstance;
		portableVersion = Firstpass.class.getResource("/assets/firstpass_icon.png") != null;
		configList = new HashMap<>();
		readConfig();
		checkConfig();
		darkMode = new ThemeManager().setLookAndFeel(getConfig(ConfigKey.LOOK_AND_FEEL));
	}

	public static void saveConfig() {
		writeConfig();
	}

	private static void readConfig() {
		try {
			File configFile = new File(new File(Config.CONFIG_PATH).getAbsolutePath());
			IO.println("Reading config from " + configFile.getAbsolutePath());
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig(); // No restart here
				return;
			}
			String fileContent = new String(java.nio.file.Files.readAllBytes(Path.of(configFile.getAbsolutePath())));
			JSONObject jsonObject = new JSONObject(fileContent);
			for (String currentKey : jsonObject.keySet()) {
				String content = jsonObject.getString(currentKey);
				configList.put(currentKey, content);
				IO.println("Loaded config: " + currentKey + " = " + content);
			}
		} catch (JSONException e) {
			System.err.println("Config file is corrupted or invalid JSON: " + e.getMessage());
			setDefaultConfig(); // No restart
		} catch (IOException e) {
			logger.error("Failed to read config file: " + e.getMessage());
			System.err.println("Failed to read config file: " + e.getMessage());
			JOptionPane.showMessageDialog(firstpass.getFrame(), "Error reading config.json. Program will exit. Please try again later", "Error", JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
	}

	private static void writeConfig() {
		File configFile = new File(new File(Config.CONFIG_PATH).getAbsolutePath());
		try (FileWriter writer = new FileWriter(configFile)) {
			if (!configFile.getParentFile().exists() && !configFile.getParentFile().mkdirs()) {
				System.err.println("Failed to create config directory");
				return;
			}
			JSONObject jsonToSave = new JSONObject(configList);
			for (String key : configList.keySet()) IO.println("Saving config: " + key + " = " + configList.get(key));
			writer.write(jsonToSave.toString(4));
			writer.flush();
		} catch (IOException e) {
			logger.error("Error writing config: " + e.getMessage());
			System.err.println("Error writing config: " + e.getMessage());
		}
	}

	private static void setDefaultConfig() {
		try {
			String defaultSalt = generateRandomString(SALT_LENGTH);
			String defaultPassword = encodePassword("", defaultSalt);

			setConfig(ConfigKey.PASSWORD, defaultPassword);
			setConfig(ConfigKey.SALT, defaultSalt);
			setConfig(ConfigKey.LOOK_AND_FEEL, DEFAULT_LAF);
			setConfig(ConfigKey.LAST_EXPORT_LOCATION, DEFAULT_EXPORT_LOCATION);
			setConfig(ConfigKey.LAST_IMPORT_LOCATION, DEFAULT_IMPORT_LOCATION);
			setConfig(ConfigKey.CHECK_FOR_UPDATES, "true");

			saveConfig();
			System.out.println("Default config set");
			// Removed Main.restart(firstpass);
		} catch (Exception e) {
			logger.error("Error setting default config: " + e.getMessage());
			IO.println("Error setting default config: " + e.getMessage());
		}
	}

	private static void setDefault(ConfigKey key) {
		try {
			switch (key) {
				case ALL -> setDefaultConfig();
				case PASSWORD, SALT -> {
					String tempSalt = generateRandomString(SALT_LENGTH);
					setConfig(ConfigKey.PASSWORD, encodePassword("", tempSalt));
					setConfig(ConfigKey.SALT, tempSalt);
				}
				case LOOK_AND_FEEL -> setConfig(key, DEFAULT_LAF);
				case LAST_EXPORT_LOCATION -> setConfig(key, DEFAULT_EXPORT_LOCATION);
				case LAST_IMPORT_LOCATION -> setConfig(key, DEFAULT_IMPORT_LOCATION);
				case CHECK_FOR_UPDATES -> setConfig(key, "true");
			}
			saveConfig();
			System.out.println("Default value set for " + key);
		} catch (Exception e) {
			logger.error("Error setting default config for " + key + ": " + e.getMessage());
			IO.println("Error setting default config for " + key + ": " + e.getMessage());
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

	public static String getConfig(ConfigKey key) {
		return configList.getOrDefault(key.toString(), null);
	}

	public static boolean getDarkMode() {
		return darkMode;
	}

	public static boolean isPortableVersion() {
		if (portableVersion != null) return portableVersion;
		portableVersion = Firstpass.class.getResource("/assets/firstpass_icon.png") != null;
		return portableVersion;
	}

	public static Path getConfigFilePath() {
		String os = System.getProperty("os.name").toLowerCase();
		String fileName = "Firstpass/config.json";
		if (isPortableVersion()) {
			return Paths.get("config.json");
		} else if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			return Paths.get(appData, fileName);
		} else if (os.contains("mac")) {
			String userHome = System.getProperty("user.home");
			return Paths.get(userHome, "Library", "Application Support", fileName);
		} else { // Linux and others
			String userHome = System.getProperty("user.home");
			return Paths.get(userHome, ".config", fileName);
		}
	}

	public static void setConfig(ConfigKey key, String value) {
		configList.put(key.toString(), value);
	}

	// For testing purposes only
	public static void resetForTesting() {
		configList = new HashMap<>();
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