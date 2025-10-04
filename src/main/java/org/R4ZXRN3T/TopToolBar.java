package org.R4ZXRN3T;

import java.awt.*;
import javax.swing.*;

class TopToolBar extends JMenuBar {

	public JButton updateButton = new JButton("New version available!");

	public TopToolBar(Firstpass firstpass, ExportImportManager exportImportManager) {
		super();
		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(_ -> {
			firstpass.save();
			Main.changeMade = false;
		});
		saveItem.setIcon(Config.getDarkMode() ? Icons.SAVE_ICON_WHITE_SCALED : Icons.SAVE_ICON_SCALED);

		JMenuItem exportAsItem = new JMenuItem("Export");
		exportAsItem.addActionListener(_ -> exportImportManager.exportData(firstpass.accountService.getAccounts()));
		exportAsItem.setIcon(Config.getDarkMode() ? Icons.EXPORT_ICON_WHITE_SCALED : Icons.EXPORT_ICON_SCALED);

		JMenuItem importAsItem = new JMenuItem("Import");
		importAsItem.setIcon(Config.getDarkMode() ? Icons.IMPORT_ICON_WHITE_SCALED : Icons.IMPORT_ICON_SCALED);
		importAsItem.addActionListener(_ -> {
			var updated = exportImportManager.importData(firstpass.accountService.getAccounts());
			if (updated != null) {
				firstpass.accountService.setAccounts(updated);
				firstpass.refreshTable();
				firstpass.setChangeMade(true);
			}
		});

		JButton settingsButton = new JButton("Settings");
		settingsButton.addActionListener(_ -> {
			SettingsMenu settingsMenu = new SettingsMenu(this);
			settingsMenu.showSettings();
			settingsMenu.close();
		});
		settingsButton.setBackground(firstpass.frame.getBackground());
		settingsButton.setBorderPainted(false);
		settingsButton.setFocusable(false);

		JMenuItem exitItem = new JMenuItem("Save & Exit");
		exitItem.addActionListener(_ -> firstpass.exit());
		exitItem.setIcon(Config.getDarkMode() ? Icons.EXIT_ICON_WHITE_SCALED : Icons.EXIT_ICON_SCALED);

		JMenu fileItem = new JMenu("File");
		fileItem.add(saveItem);
		fileItem.add(exportAsItem);
		fileItem.add(importAsItem);
		fileItem.add(exitItem);

		updateButton.setForeground(new Color(0, 180, 255));
		updateButton.setBorderPainted(true);
		updateButton.addActionListener(_ -> Updater.update());
		updateButton.setVisible(Main.updateAvailable);

		this.add(fileItem);
		this.add(settingsButton);
		this.add(updateButton);
	}
}