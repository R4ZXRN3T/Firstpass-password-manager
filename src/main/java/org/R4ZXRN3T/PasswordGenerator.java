package org.R4ZXRN3T;

import java.awt.*;
import java.awt.datatransfer.StringSelection;

import javax.swing.*;

public class PasswordGenerator {

	private JDialog frame;

	private JTextField passwordField;

	private boolean uppercase;
	private boolean lowercase;
	private boolean numbers;
	private boolean specialCharacters;
	private int length;

	public PasswordGenerator() {
		initFrame();
		uppercase = true;
		lowercase = true;
		numbers = true;
		specialCharacters = true;
		length = 16;

		JPanel optionsPanel = getOptionsPanel();
		JPanel outputPanel = getOutputPanel();
		JPanel generatePanel = getGeneratePanel();

		frame.add(optionsPanel, BorderLayout.NORTH);
		frame.add(outputPanel, BorderLayout.CENTER);
		frame.add(generatePanel, BorderLayout.SOUTH);
	}

	public void show() {
		if (frame.isVisible()) {
			frame.requestFocus();
			return;
		}
		frame.setVisible(true);
	}

	public void close() {
		frame.dispose();
	}

	private void initFrame() {
		frame = new JDialog(Main.frame, "Password Generator", true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLayout(new BorderLayout(10, 10));
		frame.setIconImage(Icons.FIRSTPASS_ICON.getImage());
		frame.setSize(450, 250);
		frame.setLocationRelativeTo(Main.frame);
		frame.setResizable(false);
	}

	private JPanel getOptionsPanel() {
		JPanel optionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		optionsPanel.setPreferredSize(new Dimension(500, 85));
		optionsPanel.setBorder(BorderFactory.createTitledBorder("Options:"));
		JCheckBox uppercaseBox = new JCheckBox("Uppercase");
		JCheckBox lowercaseBox = new JCheckBox("Lowercase");
		JCheckBox numbersBox = new JCheckBox("Numbers");
		JCheckBox specialCharactersBox = new JCheckBox("Special Characters");
		JSlider lengthSlider = new JSlider(1, 64, 16);
		lengthSlider.setBackground(Color.LIGHT_GRAY);
		JLabel lengthLabel = new JLabel("Length:");
		JLabel lengthValue = new JLabel("16");


		uppercaseBox.setSelected(true);
		lowercaseBox.setSelected(true);
		numbersBox.setSelected(true);
		specialCharactersBox.setSelected(true);

		uppercaseBox.addChangeListener(_ -> uppercase = uppercaseBox.isSelected());
		lowercaseBox.addChangeListener(_ -> lowercase = lowercaseBox.isSelected());
		numbersBox.addChangeListener(_ -> numbers = numbersBox.isSelected());
		specialCharactersBox.addChangeListener(_ -> specialCharacters = specialCharactersBox.isSelected());
		lengthSlider.addChangeListener(_ -> {
			lengthValue.setText(String.valueOf(lengthSlider.getValue()));
			length = lengthSlider.getValue();
		});

		optionsPanel.add(uppercaseBox);
		optionsPanel.add(lowercaseBox);
		optionsPanel.add(numbersBox);
		optionsPanel.add(specialCharactersBox);
		optionsPanel.add(lengthSlider);
		optionsPanel.add(lengthLabel);
		optionsPanel.add(lengthValue);

		return optionsPanel;
	}

	private JPanel getOutputPanel() {
		JPanel outputPanel = new JPanel(new BorderLayout(4, 4));
		outputPanel.setBorder(BorderFactory.createTitledBorder("Generated Password:"));

		passwordField = new JTextField();
		passwordField.setEditable(false);
		passwordField.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));

		JScrollPane scrollPane = new JScrollPane(passwordField);
		scrollPane.setAutoscrolls(true);
		scrollPane.setBackground(new Color(0, 0, 0, 0));
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);

		JButton copyButton = getCopyButton(passwordField);

		outputPanel.add(scrollPane, BorderLayout.CENTER);
		outputPanel.add(copyButton, BorderLayout.EAST);

		return outputPanel;
	}

	private JPanel getGeneratePanel() {
		CustomButton generateButton = new CustomButton(
				"Generate!",
				_ -> passwordField.setText(generatePassword(length, uppercase, lowercase, numbers, specialCharacters)),
				new Dimension(100, 35)
		);
		generateButton.setToolTipText("Generate a new password");
		generateButton.setBackground(UIManager.getColor("Button.background").brighter());

		JPanel generatePanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		generatePanel.add(generateButton);
		return generatePanel;
	}

	private JButton getCopyButton(JTextField passwordField) {
		JButton copyButton = new JButton();
		copyButton.setIcon(Config.getDarkMode() ? Icons.COPY_ICON_WHITE_SCALED : Icons.COPY_ICON_SCALED);
		copyButton.setToolTipText("Copy password to clipboard");
		copyButton.addActionListener(_ -> {
			Toolkit.getDefaultToolkit().getSystemClipboard().setContents(new StringSelection(passwordField.getText()), null);
			JOptionPane.showMessageDialog(frame, "Password copied to clipboard", "Success", JOptionPane.INFORMATION_MESSAGE);
		});
		copyButton.setPreferredSize(new Dimension(40, 40));
		return copyButton;
	}

	private static String generatePassword(int length, boolean uppercase, boolean lowercase, boolean numbers, boolean specialCharacters) {
		String characterSet = "";
		if (uppercase) characterSet += "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
		if (lowercase) characterSet += "abcdefghijklmnopqrstuvwxyz";
		if (numbers) characterSet += "0123456789";
		if (specialCharacters) characterSet += "!@#$%^&*()-_=+[{]};:'<A,<.>/?";
		return Tools.generateRandomString(length, characterSet);
	}
}