package org.R4ZXRN3T;

import org.R4ZXRN3T.Account.SearchableField;
import org.R4ZXRN3T.interfaces.AccountService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

// wierd shit happens here
public class SearchPanel extends JPanel {
	private static SearchableField selectedSearchOption = SearchableField.ALL;
	private final AccountService accountService;

	public SearchPanel(AccountService accountService) {
		this.accountService = accountService;
		setLayout(new BorderLayout(5, 5));

		JTextField searchField = new JTextField();
		String[] searchOptions = {"All", "Provider", "Username", "URL", "Comment"};
		JComboBox<String> searchOptionsBox = new JComboBox<>(searchOptions);

		searchField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				accountService.search(searchField.getText());
			}
		});

		searchOptionsBox.addActionListener(_ -> {
			selectedSearchOption = Account.SearchableField.fromString(searchOptionsBox.getItemAt(searchOptionsBox.getSelectedIndex()));
			accountService.search(searchField.getText());
		});

		add(new JLabel("Search: "), BorderLayout.WEST);
		add(searchField, BorderLayout.CENTER);
		add(searchOptionsBox, BorderLayout.EAST);
	}

	public static SearchableField getSelectedSearchOption() {
		return selectedSearchOption;
	}
}