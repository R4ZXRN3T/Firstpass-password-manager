package org.R4ZXRN3T.firstpass;

import org.R4ZXRN3T.firstpass.Account.SearchableField;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;

// wierd shit happens here
class SearchPanel extends JPanel {

	// selected search option with enum from the Accounts class
	// I can't decide whether it's good practice or not
	private static SearchableField selectedSearchOption = SearchableField.ALL;
	private final Firstpass firstpass;

	public SearchPanel(Firstpass firstpass) {
		super();
		this.firstpass = firstpass;
		// set stuff and add correct icon
		this.setLayout(new BorderLayout(6, 0));
		this.setFocusable(false);
		this.add(Config.getDarkMode() ? new JLabel(Icons.SEARCH_ICON_WHITE_SCALED) : new JLabel(Icons.SEARCH_ICON_SCALED), BorderLayout.WEST);

		// add a combo box to select the field to search in
		JComboBox<String> searchOptions = new JComboBox<>(new String[]{"All", "Name", "Username", "Password", "URL", "Comment"});
		searchOptions.setSelectedIndex(0);
		searchOptions.setFocusable(false);
		searchOptions.setBackground(Config.getDarkMode() ? BottomToolBar.DARK_MODE_COLOR : new Color(230, 230, 230));
		searchOptions.addActionListener(_ -> selectedSearchOption = SearchableField.fromValue(searchOptions.getSelectedIndex()));

		// add stuff again
		this.add(searchOptions, BorderLayout.EAST);
		this.add(new SearchBar(), BorderLayout.CENTER);
	}

	// get the selected search option
	public static SearchableField getSelectedSearchOption() {
		return selectedSearchOption;
	}

	class SearchBar extends JTextField {

		SearchBar() {
			super();

			// set stuff
			this.setFocusable(true);

			this.setBackground(Config.getDarkMode() ? BottomToolBar.DARK_MODE_COLOR : new Color(230, 230, 230));


			// used to get user input every time the user types something in
			this.getDocument().addDocumentListener(new DocumentListener() {
				@Override
				public void insertUpdate(DocumentEvent e) {
					performSearch();
				}

				@Override
				public void removeUpdate(DocumentEvent e) {
					performSearch();
				}

				@Override
				public void changedUpdate(DocumentEvent e) {
					performSearch();
				}

				// pass the user's search to Main class
				private void performSearch() {
					requestFocus();
					firstpass.search(getText());
					requestFocus();
				}
			});
		}
	}
}