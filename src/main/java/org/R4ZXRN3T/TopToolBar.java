package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

class TopToolBar {

	private JButton updateButton = new JButton("New version available!");
	private final Main main;

	public TopToolBar(Main main) {
		this.main = main;
	}

	public JMenuBar getTopToolBar() {
		JMenuBar toolBar = new JMenuBar();

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(e -> {
			main.save();
			main.setChangeMade(false);
		});
		saveItem.setIcon(main.isDarkMode() ? Icons.SAVE_ICON_WHITE_SCALED : Icons.SAVE_ICON_SCALED);

		JMenuItem exportAsItem = new JMenuItem("Export");
		exportAsItem.addActionListener(e -> Files.exportData(main));
		exportAsItem.setIcon(main.isDarkMode() ? Icons.EXPORT_ICON_WHITE_SCALED : Icons.EXPORT_ICON_SCALED);

		JMenuItem importAsItem = new JMenuItem("Import");
		importAsItem.setIcon(main.isDarkMode() ? Icons.IMPORT_ICON_WHITE_SCALED : Icons.IMPORT_ICON_SCALED);
		importAsItem.addActionListener(e -> Files.importData(main));

		JButton settingsButton = new JButton("Settings");
		settingsButton.addActionListener(e -> new SettingsMenu(main).showSettings());
		settingsButton.setBackground(main.getFrame().getBackground());
		settingsButton.setBorderPainted(false);
		settingsButton.setFocusable(false);

		JMenuItem exitItem = new JMenuItem("Save & Exit");
		exitItem.addActionListener(e -> main.exit());
		exitItem.setIcon(main.isDarkMode() ? Icons.EXIT_ICON_WHITE_SCALED : Icons.EXIT_ICON_SCALED);

		JMenu fileItem = new JMenu("File");
		fileItem.add(saveItem);
		fileItem.add(exportAsItem);
		fileItem.add(importAsItem);
		fileItem.add(exitItem);

		updateButton.setForeground(new Color(0, 180, 255));
		updateButton.setBorderPainted(true);
		updateButton.addActionListener(e -> Updater.update());
		updateButton.setVisible(main.isUpdateAvailable());

		toolBar.add(fileItem);
		toolBar.add(settingsButton);
		toolBar.add(updateButton);

		return toolBar;
	}

	public JButton getUpdateButton() {
		return updateButton;
	}
}