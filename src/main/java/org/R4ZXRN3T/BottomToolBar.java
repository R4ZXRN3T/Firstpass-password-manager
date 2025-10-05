package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

import static org.R4ZXRN3T.Icons.*;

class BottomToolBar {

	public final static Color DARK_MODE_COLOR = new Color(50, 50, 50);
	private final static Dimension DEFAULT_BUTTON_SIZE = new Dimension(105, 35);
	private final Firstpass firstpass;
	// only here to be able to refresh the button
	private CustomButton undoButton;

	public BottomToolBar(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	// get and populate the toolbar
	public JToolBar getToolBar() {
		// init toolbar
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setLayout(new BorderLayout());

		// get panels
		JPanel buttonPanel = getButtonPanel();
		JPanel exitPanel = getExitPanel();

		// make sure the undo button is correct
		refreshUndoButton();

		// add panels to toolbar
		toolBar.add(buttonPanel, BorderLayout.WEST);
		toolBar.add(exitPanel, BorderLayout.EAST);

		return toolBar;
	}

	// returns the panel in which all the buttons live
	private JPanel getButtonPanel() {
		// init panel
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// create buttons
		CustomButton addButton = new CustomButton("Add", Config.getDarkMode() ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED, _ -> firstpass.addAccount(), DEFAULT_BUTTON_SIZE);
		CustomButton removeButton = new CustomButton("Remove", Config.getDarkMode() ? REMOVE_ICON_WHITE_SCALED : REMOVE_ICON_SCALED, _ -> firstpass.removeAccount(firstpass.getTable().getSelectedRow()), firstpass.getTable(), DEFAULT_BUTTON_SIZE);
		CustomButton editButton = new CustomButton("Edit", Config.getDarkMode() ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED, _ -> firstpass.editAccount(firstpass.getTable().getSelectedRow()), firstpass.getTable(), DEFAULT_BUTTON_SIZE);
		undoButton = new CustomButton("Undo", Config.getDarkMode() ? UNDO_ICON_WHITE_SCALED : UNDO_ICON_SCALED, _ -> firstpass.undoDeletion(), DEFAULT_BUTTON_SIZE);
		CustomButton settingsButton = new CustomButton("Generator", Config.getDarkMode() ? GENERATE_ICON_WHITE_SCALED : GENERATE_ICON_SCALED, _ -> PasswordGenerator.showPasswordGeneratorDialog(firstpass.getFrame(), Config.getDarkMode()), DEFAULT_BUTTON_SIZE);

		// add buttons to panel
		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(editButton);
		buttonPanel.add(undoButton);
		buttonPanel.add(settingsButton);

		return buttonPanel;
	}

	// returns the panel in which the exit button lives
	private JPanel getExitPanel() {
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		exitPanel.add(new CustomButton("Save & Exit", Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, _ -> firstpass.exit(), new Dimension(125, 35)), BorderLayout.EAST);

		return exitPanel;
	}

	// refresh the undo button
	// this damn button is annoying
	public void refreshUndoButton() {
		undoButton.setEnabled(!firstpass.getUndoStack().isEmpty());
	}
}