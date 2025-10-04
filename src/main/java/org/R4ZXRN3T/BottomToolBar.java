package org.R4ZXRN3T;

import org.R4ZXRN3T.interfaces.AccountService;

import static org.R4ZXRN3T.Icons.*;

import java.awt.*;

import javax.swing.*;

public class BottomToolBar extends JToolBar {

	private final static Dimension DEFAULT_BUTTON_SIZE = new Dimension(105, 35);

	// only here to be able to refresh the button
	private final CustomButton undoButton;
	private final AccountService accountService;
	private final Firstpass firstpass;

	public BottomToolBar(AccountService accountService, Firstpass firstpass) {
		super();
		this.accountService = accountService;
		this.firstpass = firstpass;

		setFloatable(false);
		setLayout(new BorderLayout());

		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

		CustomButton addButton = new CustomButton("Add", Config.getDarkMode() ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED,
			_ -> accountService.addAccount(), DEFAULT_BUTTON_SIZE);
		CustomButton removeButton = new CustomButton("Remove", Config.getDarkMode() ? REMOVE_ICON_WHITE_SCALED : REMOVE_ICON_SCALED,
			_ -> accountService.removeAccount(firstpass.table.getSelectedRow()), true, DEFAULT_BUTTON_SIZE);
		CustomButton editButton = new CustomButton("Edit", Config.getDarkMode() ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED,
			_ -> accountService.editAccount(firstpass.table.getSelectedRow()), true, DEFAULT_BUTTON_SIZE);
		undoButton = new CustomButton("Undo", Config.getDarkMode() ? UNDO_ICON_WHITE_SCALED : UNDO_ICON_SCALED,
			_ -> accountService.undoDeletion(), DEFAULT_BUTTON_SIZE);
		refreshUndoButton();
		CustomButton generatorButton = new CustomButton("Generator", Config.getDarkMode() ? GENERATE_ICON_WHITE_SCALED : GENERATE_ICON_SCALED, _ -> {
			PasswordGenerator passwordGenerator = new PasswordGenerator();
			passwordGenerator.show();
			passwordGenerator.close();
		}, DEFAULT_BUTTON_SIZE);
		JPanel exitPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		CustomButton exitButton = new CustomButton("Save & Exit", Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED,
			_ -> firstpass.exit(), new Dimension(125, 35));

		buttonPanel.add(addButton);
		buttonPanel.add(removeButton);
		buttonPanel.add(editButton);
		buttonPanel.add(undoButton);
		buttonPanel.add(generatorButton);
		exitPanel.add(exitButton, BorderLayout.EAST);
		add(buttonPanel, BorderLayout.WEST);
		add(exitPanel, BorderLayout.EAST);
	}

	// refresh the undo button
	// this damn button is annoying
	public void refreshUndoButton() {
		undoButton.setEnabled(accountService.isUndoAvailable());
	}

	public static JToolBar getToolBar(AccountService accountService, Firstpass firstpass) {
		return new BottomToolBar(accountService, firstpass);
	}
}