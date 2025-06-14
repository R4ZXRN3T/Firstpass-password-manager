package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;
import java.util.*;

class SettingsMenu {

	private static final HashMap<Integer, String> currentSettings = new HashMap<>();
	private static JDialog settingsFrame;
	private static boolean needsRestart = false;
	private static CustomButton changePasswordButton;
	private static CustomButton removePasswordButton;

	public static void showSettings() {

		setCurrentSettings();

		// set up frame
		settingsFrame = new JDialog(Main.frame, "Firstpass Settings", true);
		settingsFrame.setLayout(new BorderLayout(16, 16));
		settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// add elements to the main frame
		settingsFrame.add(getMainPanel(), BorderLayout.CENTER);
		settingsFrame.add(getApplyCancelToolbar(), BorderLayout.SOUTH);

		// set up the frame
		settingsFrame.setSize(500, 400);
		settingsFrame.setResizable(false);
		settingsFrame.setLocationRelativeTo(Main.frame);
		settingsFrame.setIconImage(Icons.FIRSTPASS_ICON.getImage());
		settingsFrame.requestFocus();
		settingsFrame.setVisible(true);
	}

	// write the current settings to the HashMap
	private static void setCurrentSettings() {
		currentSettings.put(0, Main.correctPassword);
		currentSettings.put(1, Files.getConfig(Files.LOOK_AND_FEEL));
		currentSettings.put(2, Files.getConfig(Files.CHECK_FOR_UPDATES));
	}

	// get the panel for theme settings
	private static JPanel getThemePanel() {
		JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		themePanel.setBorder(BorderFactory.createTitledBorder("Theme"));
		JLabel themeLabel = new JLabel("Theme:");
		String[] themeOptions = {"Flat Light", "Flat Dark", "Flat Mac Light", "Flat Mac Dark", "Flat IntelliJ", "Flat Darcula", "Swing Metal", "System Default"};
		JComboBox<String> themeSelector = new JComboBox<>(themeOptions);
		themeSelector.setSelectedIndex(Integer.parseInt(currentSettings.get(1)));
		themeSelector.addActionListener(e -> {
			currentSettings.replace(1, String.valueOf(themeSelector.getSelectedIndex()));
			needsRestart = true;
		});
		themePanel.add(themeLabel);
		themePanel.add(themeSelector);
		return themePanel;
	}

	// get panel with password settings
	private static JPanel getPasswordPanel() {
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordPanel.setBorder(BorderFactory.createTitledBorder("Password"));
		changePasswordButton = new CustomButton("Change Password", e -> {
			changePassword();
			refreshButton();
		}, new Dimension(140, 30));
		passwordPanel.add(changePasswordButton);
		removePasswordButton = new CustomButton("Remove Password", e -> removePasswordDialog(), new Dimension(140, 30));
		passwordPanel.add(removePasswordButton);
		refreshButton();
		return passwordPanel;
	}

	private static JPanel getFullDeletePane() {
		JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		deletePanel.setBorder(BorderFactory.createTitledBorder("Delete"));
		deletePanel.add(new CustomButton("Delete Everything", null, e -> SettingsMenu.fullDeleteDialog(), false, true, new Dimension(140, 30), null, Color.RED, null));
		JLabel deleteLabel = new JLabel("Delete all data");
		deletePanel.add(deleteLabel);
		return deletePanel;
	}

	private static JPanel getUpdatePanel() {
		JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		updatePanel.setBorder(BorderFactory.createTitledBorder("Update"));

		updatePanel.add(new CustomButton("Check for Updates", e -> {
			Main.updateAvailable = Updater.checkVersion(true).compareToIgnoreCase(Main.CURRENT_VERSION) > 0;
			TopToolBar.updateButton.setVisible(Main.updateAvailable);
			if (Main.updateAvailable) {
				Updater.update();
			} else {
				JOptionPane.showMessageDialog(settingsFrame, "You are already using the latest version.", "No Updates Available", JOptionPane.INFORMATION_MESSAGE);
			}
		}, new Dimension(140, 30)));

		JCheckBox updateCheckBox = new JCheckBox("Check for updates on startup");
		updateCheckBox.setSelected(Boolean.parseBoolean(currentSettings.get(2)));
		updateCheckBox.addActionListener(e -> {
			currentSettings.replace(2, String.valueOf(updateCheckBox.isSelected()));
		});
		updatePanel.add(updateCheckBox);

		return updatePanel;
	}

	// get the toolbar with apply and cancel buttons
	private static JToolBar getApplyCancelToolbar() {
		JToolBar bottomToolbar = new JToolBar();
		bottomToolbar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomToolbar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		bottomToolbar.add(new CustomButton("Apply", Main.darkMode ? Icons.APPLY_ICON_WHITE_SCALED : Icons.APPLY_ICON_SCALED, e -> SettingsMenu.applySettings(), false, false, new Dimension(90, 30), Main.darkMode ? new Color(50, 50, 50) : Color.lightGray, null, null));
		bottomToolbar.add(new CustomButton("Cancel", Main.darkMode ? Icons.CANCEL_ICON_WHITE_SCALED : Icons.CANCEL_ICON_SCALED, e -> {
			Main.frame.setEnabled(true);
			settingsFrame.dispose();
		}, false, false, new Dimension(90, 30), Main.darkMode ? new Color(50, 50, 50) : Color.lightGray, null, null));
		return bottomToolbar;
	}

	// get the main panel with all settings
	private static JPanel getMainPanel() {
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		mainPanel.add(getThemePanel());
		mainPanel.add(getPasswordPanel());
		mainPanel.add(getUpdatePanel());
		mainPanel.add(getFullDeletePane());
		mainPanel.add(new Box.Filler(new Dimension(0, 0), new Dimension(0, 0), new Dimension(0, Integer.MAX_VALUE)));
		return mainPanel;
	}

	// change the password
	private static void changePassword() {
		JTextField oldPassword = new JTextField();
		JTextField newPassword = new JTextField();

		Object[] message = Main.passwordSet ? new Object[]{"Old Password:", oldPassword, "New Password:", newPassword} : new Object[]{"New Password:", newPassword};

		int option = JOptionPane.showConfirmDialog(settingsFrame, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
		if (option == JOptionPane.OK_OPTION) {
			if (!Main.passwordSet) {
				currentSettings.replace(0, newPassword.getText());
				Main.passwordSet = true;
				JOptionPane.showMessageDialog(settingsFrame, "Password successfully set", "Success", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			if (oldPassword.getText().equals(Main.correctPassword)) {
				currentSettings.replace(0, newPassword.getText());
				JOptionPane.showMessageDialog(settingsFrame, "Password successfully changed", "Success", JOptionPane.INFORMATION_MESSAGE);
			} else {
				JOptionPane.showMessageDialog(settingsFrame, "The old password is incorrect", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
	}

	// apply the settings and restart the program if necessary
	private static void applySettings() {
		Main.correctPassword = currentSettings.get(0);
		Files.setConfig(Files.LOOK_AND_FEEL, currentSettings.get(1));
		Files.setConfig(Files.CHECK_FOR_UPDATES, currentSettings.get(2));

		if (needsRestart) {
			String message = "The program needs to be restarted in order to apply the new settings. Do you want to restart now?";
			int option = JOptionPane.showConfirmDialog(null, message, "Restart to apply settings", JOptionPane.YES_NO_OPTION);
			if (option == JOptionPane.YES_OPTION) {
				Main.save();
				Tools.restart();
			}
		}

		settingsFrame.dispose();
		Main.changeMade = true;
	}

	private static void fullDeleteDialog() {
		JLabel warningLabel = new JLabel("Are you sure you want to delete all data?");
		JLabel warningLabel2 = new JLabel("Warning: This action will delete all saved accounts and then close the program. This action cannot be undone.");
		warningLabel2.setForeground(Color.RED);
		int option = JOptionPane.showConfirmDialog(null, new Object[]{warningLabel, warningLabel2}, "Delete all data", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) {
			Main.fullDelete();
		}
	}

	private static void removePasswordDialog() {
		int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to remove the password?", "Remove Password", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.NO_OPTION) {
			return;
		}
		currentSettings.replace(0, "");
		Main.passwordSet = false;
		JOptionPane.showMessageDialog(null, "Password removed", "Success", JOptionPane.INFORMATION_MESSAGE);
		refreshButton();
	}

	private static void refreshButton() {
		changePasswordButton.setText(Main.passwordSet ? "Change Password" : "Set Password");
		removePasswordButton.setEnabled(Main.passwordSet);
	}
}