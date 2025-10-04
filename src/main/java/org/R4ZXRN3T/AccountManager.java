package org.R4ZXRN3T;

import org.R4ZXRN3T.interfaces.AccountService;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Stack;

import static org.R4ZXRN3T.Icons.*;

public class AccountManager implements AccountService {

	private ArrayList<Account> accountList;
	private final Stack<Account> undoStack;
	private final JFrame mainFrame;
	private final Firstpass firstpass;

	public AccountManager(JFrame mainFrame, Firstpass firstpass) {
		this.accountList = new ArrayList<>();
		this.undoStack = new Stack<>();
		this.mainFrame = mainFrame;
		this.firstpass = firstpass;
	}

	@Override
	public ArrayList<Account> getAccounts() {
		return accountList;
	}

	@Override
	public void setAccounts(ArrayList<Account> accounts) {
		this.accountList = accounts;
	}

	@Override
	public boolean isUndoAvailable() {
		return !undoStack.isEmpty();
	}

	public void close() {
		accountList.clear();
		undoStack.clear();
	}

	// add an account to the Account ArrayList
	@Override
	public void addAccount() {
		// initialize text fields
		JTextField providerField = new JTextField();
		JTextField usernameField = new JTextField();
		JTextField passwordField = new JTextField();
		JTextField URLField = new JTextField();
		JTextField commentField = new JTextField();

		// create message object
		Object[] message = {"Provider:", providerField, "Username:", usernameField, "Password:", passwordField, "URL:", URLField, "Comment:", commentField};

		// set correct icon
		ImageIcon icon = Config.getDarkMode() ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED;

		// show dialog and add account if OK is pressed
		int option = JOptionPane.showConfirmDialog(mainFrame, message, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
		if (option == JOptionPane.OK_OPTION) {
			Account newAccount = new Account(providerField.getText(), usernameField.getText(), passwordField.getText(), URLField.getText(), commentField.getText());

			for (Account account : accountList) {
				if (account.equalsField(newAccount, Account.SearchableField.PROVIDER)) {
					int keepOption = JOptionPane.showConfirmDialog(mainFrame, "Account already exists! Do you still want to add it?", "Account already exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (keepOption == JOptionPane.NO_OPTION) return;
					else break;
				}
			}
			accountList.add(newAccount);
			firstpass.refreshTable();
			firstpass.setChangeMade(true);
		}
	}

	// removes an account from the Account ArrayList
	@Override
	public void removeAccount(int rowIndex) {
		// check if row index is valid
		if (rowIndex < 0 || rowIndex >= accountList.size()) {
			// very unlikely error
			JOptionPane.showMessageDialog(null, "No row selected or invalid row index.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// add account for deletion to undo stack
		undoStack.push(new Account(
				accountList.get(rowIndex).getProvider(),
				accountList.get(rowIndex).getUsername(),
				accountList.get(rowIndex).getPassword(),
				accountList.get(rowIndex).getUrl(),
				accountList.get(rowIndex).getComment(),
				rowIndex
		));

		accountList.remove(rowIndex);

		// finish up
		refreshIndices();
		firstpass.refreshTable();
		firstpass.setChangeMade(true);
	}

	// edit an account in the Account ArrayList
	@Override
	public void editAccount(int rowIndex) {
		// check if row index is valid
		if (rowIndex < 0 || rowIndex >= accountList.size()) {
			JOptionPane.showMessageDialog(null, "No row selected or invalid row index.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		// initialize text fields
		JTextField providerField = new JTextField(accountList.get(rowIndex).getProvider());
		JTextField usernameField = new JTextField(accountList.get(rowIndex).getUsername());
		JTextField passwordField = new JTextField(accountList.get(rowIndex).getPassword());
		JTextField URLField = new JTextField(accountList.get(rowIndex).getUrl());
		JTextField commentField = new JTextField(accountList.get(rowIndex).getComment());

		// create message object
		Object[] message = {"Provider:", providerField, "Username:", usernameField, "Password:", passwordField, "URL:", URLField, "Comment:", commentField};

		// set correct icon
		ImageIcon icon = Config.getDarkMode() ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED;

		// show dialog and edit account if OK is pressed
		int option = JOptionPane.showConfirmDialog(null, message, "Edit Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
		if (option != JOptionPane.OK_OPTION) {
			return;
		}

		accountList.get(rowIndex).setProvider(providerField.getText());
		accountList.get(rowIndex).setUsername(usernameField.getText());
		accountList.get(rowIndex).setPassword(passwordField.getText());
		accountList.get(rowIndex).setUrl(URLField.getText());
		accountList.get(rowIndex).setComment(commentField.getText());
		firstpass.refreshTable();
		firstpass.setChangeMade(true);
	}

	// undo the last deletion from the undo stack
	@Override
	public void undoDeletion() {
		if (undoStack.isEmpty()) {
			JOptionPane.showMessageDialog(null, "No deletions to undo.", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}
		Account newAccount = undoStack.pop();
		// get index from Account Object, in order to put it back into the correct position in the ArrayList
		accountList.add(newAccount.getIndex(), newAccount);
		// refresh stuff
		refreshIndices();
		firstpass.refreshTable();
		firstpass.setChangeMade(true);
	}

	// actually filters not searches, but we're not so picky with terminology :)
	@Override
	public void search(String searchQuery) {
		// creates new ArrayList to store search results for the table
		ArrayList<Account> searchResults = new ArrayList<>();
		if (!searchQuery.isEmpty()) {
			// adds results to searchResults ArrayList
			for (Account account : accountList) {
				if (account.containsIgnoreCase(searchQuery, SearchPanel.getSelectedSearchOption())) {
					searchResults.add(account);
				}
			}
			// refreshes table with search results
			firstpass.refreshTable(searchResults);
		} else {
			// refreshes table with all accounts
			firstpass.refreshTable();
		}
	}

	// adds position in main ArrayList to each Account object
	@Override
	public void refreshIndices() {
		for (int i = 0; i < accountList.size(); i++) {
			accountList.get(i).setIndex(i);
		}
	}
}