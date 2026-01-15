package org.R4ZXRN3T.firstpass;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.StringSelection;

public class PasswordGenerator {

	private static JDialog frame = null;

	public static void showPasswordGeneratorDialog(JFrame parentFrame, boolean darkMode) {
		if (frame != null && frame.isVisible()) {
			frame.requestFocus();
			return;
		}
		frame = new JDialog(parentFrame, "Password Generator", true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));
		frame.setIconImage(Icons.FIRSTPASS_ICON.getImage());
		frame.setSize(450, 250);
		frame.setLocationRelativeTo(parentFrame);
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

		JButton copyButton = getCopyButton(passwordField, darkMode);

		outputPanel.add(scrollPane, BorderLayout.CENTER);
		outputPanel.add(copyButton, BorderLayout.EAST);

		CustomButton generateButton = new CustomButton("Generate!",
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

	private static JButton getCopyButton(JTextField passwordField, boolean darkMode) {
		JButton copyButton = new JButton();
		copyButton.setIcon(darkMode ? Icons.COPY_ICON_WHITE_SCALED : Icons.COPY_ICON_SCALED);
		copyButton.setToolTipText("Copy password to clipboard");
		copyButton.addActionListener(e -> {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordField.getText()), null);
			Tools.showToast(frame, "Password copied to clipboard", 1500, darkMode, -50);
		});
		copyButton.setPreferredSize(new Dimension(40, 40));
		return copyButton;
	}


	private static String generatePassword(int length, boolean uppercase, boolean lowercase, boolean numbers, boolean specialCharacters) {
		String characterSet = "";
		if (uppercase) characterSet += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if (lowercase) characterSet += "abcdefghijklmnopqrstuvwxyz";
		if (numbers) characterSet += "0123456789";
		if (specialCharacters) characterSet += "!#$%&'()*+,-./:;<=>?@[]^_`{|}~";
		return Tools.generateRandomString(length, characterSet);
	}
}