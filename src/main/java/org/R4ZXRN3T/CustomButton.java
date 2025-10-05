package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

class CustomButton extends JButton {

	private final JLabel textLabel;

	// most simple constructor without icon
	public CustomButton(String text, ActionListener actionListener, Dimension size) {
		this(text, null, actionListener, false, false, size, null, null, null);
	}

	// most simple constructor with icon
	public CustomButton(String text, ImageIcon icon, ActionListener actionListener, Dimension size) {
		this(text, icon, actionListener, false, false, size, null, null, null);
	}

	// only relevant for the remove and edit button
	public CustomButton(String text, ImageIcon icon, ActionListener actionListener, AccountTable table, Dimension size) {
		this(text, icon, actionListener, false, false, size, null, null, null, table);
	}

	// default constructor for most customization
	public CustomButton(String text, ImageIcon icon, ActionListener actionListener, boolean listenForTableSelection, boolean focusable, Dimension size, Color customBackground, Color customForeground, Insets margins) {
		this(text, icon, actionListener, listenForTableSelection, focusable, size, customBackground, customForeground, margins, null);
	}

	// default constructor for most customization
	public CustomButton(String text, ImageIcon icon, ActionListener actionListener, boolean listenForTableSelection, boolean focusable, Dimension size, Color customBackground, Color customForeground, Insets margins, AccountTable table) {
		// add the action listener and set stuff
		this.addActionListener(actionListener);
		this.setPreferredSize(size);
		this.setLayout(new BorderLayout(0, 0));
		this.setBorderPainted(false);
		this.setMargin(margins == null ? new Insets(0, 10, 0, 10) : margins);
		this.setFocusable(focusable);
		this.setFocusPainted(true);

		// add contents, important because I just can't stand it when something is not centered properly.
		// Border is there too, because it looks better
		JLabel iconLabel = new JLabel();
		iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 4));
		this.textLabel = new JLabel(text, SwingConstants.CENTER);

		// set the icon and background color depending on the dark mode

		iconLabel.setIcon(icon);

		if (customBackground != null) this.setBackground(customBackground);
		if (customForeground != null) textLabel.setForeground(customForeground);

		if (listenForTableSelection || table != null) {
			this.setEnabled(table != null && table.isRowSelected() && table.isFocused());
			if (table != null)
				table.addRowSelectionListener(_ -> this.setEnabled(table.isRowSelected() && table.isFocused()));
		}

		// add stuff
		this.add(iconLabel, BorderLayout.WEST);
		this.add(textLabel, BorderLayout.CENTER);
	}

	// set the text of the button
	public void setText(String text) {
		textLabel.setText(text);
	}
}