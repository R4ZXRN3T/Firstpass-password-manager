package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;

import static org.R4ZXRN3T.Icons.*;
import static org.R4ZXRN3T.Main.*;

class BottomToolBar {

	// took 10 minutes to get right
	public final static Color DARK_MODE_COLOR = new Color(50, 50, 50);
	private final static Dimension DEFAULT_BUTTON_SIZE = new Dimension(105, 35);

	// only here to be able to refresh the button
	private static CustomButton undoButton;

	// get and populate the toolbar
	public static JToolBar getToolBar() {
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);
		toolBar.setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		// addButton
		buttonPanel.add(new CustomButton("Add", Main.darkMode ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED, e -> addAccount(), DEFAULT_BUTTON_SIZE));

		// removeButton
		buttonPanel.add(new CustomButton("Remove", Main.darkMode ? REMOVE_ICON_WHITE_SCALED : REMOVE_ICON_SCALED, e -> Main.removeAccount(Main.table.getSelectedRow()), true, DEFAULT_BUTTON_SIZE));

		// editButton
		buttonPanel.add(new CustomButton("Edit", Main.darkMode ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED, e -> Main.editAccount(Main.table.getSelectedRow()), true, DEFAULT_BUTTON_SIZE));
		// undoButton
		undoButton = new CustomButton("Undo", Main.darkMode ? UNDO_ICON_WHITE_SCALED : UNDO_ICON_SCALED, e -> Main.undoDeletion(), DEFAULT_BUTTON_SIZE);
		buttonPanel.add(undoButton);
		refreshUndoButton();
		// settingsButton
		buttonPanel.add(new CustomButton("Generator", Main.darkMode ? GENERATE_ICON_WHITE_SCALED : GENERATE_ICON_SCALED, e -> Tools.passwordGeneratorDialog(), DEFAULT_BUTTON_SIZE));
		// exitButton
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		exitPanel.add(new CustomButton("Save & Exit", Main.darkMode ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, e -> Main.exit(), new Dimension(125, 35)), BorderLayout.EAST);

		toolBar.add(buttonPanel, BorderLayout.WEST);
		toolBar.add(exitPanel, BorderLayout.EAST);

		return toolBar;
	}

	// refresh the undo button
	// this damn button is annoying
	public static void refreshUndoButton() {
		undoButton.setEnabled(!Main.undoStack.isEmpty());
	}
}