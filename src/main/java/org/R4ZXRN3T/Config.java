package org.R4ZXRN3T;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Objects;

import javax.swing.*;

public class Config {

	private static final String DEFAULT_LAF = "0";
	private static final int PASSWORD_LENGTH = 64;
	private static final int SALT_LENGTH = 16;
	private static final String DEFAULT_EXPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();
	private static final String DEFAULT_IMPORT_LOCATION = Paths.get(System.getProperty("user.home")).toString();

	private static HashMap<String, String> configList;

	public static int init() {
		configList = new HashMap<>();
		if (!readConfig("config.json")) return 1;
		if (checkConfig()) return 2;
		// Set LaF and store dark mode separately
		boolean dark = ThemeManager.setLookAndFeel(getConfig(ConfigKey.LOOK_AND_FEEL));
		setConfig(ConfigKey.DARK_MODE, Boolean.toString(dark));
		return 0;
	}

	public static int saveConfig() {
		return writeConfig("config.json") ? 0 : 1;
	}

	private static void setDefaultConfig() {
		String defaultSalt = Tools.generateRandomString(SALT_LENGTH);
		String defaultPassword = Tools.encodePassword("", defaultSalt);
		JSONObject configJSON = getDefaultConfigJSON(defaultPassword, defaultSalt);

		File configFile = new File("config.json");
		File tempFile = new File("config.json.tmp");
		try (FileWriter writer = new FileWriter(tempFile)) {
			writer.write(configJSON.toString(4));
			writer.flush();
			if (!tempFile.renameTo(configFile)) {
				throw new IOException("Failed to rename temp config file.");
			}
			System.out.println("Default config set");
		} catch (IOException e) {
			System.err.println("Error setting default config: " + e.getMessage());
			JOptionPane.showMessageDialog(Main.frame, "Error writing default config.json", "Error", JOptionPane.ERROR_MESSAGE);
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
		configString.put(ConfigKey.DARK_MODE.toString(), "false");
		return configString;
	}

	private static void setDefault(ConfigKey key) {
		try {
			File configFile = new File("config.json");
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig();
			}
			String content = new String(java.nio.file.Files.readAllBytes(Paths.get("config.json")));
			JSONObject jsonObject = new JSONObject(content);
			switch (key) {
				case ALL -> {
					setDefaultConfig();
					return;
				}
				case PASSWORD -> {
					String tempSalt = Tools.generateRandomString(SALT_LENGTH);
					jsonObject.put(ConfigKey.PASSWORD.toString(), Tools.encodePassword("", tempSalt));
					jsonObject.put(ConfigKey.SALT.toString(), tempSalt);
				}
				case SALT -> {
					key = ConfigKey.PASSWORD;
				}
				case LOOK_AND_FEEL -> jsonObject.put(key.toString(), DEFAULT_LAF);
				case LAST_EXPORT_LOCATION -> jsonObject.put(key.toString(), DEFAULT_EXPORT_LOCATION);
				case LAST_IMPORT_LOCATION -> jsonObject.put(key.toString(), DEFAULT_IMPORT_LOCATION);
				case CHECK_FOR_UPDATES -> jsonObject.put(key.toString(), "true");
				case DARK_MODE -> jsonObject.put(key.toString(), "false");
				default -> {}
			}
			try (FileWriter writer = new FileWriter(configFile)) {
				writer.write(jsonObject.toString(4));
			}
			System.out.println("Default value set for " + key);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (JSONException e) {
			setDefaultConfig();
			e.printStackTrace();
		}
	}

	private static boolean checkConfig() {
		boolean configChanged = false;
		String tempPassword = getConfig(ConfigKey.PASSWORD);
		if (tempPassword == null || tempPassword.length() != PASSWORD_LENGTH) {
			setDefault(ConfigKey.PASSWORD);
			configChanged = true;
		}

		String tempSalt = getConfig(ConfigKey.SALT);
		if (tempSalt == null || tempSalt.length() != SALT_LENGTH) {
			setDefault(ConfigKey.SALT);
			configChanged = true;
		}

		try {
			int tempLaF = Integer.parseInt(Objects.requireNonNull(getConfig(ConfigKey.LOOK_AND_FEEL)));
			if (tempLaF < 0 || tempLaF > 7) {
				setDefault(ConfigKey.LOOK_AND_FEEL);
				configChanged = true;
			}
		} catch (Exception e) {
			setDefault(ConfigKey.LOOK_AND_FEEL);
			configChanged = true;
		}

		if (isEmpty(ConfigKey.LAST_EXPORT_LOCATION)) {
			setDefault(ConfigKey.LAST_EXPORT_LOCATION);
			configChanged = true;
		}
		if (isEmpty(ConfigKey.LAST_IMPORT_LOCATION)) {
			setDefault(ConfigKey.LAST_IMPORT_LOCATION);
			configChanged = true;
		}

		String tempCheckForUpdates = getConfig(ConfigKey.CHECK_FOR_UPDATES);
		if (tempCheckForUpdates == null || !(tempCheckForUpdates.equals("true") || tempCheckForUpdates.equals("false"))) {
			setDefault(ConfigKey.CHECK_FOR_UPDATES);
			configChanged = true;
		}

		if (getConfig(ConfigKey.DARK_MODE) == null) {
			setDefault(ConfigKey.DARK_MODE);
			configChanged = true;
		}

		return configChanged;
	}

	private static boolean isEmpty(ConfigKey key) {
		String v = getConfig(key);
		return v == null || v.isEmpty();
	}

	private static boolean readConfig(String path) {
		try {
			File configFile = new File(path);
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig();
				return false;
			}
			String fileContent = new String(java.nio.file.Files.readAllBytes(Paths.get(path)));
			JSONObject jsonObject = new JSONObject(fileContent);
			for (String currentKey : jsonObject.keySet()) {
				String content = jsonObject.getString(currentKey);
				configList.put(currentKey, content);
			}
			return true;
		} catch (JSONException e) {
			return false;
		} catch (IOException e) {
			System.err.println("Failed to read config file: " + e.getMessage());
			JOptionPane.showMessageDialog(Main.frame, "Error reading config.json", "Error", JOptionPane.ERROR_MESSAGE);
			return false;
		}
	}

	private static boolean writeConfig(String path) {
		File configFile = new File(path);
		File tempFile = new File(path + ".tmp");
		try (FileWriter writer = new FileWriter(tempFile)) {
			JSONObject jsonToSave = new JSONObject(configList);
			writer.write(jsonToSave.toString(4));
			writer.flush();
			if (!tempFile.renameTo(configFile)) {
				System.err.println("Failed to rename temp config file.");
				return false;
			}
			return true;
		} catch (IOException e) {
			System.err.println("Error writing config: " + e.getMessage());
			return false;
		}
	}

	public static String getConfig(ConfigKey key) {
		return configList.get(key.value);
	}

	public static boolean getDarkMode() {
		return Boolean.parseBoolean(getConfig(ConfigKey.DARK_MODE));
	}

	public static void setConfig(ConfigKey key, String value) {
		configList.put(key.value, value);
	}

	public enum ConfigKey {
		ALL("all"),
		PASSWORD("password"),
		SALT("salt"),
		LOOK_AND_FEEL("lookAndFeel"),
		LAST_EXPORT_LOCATION("export"),
		LAST_IMPORT_LOCATION("import"),
		CHECK_FOR_UPDATES("checkForUpdates"),
		DARK_MODE("darkMode");

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