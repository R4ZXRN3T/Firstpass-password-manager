package org.R4ZXRN3T.firstpass.gui;

import org.R4ZXRN3T.firstpass.Config;
import org.R4ZXRN3T.firstpass.Firstpass;
import org.R4ZXRN3T.firstpass.ImportExportManager;
import org.R4ZXRN3T.firstpass.Updater;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;

public class TopToolBar {

	private final JButton updateButton = new JButton("New version available!");
	private final Firstpass firstpass;

	public TopToolBar(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	public JMenuBar getTopToolBar() {
		JMenuBar toolBar = new JMenuBar();
		UIManager.put("MenuBar.background", firstpass.getCenterPanel().getBackground());
		toolBar.setBackground(firstpass.getCenterPanel().getBackground());
		toolBar.setBorderPainted(false);

		JMenuItem saveItem = new JMenuItem("Save");
		saveItem.addActionListener(_ -> {
			firstpass.save();
			firstpass.setChangeMade(false);
		});
		saveItem.setIcon(Config.getDarkMode() ? Icons.SAVE_ICON_WHITE_SCALED : Icons.SAVE_ICON_SCALED);

		JMenuItem exportAsItem = new JMenuItem("Export");
		exportAsItem.addActionListener(_ -> ImportExportManager.exportData(firstpass));
		exportAsItem.setIcon(Config.getDarkMode() ? Icons.EXPORT_ICON_WHITE_SCALED : Icons.EXPORT_ICON_SCALED);

		JMenuItem importAsItem = new JMenuItem("Import");
		importAsItem.setIcon(Config.getDarkMode() ? Icons.IMPORT_ICON_WHITE_SCALED : Icons.IMPORT_ICON_SCALED);
		importAsItem.addActionListener(_ -> ImportExportManager.importData(firstpass));

		JMenuItem exitItem = new JMenuItem("Save & Exit");
		exitItem.addActionListener(_ -> firstpass.exit());
		exitItem.setIcon(Config.getDarkMode() ? Icons.EXIT_ICON_WHITE_SCALED : Icons.EXIT_ICON_SCALED);

		JMenu fileItem = new JMenu("File");
		fileItem.add(saveItem);
		fileItem.add(exportAsItem);
		fileItem.add(importAsItem);
		fileItem.add(exitItem);

		JMenu settingsButton = getSettingsButton();

		updateButton.setForeground(new Color(0, 180, 255));
		updateButton.setBorderPainted(true);
		updateButton.addActionListener(_ -> Updater.update());
		updateButton.setVisible(firstpass.isUpdateAvailable());

		toolBar.add(fileItem);
		toolBar.add(settingsButton);
		toolBar.add(updateButton);

		return toolBar;
	}

	/**
	 * Use a JMenu so it visually matches the other menus (hover/selection
	 * painting from the L&F). Prevent the normal popup behavior and invoke
	 * the settings dialog on click so it behaves like a button.
	 */
	private JMenu getSettingsButton() {
		JMenu settingsButton = new JMenu("Settings") {
			@Override
			public void setPopupMenuVisible(boolean b) {
				super.setPopupMenuVisible(false);
			}
		};

		settingsButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				SwingUtilities.invokeLater(() -> {
					new SettingsMenu(firstpass).showSettings();
					MenuSelectionManager.defaultManager().clearSelectedPath();
				});
			}
		});
		return settingsButton;
	}

	public JButton getUpdateButton() {
		return updateButton;
	}
}