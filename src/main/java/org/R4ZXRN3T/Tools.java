package org.R4ZXRN3T;

import org.json.JSONException;
import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;
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
	private static JFrame frame = new JFrame("Password Generator");

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
			if (!jarPath.contains(".jar")) jarPath = "Firstpass.jar";
			jarPath = "\"" + jarPath + "\"";
			System.out.println("Restarting with JAR path: " + jarPath);
			Runtime.getRuntime().exec("java -jar " + jarPath);
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
			JSONObject configString = new JSONObject();
			configString.put(Files.PASSWORD, defaultPassword);
			configString.put(Files.SALT, defaultSalt);
			configString.put(Files.LOOK_AND_FEEL, DEFAULT_LAF);
			configString.put(Files.LAST_EXPORT_LOCATION, DEFAULT_EXPORT_LOCATION);
			configString.put(Files.LAST_IMPORT_LOCATION, DEFAULT_IMPORT_LOCATION);

			FileWriter writer = new FileWriter(configFile);
			writer.write(configString.toString(4));
			writer.close();
			System.out.println("Default config set");
			restart();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setDefaultLaf(String key) {
		try {
			File configFile = new File("config.json");
			if (!configFile.exists() || configFile.length() == 0) {
				setDefaultConfig();
			}
			String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get("config.json")));
			JSONObject jsonObject = new JSONObject(content);
			switch (key) {
				case Files.PASSWORD:
					String tempSalt = generateRandomString(SALT_LENGTH);
					jsonObject.put(Files.PASSWORD, encodePassword("", tempSalt));
					jsonObject.put(Files.SALT, tempSalt);
					break;
				case Files.SALT:
					key = Files.PASSWORD;
				case Files.LOOK_AND_FEEL:
					jsonObject.put(key, DEFAULT_LAF);
					break;
				case Files.LAST_EXPORT_LOCATION:
					jsonObject.put(key, DEFAULT_EXPORT_LOCATION);
					break;
				case Files.LAST_IMPORT_LOCATION:
					jsonObject.put(key, DEFAULT_IMPORT_LOCATION);
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
		String tempPassword = Files.getConfig(Files.PASSWORD);
		if (tempPassword == null || tempPassword.length() != PASSWORD_LENGTH)
			setDefaultLaf(Files.PASSWORD);
		String tempSalt = Files.getConfig(Files.SALT);
		if (tempSalt == null || tempSalt.length() != SALT_LENGTH)
			setDefaultLaf(Files.SALT);
		try {
			int tempLaF = Integer.parseInt(Objects.requireNonNull(Files.getConfig(Files.LOOK_AND_FEEL)));
			if (tempLaF < 0 || tempLaF > 7)
				setDefaultLaf(Files.LOOK_AND_FEEL);
		} catch (NumberFormatException e) {
			setDefaultLaf(Files.LOOK_AND_FEEL);
		}
		String tempExportLocation = Files.getConfig(Files.LAST_EXPORT_LOCATION);
		if (tempExportLocation == null || tempExportLocation.isEmpty())
			setDefaultLaf(Files.LAST_EXPORT_LOCATION);
		String tempImportLocation = Files.getConfig(Files.LAST_IMPORT_LOCATION);
		if (tempImportLocation == null || tempImportLocation.isEmpty())
			setDefaultLaf(Files.LAST_IMPORT_LOCATION);
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

	public static void passwordGeneratorDialog() {
		if (frame.isVisible()) {
			frame.requestFocus();
			return;
		}
		frame = new JFrame("Password Generator");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));
		frame.setIconImage(Icons.FIRSTPASS_ICON.getImage());
		frame.setSize(420, 250);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionsPanel.setPreferredSize(new Dimension(500, 85));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options:"));
		JCheckBox uppercase = new JCheckBox("Uppercase");
		JCheckBox lowercase = new JCheckBox("Lowercase");
		JCheckBox numbers = new JCheckBox("Numbers");
		JCheckBox specialCharacters = new JCheckBox("Special Characters");
		JSlider lengthSlider = new JSlider(1, 64, 16);
		lengthSlider.setBackground(Color.LIGHT_GRAY);
		JLabel lengthLabel = new JLabel("Length:");
		JLabel lengthValue = new JLabel("16");
		lengthSlider.addChangeListener(e -> lengthValue.setText(String.valueOf(lengthSlider.getValue())));

		uppercase.setSelected(true);
		lowercase.setSelected(true);
		numbers.setSelected(true);
		specialCharacters.setSelected(true);

		optionsPanel.add(uppercase);
		optionsPanel.add(lowercase);
		optionsPanel.add(numbers);
		optionsPanel.add(specialCharacters);
		optionsPanel.add(lengthSlider);
		optionsPanel.add(lengthLabel);
		optionsPanel.add(lengthValue);

		JPanel outputPanel = new JPanel(new BorderLayout(4, 4));
		outputPanel.setBorder(BorderFactory.createTitledBorder("Generated Password:"));

		JTextField passwordField = new JTextField();
		passwordField.setEditable(false);
		passwordField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));


		JScrollPane scrollPane = new JScrollPane(passwordField);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBackground(new Color(0, 0, 0, 0));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		CustomButton copyButton = new CustomButton("", Main.darkMode ? Icons.COPY_ICON_WHITE_SCALED : Icons.COPY_ICON_SCALED, e -> {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordField.getText()), null);
			JOptionPane.showMessageDialog(frame, "Password copied to clipboard", "Success", JOptionPane.INFORMATION_MESSAGE);
		}, new Dimension(35, 35));
		copyButton.setToolTipText("Copy password to clipboard");

		outputPanel.add(scrollPane, BorderLayout.CENTER);
		outputPanel.add(copyButton, BorderLayout.EAST);

		CustomButton generateButton = new CustomButton("Generate",
				e -> passwordField.setText(generatePassword(lengthSlider.getValue(), uppercase.isSelected(), lowercase.isSelected(), numbers.isSelected(), specialCharacters.isSelected())),
				new Dimension(100, 35));
		generateButton.setToolTipText("Generate a new password");
		generateButton.setBackground(UIManager.getColor("Button.background").brighter());

		JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		generatePanel.add(generateButton);

		frame.add(optionsPanel, BorderLayout.NORTH);
		frame.add(outputPanel, BorderLayout.CENTER);
		frame.add(generatePanel, BorderLayout.SOUTH);

		frame.setVisible(true);
	}

	private static String generatePassword(int length, boolean uppercase, boolean lowercase, boolean numbers, boolean specialCharacters) {
		String characterSet = "";
		if (uppercase) characterSet += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if (lowercase) characterSet += "abcdefghijklmnopqrstuvwxyz";
		if (numbers) characterSet += "0123456789";
		if (specialCharacters) characterSet += "!@#$%^&*()-_=+[{]};:'<A,<.>/?";
		return generateRandomString(length, characterSet);
	}
}