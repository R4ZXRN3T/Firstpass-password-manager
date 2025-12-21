package org.R4ZXRN3T;

import static org.R4ZXRN3T.Config.ConfigKey.*;

import org.R4ZXRN3T.Config.ConfigKey;

import java.awt.*;
import java.util.HashMap;

import javax.swing.*;

class SettingsMenu {

	private final HashMap<ConfigKey, String> currentSettings = new HashMap<>();
	private final Firstpass firstpass;
	private JDialog settingsFrame;
	private boolean needsRestart = false;
	private CustomButton changePasswordButton;
	private CustomButton removePasswordButton;

	private boolean passwordSet = true;

	public SettingsMenu(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	public void showSettings() {

		setCurrentSettings();
		setPasswordSet(!firstpass.getCorrectPassword().isEmpty());

		// set up frame
		settingsFrame = new JDialog(firstpass.getFrame(), "Firstpass Settings", true);
		settingsFrame.setLayout(new BorderLayout(16, 16));
		settingsFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// add elements to the main frame
		settingsFrame.add(getMainPanel(), BorderLayout.CENTER);
		settingsFrame.add(getApplyCancelToolbar(), BorderLayout.SOUTH);

		// set up the frame
		settingsFrame.setSize(500, 400);
		settingsFrame.setResizable(false);
		settingsFrame.setLocationRelativeTo(firstpass.getFrame());
		settingsFrame.setIconImage(Icons.FIRSTPASS_ICON.getImage());
		settingsFrame.requestFocus();
		settingsFrame.setVisible(true);
	}

	// write the current settings to the HashMap
	private void setCurrentSettings() {
		currentSettings.put(PASSWORD, firstpass.getCorrectPassword());
		currentSettings.put(LOOK_AND_FEEL, Config.getConfig(LOOK_AND_FEEL));
		currentSettings.put(CHECK_FOR_UPDATES, Config.getConfig(Config.ConfigKey.CHECK_FOR_UPDATES));
	}

	// get the panel for theme settings
	private JPanel getThemePanel() {
		JPanel themePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		themePanel.setBorder(BorderFactory.createTitledBorder("Theme"));
		JLabel themeLabel = new JLabel("Theme:");
		String[] themeOptions = {"Flat Light", "Flat Dark", "Flat Mac Light", "Flat Mac Dark", "Flat IntelliJ", "Flat Darcula", "Swing Metal", "System Default"};
		JComboBox<String> themeSelector = new JComboBox<>(themeOptions);
		themeSelector.setSelectedIndex(Integer.parseInt(currentSettings.get(LOOK_AND_FEEL)));
		themeSelector.addActionListener(_ -> {
			currentSettings.replace(LOOK_AND_FEEL, String.valueOf(themeSelector.getSelectedIndex()));
			needsRestart = true;
		});
		themePanel.add(themeLabel);
		themePanel.add(themeSelector);
		return themePanel;
	}

	// get panel with password settings
	private JPanel getPasswordPanel() {
		JPanel passwordPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		passwordPanel.setBorder(BorderFactory.createTitledBorder("Password"));

		changePasswordButton = new CustomButton("Change Password", _ -> changePassword(), new Dimension(140, 30));
		passwordPanel.add(changePasswordButton);

		removePasswordButton = new CustomButton("Remove Password", _ -> removePasswordDialog(), new Dimension(140, 30));
		passwordPanel.add(removePasswordButton);

		refreshButton();
		return passwordPanel;
	}

	private JPanel getFullDeletePane() {
		JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		deletePanel.setBorder(BorderFactory.createTitledBorder("Delete"));
		deletePanel.add(new CustomButton("Delete Everything", null, _ -> fullDeleteDialog(), false, true, new Dimension(140, 30), null, Color.RED, null));
		JLabel deleteLabel = new JLabel("Delete all data");
		deletePanel.add(deleteLabel);
		return deletePanel;
	}

	private JPanel getUpdatePanel() {
		JPanel updatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
		updatePanel.setBorder(BorderFactory.createTitledBorder("Update"));

		updatePanel.add(new CustomButton("Check for Updates", _ -> {
			firstpass.setUpdateAvailable(Updater.checkVersion(true).compareToIgnoreCase(Firstpass.CURRENT_VERSION) > 0);
			firstpass.getTopToolBar().getUpdateButton().setVisible(firstpass.isUpdateAvailable());
			if (firstpass.isUpdateAvailable()) Updater.update();
			else
				JOptionPane.showMessageDialog(settingsFrame, "You are already using the latest version.", "No Updates Available", JOptionPane.INFORMATION_MESSAGE);
		}, new Dimension(140, 30)));

		JCheckBox updateCheckBox = new JCheckBox("Check for updates on startup");
		updateCheckBox.setSelected(Boolean.parseBoolean(currentSettings.get(CHECK_FOR_UPDATES)));
		updateCheckBox.addActionListener(_ -> currentSettings.replace(CHECK_FOR_UPDATES, String.valueOf(updateCheckBox.isSelected())));
		updatePanel.add(updateCheckBox);

		return updatePanel;
	}

	// get the toolbar with apply and cancel buttons
	private JToolBar getApplyCancelToolbar() {
		JToolBar bottomToolbar = new JToolBar();
		bottomToolbar.setLayout(new FlowLayout(FlowLayout.RIGHT));
		bottomToolbar.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
		bottomToolbar.add(new CustomButton("Apply", Config.getDarkMode() ? Icons.APPLY_ICON_WHITE_SCALED : Icons.APPLY_ICON_SCALED, _ -> applySettings(), false, false, new Dimension(90, 30), Config.getDarkMode() ? new Color(50, 50, 50) : Color.lightGray, null, null));
		bottomToolbar.add(new CustomButton("Cancel", Config.getDarkMode() ? Icons.CANCEL_ICON_WHITE_SCALED : Icons.CANCEL_ICON_SCALED, _ -> {
			firstpass.getFrame().setEnabled(true);
			settingsFrame.dispose();
		}, false, false, new Dimension(90, 30), Config.getDarkMode() ? new Color(50, 50, 50) : Color.lightGray, null, null));
		return bottomToolbar;
	}

	// get the main panel with all settings
	private JPanel getMainPanel() {
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
	private void changePassword() {
		JPasswordField oldPassword = new JPasswordField();
		JPasswordField newPassword = new JPasswordField();

		Object[] message = isPasswordSet() ? new Object[]{"Old Password:", oldPassword, "New Password:", newPassword} : new Object[]{"New Password:", newPassword};

		boolean firstRun = true;

		do {
			if (!firstRun) {
				Tools.showToast(settingsFrame, "Old password is incorrect", 2000, Config.getDarkMode(), 70);
				oldPassword.setText("");
				newPassword.setText("");
			}
			oldPassword.addAncestorListener(new Firstpass.RequestFocusListener());
			int option = JOptionPane.showConfirmDialog(settingsFrame, message, "Change Password", JOptionPane.OK_CANCEL_OPTION);
			firstRun = false;
			if (option != JOptionPane.OK_OPTION) return;
		} while (isPasswordSet() && !String.valueOf(oldPassword.getPassword()).equals(firstpass.getCorrectPassword()));

		currentSettings.replace(PASSWORD, String.valueOf(newPassword.getPassword()));
		setPasswordSet(true);
		refreshButton();
		Tools.showToast(settingsFrame, "Password successfully changed.", 1500, true, 70);
	}

	// apply the settings and restart the program if necessary
	private void applySettings() {
		firstpass.setCorrectPassword(currentSettings.get(PASSWORD));
		Config.setConfig(LOOK_AND_FEEL, currentSettings.get(LOOK_AND_FEEL));
		Config.setConfig(CHECK_FOR_UPDATES, currentSettings.get(CHECK_FOR_UPDATES));

		boolean restart = needsRestart && JOptionPane.showConfirmDialog(settingsFrame, "The program needs to restart to apply the new settings. Restart now?", "Restart", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION;

		// Dispose before restarting to clear modality influence
		settingsFrame.dispose();

		if (restart) {
			firstpass.save();
			Main.restart(firstpass);
			return;
		}

		firstpass.setChangeMade(true);
	}

	private void fullDeleteDialog() {
		JLabel warningLabel = new JLabel("Are you sure you want to delete all data?");
		JLabel warningLabel2 = new JLabel("Warning: This action will delete all saved accounts and then close the program. This action cannot be undone.");
		warningLabel2.setForeground(Color.RED);
		int option = JOptionPane.showConfirmDialog(null, new Object[]{warningLabel, warningLabel2}, "Delete all data", JOptionPane.YES_NO_OPTION);
		if (option == JOptionPane.YES_OPTION) firstpass.fullDelete();
	}

	private void removePasswordDialog() {
		JLabel promptLabel = new JLabel("Enter current password to remove it:");
		JPasswordField passwordField = new JPasswordField();
		Object[] message = {promptLabel, passwordField};

		boolean firstRun = true;
		do {
			if (!firstRun) {
				promptLabel.setText("Wrong password. Please try again:");
				promptLabel.setForeground(Color.RED);
				passwordField.setText("");
			}
			passwordField.addAncestorListener(new Firstpass.RequestFocusListener());
			int option = JOptionPane.showConfirmDialog(null, message, "Remove Password", JOptionPane.OK_CANCEL_OPTION);
			if (option != JOptionPane.OK_OPTION) return;
			firstRun = false;
		} while (!String.valueOf(passwordField.getPassword()).equals(currentSettings.get(PASSWORD)));

		currentSettings.replace(PASSWORD, "");
		setPasswordSet(false);
		Tools.showToast(settingsFrame, "Password successfully removed", 2000, Config.getDarkMode(), 70);
		refreshButton();
	}

	private boolean isPasswordSet() {
		return passwordSet;
	}

	private void setPasswordSet(boolean passwordSet) {
		this.passwordSet = passwordSet;
	}

	private void refreshButton() {
		changePasswordButton.setText(isPasswordSet() ? "Change Password" : "Set Password");
		removePasswordButton.setEnabled(isPasswordSet());
	}
}