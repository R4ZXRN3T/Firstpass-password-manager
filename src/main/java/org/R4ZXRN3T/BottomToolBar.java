package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

import static org.R4ZXRN3T.Icons.*;

class BottomToolBar {

	// took 10 minutes to get right
	public final static Color DARK_MODE_COLOR = new Color(50, 50, 50);
	private final static Dimension DEFAULT_BUTTON_SIZE = new Dimension(105, 35);

	// only here to be able to refresh the button
	private CustomButton undoButton;
	private final Firstpass firstpass;

	public BottomToolBar(Firstpass firstpass) {
		this.firstpass = firstpass;
	}

	// get and populate the toolbar
	public JToolBar getToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// addButton
		buttonPanel.add(new CustomButton("Add", Config.getDarkMode() ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED, _ -> firstpass.addAccount(), DEFAULT_BUTTON_SIZE));

		// removeButton
		buttonPanel.add(new CustomButton("Remove", Config.getDarkMode() ? REMOVE_ICON_WHITE_SCALED : REMOVE_ICON_SCALED, _ -> firstpass.removeAccount(firstpass.getTable().getSelectedRow()), firstpass.getTable(), DEFAULT_BUTTON_SIZE));

		// editButton
		buttonPanel.add(new CustomButton("Edit", Config.getDarkMode() ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED, _ -> firstpass.editAccount(firstpass.getTable().getSelectedRow()), firstpass.getTable(), DEFAULT_BUTTON_SIZE));
		// undoButton
		undoButton = new CustomButton("Undo", Config.getDarkMode() ? UNDO_ICON_WHITE_SCALED : UNDO_ICON_SCALED, _ -> firstpass.undoDeletion(), DEFAULT_BUTTON_SIZE);
		buttonPanel.add(undoButton);
		refreshUndoButton();
		// settingsButton
		buttonPanel.add(new CustomButton("Generator", Config.getDarkMode() ? GENERATE_ICON_WHITE_SCALED : GENERATE_ICON_SCALED, _ -> PasswordGenerator.showPasswordGeneratorDialog(firstpass.getFrame(), Config.getDarkMode()), DEFAULT_BUTTON_SIZE));
		// exitButton
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		exitPanel.add(new CustomButton("Save & Exit", Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, _ -> firstpass.exit(), new Dimension(125, 35)), BorderLayout.EAST);

		toolBar.add(buttonPanel, BorderLayout.WEST);
		toolBar.add(exitPanel, BorderLayout.EAST);

		return toolBar;
	}

	// refresh the undo button
	// this damn button is annoying
	public void refreshUndoButton() {
		undoButton.setEnabled(!firstpass.getUndoStack().isEmpty());
	}
}