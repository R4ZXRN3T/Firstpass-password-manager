package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

public class TopToolBar {

	private final JButton updateButton = new JButton("New version available!");
	private final Firstpass firstpass;

	public TopToolBar(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	public JMenuBar getTopToolBar() {
		JMenuBar toolBar = new JMenuBar();

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(_ -> {
			firstpass.save();
			firstpass.setChangeMade(false);
		});
		saveItem.setIcon(Config.getDarkMode() ? Icons.SAVE_ICON_WHITE_SCALED : Icons.SAVE_ICON_SCALED);

		JMenuItem exportAsItem = new JMenuItem("Export");
		exportAsItem.addActionListener(_ -> Files.exportData(firstpass));
		exportAsItem.setIcon(Config.getDarkMode() ? Icons.EXPORT_ICON_WHITE_SCALED : Icons.EXPORT_ICON_SCALED);

		JMenuItem importAsItem = new JMenuItem("Import");
		importAsItem.setIcon(Config.getDarkMode() ? Icons.IMPORT_ICON_WHITE_SCALED : Icons.IMPORT_ICON_SCALED);
		importAsItem.addActionListener(_ -> Files.importData(firstpass));

		JButton settingsButton = new JButton("Settings");
		settingsButton.addActionListener(_ -> new SettingsMenu(firstpass).showSettings());
		settingsButton.setBackground(firstpass.getFrame().getBackground());
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
		updateButton.setVisible(firstpass.isUpdateAvailable());

		toolBar.add(fileItem);
		toolBar.add(settingsButton);
		toolBar.add(updateButton);

		return toolBar;
	}

	public JButton getUpdateButton() {
		return updateButton;
	}
}