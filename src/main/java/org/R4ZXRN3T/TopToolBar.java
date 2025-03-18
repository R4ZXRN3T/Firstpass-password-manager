package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

class TopToolBar {

	public static JMenuBar getTopToolBar() {
		JMenuBar toolBar = new JMenuBar();

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(e -> {Main.save(); Main.changeMade = false;});
		saveItem.setIcon(Main.darkMode ? Icons.SAVE_ICON_WHITE_SCALED : Icons.SAVE_ICON_SCALED);

		JMenuItem exportAsItem = new JMenuItem("Export as...");
		exportAsItem.addActionListener(e -> Files.exportData());
		exportAsItem.setIcon(Main.darkMode ? Icons.EXPORT_ICON_WHITE_SCALED : Icons.EXPORT_ICON_SCALED);

		JMenuItem importAsItem = new JMenuItem("Import...");
		importAsItem.setIcon(Main.darkMode ? Icons.IMPORT_ICON_WHITE_SCALED : Icons.IMPORT_ICON_SCALED);
		importAsItem.addActionListener(e -> Files.importData());

		JMenuItem settingsItem = new JMenuItem("Settings");
		settingsItem.addActionListener(e -> SettingsMenu.showSettings());
		settingsItem.setIcon(Main.darkMode ? Icons.SETTINGS_ICON_WHITE_SCALED : Icons.SETTINGS_ICON_SCALED);

		JMenuItem exitItem = new JMenuItem("Exit");
		exitItem.addActionListener(e -> Main.exit());
		exitItem.setIcon(Main.darkMode ? Icons.EXIT_ICON_WHITE_SCALED : Icons.EXIT_ICON_SCALED);

		JMenu fileItem = new JMenu("File");
		fileItem.add(saveItem);
		fileItem.add(exportAsItem);
		fileItem.add(importAsItem);
		fileItem.add(settingsItem);
		fileItem.add(exitItem);

		JButton updateButton = new JButton("New version available!");
		updateButton.setForeground(new Color(0, 180, 255));
		updateButton.addActionListener(e -> Updater.update());

		toolBar.add(fileItem);
		if (Main.updateAvailable) toolBar.add(updateButton);

		return toolBar;
	}
}