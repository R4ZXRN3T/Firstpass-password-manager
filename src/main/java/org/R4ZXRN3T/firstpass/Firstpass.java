package org.R4ZXRN3T.firstpass;

import static org.R4ZXRN3T.firstpass.Icons.*;

import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import javax.swing.*;
import javax.swing.event.AncestorEvent;
import javax.swing.event.AncestorListener;

public class Firstpass {

	public static final String CURRENT_VERSION = "2.1.1";
	private final ArrayList<Account> accountList = new ArrayList<>();
	private final Stack<Account> undoStack = new Stack<>();
	private JFrame frame;
	private String correctPassword;
	private AccountTable table;
	private boolean changeMade = false;
	private boolean updateAvailable = false;
	private BottomToolBar bottomToolBar;
	private TopToolBar topToolBar;

	/**
	 * This funtion is the main function responsible for initializing and running the program.
	 */
	public void run() {

		// general initialization
		Locale.setDefault(Locale.ENGLISH);

		Config.init(this);

		// check whether an update is available
		// in a separate thread, as on slow internet connection this might take a while
		if (Boolean.parseBoolean(Config.getConfig(Config.ConfigKey.CHECK_FOR_UPDATES))) new Thread(() -> {
			updateAvailable = Updater.checkVersion(false).compareToIgnoreCase(CURRENT_VERSION) > 0;
			if (getTopToolBar() != null) getTopToolBar().getUpdateButton().setVisible(isUpdateAvailable());
		}).start();

		deleteFiles();

		correctPassword = checkPassword();

		Updater.initialize(this);

		table = new AccountTable(new ArrayList<>(), this);
		bottomToolBar = new BottomToolBar(this);
		topToolBar = new TopToolBar(this);

		new Thread(() -> {
			accountList.addAll(Files.getAccounts(correctPassword));
			table.setMain(this);
			table.setContent(accountList);
		}).start();

		initializeFrame();
		frame.setVisible(true);
	}

	/**
	 * Kills the entire program and resets everything. Save to restart afterwards.
	 */
	public void kill() {
		if (frame != null) frame.dispose();
		frame = null;
		correctPassword = null;
		accountList.clear();
		undoStack.clear();
		table = null;
		bottomToolBar = null;
		topToolBar = null;
		System.gc();
	}

	/**
	 * Prompts the user to add a new account and adds it to the Account ArrayList.
	 */
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
		int option = JOptionPane.showConfirmDialog(frame, message, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
		if (option == JOptionPane.OK_OPTION) {
			Account newAccount = new Account(providerField.getText(), usernameField.getText(), passwordField.getText(), URLField.getText(), commentField.getText());
			for (Account account : accountList) {
				if (account.equals(newAccount, Account.SearchableField.PROVIDER)) {
					int keepOption = JOptionPane.showConfirmDialog(frame, "Account already exists! Do you still want to add it?", "Account already exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (keepOption == JOptionPane.NO_OPTION) return;
					else break;
				}
			}
			accountList.add(newAccount);
			refreshTable();
			changeMade = true;
		}
	}

	/**
	 * Removes an account from the Account ArrayList.
	 *
	 * @param rowIndex the index of the account to remove
	 *
	 */
	public void removeAccount(int rowIndex) {

		// check if row index is valid
		if (rowIndex < 0 || rowIndex > table.getRowCount()) {
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
		refreshTable();
		bottomToolBar.refreshUndoButton();
		changeMade = true;
	}

	/**
	 * Prompts the user to edit an existing account and updates it in the Account ArrayList.
	 *
	 * @param rowIndex the index of the account to edit
	 *
	 */
	public void editAccount(int rowIndex) {

		// check if row index is valid
		if (rowIndex < 0 || rowIndex > table.getRowCount()) {
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
		if (option != JOptionPane.OK_OPTION) return;

		accountList.get(rowIndex).setProvider(providerField.getText());
		accountList.get(rowIndex).setUsername(usernameField.getText());
		accountList.get(rowIndex).setPassword(passwordField.getText());
		accountList.get(rowIndex).setUrl(URLField.getText());
		accountList.get(rowIndex).setComment(commentField.getText());
		refreshTable();
		changeMade = true;
	}

	/**
	 * Restores the last deleted account from the undo stack back into the Account ArrayList.
	 */
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
		refreshTable();
		changeMade = true;
		bottomToolBar.refreshUndoButton();
	}

	/**
	 * save accounts and config and exit the program
	 */
	public void exit() {
		save();
		System.exit(0);
	}

	/**
	 * Saves all accounts to file, generates a new salt, hashes it with the password and then saves the config file
	 */
	public void save() {
		Files.saveAccounts(accountList, correctPassword);
		// generate new salt and thus also new encoded password; *VERY* important for security :)
		String newSalt = Tools.generateRandomString(Config.SALT_LENGTH);
		Config.setConfig(Config.ConfigKey.SALT, newSalt);
		Config.setConfig(Config.ConfigKey.PASSWORD, Tools.encodePassword(correctPassword, newSalt));
		Config.saveConfig();
	}

	/**
	 * Completely removes the config and all account data from disk. Called from the settings menu
	 */
	public void fullDelete() {
		accountList.clear();
		new File(Files.ACCOUNTS_PATH).delete();
		new File(Config.CONFIG_PATH).delete();
		if (!Config.isPortableVersion()) new File(Paths.get(System.getenv("APPDATA")) + "\\Firstpass").delete();
		System.exit(0);
	}

	// actually filters not searches, but we're not so picky with terminology :)
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
			refreshTable(searchResults);
		} else {
			// refreshes table with all accounts
			refreshTable();
		}
	}

	/**
	 * Changes the value of each index field in the account objects to reflect their position in the ArrayList
	 */
	public void refreshIndices() {
		for (int i = 0; i < accountList.size(); i++) accountList.get(i).setIndex(i);
	}

	/**
	 * Initializes the main application frame and adds all items to it.
	 */
	public void initializeFrame() {
		// create and initialize center panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(4, 4));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 32, 16));
		centerPanel.add(table.getScrollPane(), BorderLayout.CENTER);
		centerPanel.add(bottomToolBar.getToolBar(), BorderLayout.SOUTH);
		centerPanel.add(new SearchPanel(this), BorderLayout.NORTH);

		// initialize frame
		frame = new JFrame("Firstpass Password Manager v" + CURRENT_VERSION);
		frame.setSize(1000, 650);
		frame.setMinimumSize(new Dimension(690, 270));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(FIRSTPASS_ICON.getImage());
		frame.setExtendedState(JFrame.NORMAL);

		// add save prompt on exit
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				String message = "Do you want to save before exiting the program?";
				String[] options = {"Save", "Don't save", "Cancel"};

				// exit if no changes were made
				if (!changeMade) System.exit(0);

				// dialog to check if user wants to save changes
				int option = JOptionPane.showOptionDialog(
						frame,
						message,
						"Save changes?",
						JOptionPane.YES_NO_CANCEL_OPTION,
						JOptionPane.QUESTION_MESSAGE,
						Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED,
						options,
						options[0]
				);
				switch (option) {
					case JOptionPane.YES_OPTION -> exit();
					case JOptionPane.NO_OPTION -> System.exit(0);
				}
			}
		});

		// re-add stuff to frame
		frame.getContentPane().removeAll();
		frame.setJMenuBar(topToolBar.getTopToolBar());
		frame.add(centerPanel);
		frame.revalidate();
		frame.repaint();
	}

	/**
	 * refreshes the table, using the main Account ArrayList
	 */
	public void refreshTable() {
		refreshTable(accountList);
	}

	/**
	 * refreshes the table with a given Account ArrayList
	 *
	 * @param accountsArr the ArrayList of accounts to display in the table
	 *
	 */
	public void refreshTable(ArrayList<Account> accountsArr) {
		table.setContent(accountsArr);
		table.revalidate();
		table.repaint();
	}

	/**
	 * Prompts the user for entering a correct password. This will be checked and if correct,
	 * the user is let in and the correct password is returned.
	 *
	 * @return the correct password as String
	 *
	 */
	private String checkPassword() {
		String currentSalt = Config.getConfig(Config.ConfigKey.SALT);
		String encodedPassword = Config.getConfig(Config.ConfigKey.PASSWORD);

		// If no password is set (null, empty, or equals hash of empty password with current salt) skip prompt
		if (encodedPassword == null || currentSalt == null || encodedPassword.isEmpty() || Objects.equals(encodedPassword, Tools.encodePassword("", currentSalt))) {
			return ""; // no password set
		}

		// initialize variables for inputDialog, in order to make the code more readable
		JLabel label = new JLabel();
		String promptMessage = "Please Enter your password: ";
		String title = "Firstpass Password Manager";
		JPasswordField passwordField = new JPasswordField();

		Object[] message = {label, passwordField};

		JFrame tempFrame = new JFrame("Firstpass Password Manager");
		tempFrame.setUndecorated(true);
		tempFrame.setVisible(true);
		tempFrame.setLocationRelativeTo(null);
		tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tempFrame.setIconImage(FIRSTPASS_ICON.getImage());

		boolean firstRun = true;
		do {
			if (!firstRun) {
				promptMessage = "Incorrect password. Please try again: ";
				label.setForeground(Color.RED);
				passwordField.setText("");
			}

			label.setText(promptMessage);

			passwordField.addAncestorListener(new RequestFocusListener());
			int option = JOptionPane.showConfirmDialog(tempFrame, message, title, JOptionPane.OK_CANCEL_OPTION);
			if (option != JOptionPane.OK_OPTION) System.exit(0);

			firstRun = false;
		} while (!Tools.encodePassword(String.valueOf(passwordField.getPassword()), currentSalt).equals(encodedPassword));

		tempFrame.dispose();
		return String.valueOf(passwordField.getPassword());
	}

	/**
	 * Helper method to remove any installer related files
	 */
	private void deleteFiles() {
		new Thread(() -> {
			// delete installer files if existing
			// in new thread as I need a delay. If deletion happens too quickly the files are still open, thus can't be deleted
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			Path parentPath = Config.getConfigFilePath().toAbsolutePath().getParent();
			if (!parentPath.toFile().exists()) return;
			new File(parentPath + "/Firstpass_setup.msi").delete();
			new File(parentPath + "/Firstpass_setup.msi.tmp").delete();
			new File(parentPath + "/Firstpass_setup.exe").delete();
			new File(parentPath + "/Firstpass_setup.exe.tmp").delete();
			new File(parentPath + "/Firstpass_portable.jar.tmp").delete();
			new File(parentPath + "/rename.bat").delete();
			new File(parentPath + "/rename.sh").delete();
		}).start();
	}

	/**
	 * Gets the accountList
	 *
	 * @return the ArrayList of accounts
	 */
	public ArrayList<Account> getAccountList() {
		return accountList;
	}

	/**
	 * Gets the undoStack
	 *
	 * @return the Stack of deleted accounts
	 */
	public Stack<Account> getUndoStack() {
		return undoStack;
	}

	/**
	 * Gets the main application frame
	 *
	 * @return the JFrame of the main application
	 */
	public JFrame getFrame() {
		return frame;
	}

	/**
	 * Gets the correct password
	 *
	 * @return the correct password as String
	 */
	public String getCorrectPassword() {
		return correctPassword;
	}

	/**
	 * Sets the correct password
	 *
	 * @param correctPassword the correct password as String
	 */
	public void setCorrectPassword(String correctPassword) {
		this.correctPassword = correctPassword;
	}

	/**
	 * Gets the account table
	 *
	 * @return the AccountTable object
	 */
	public AccountTable getTable() {
		return table;
	}

	/**
	 * Sets changeMade to the value
	 *
	 * @param changeMade the value to set it to
	 */
	public void setChangeMade(boolean changeMade) {
		this.changeMade = changeMade;
	}

	/**
	 * returns the value of the upodateAvailable variable
	 *
	 * @return true if an update is available, false otherwise
	 */
	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

	/**
	 * sets the value of the updateAvailable variable
	 *
	 * @param updateAvailable true if an update is available, false otherwise
	 */
	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	/**
	 * gets the bottom tool bar
	 *
	 * @return the BottomToolBar object
	 */
	public TopToolBar getTopToolBar() {
		return topToolBar;
	}

	/**
	 * <p>This class can be added to a component to immediately focus them on display.</p>
	 * <p>Only used for JOptionPanes.</p>
	 */
	public static class RequestFocusListener implements AncestorListener {
		public RequestFocusListener() {
		}

		@Override
		public void ancestorAdded(AncestorEvent e) {
			JComponent component = e.getComponent();
			component.requestFocusInWindow();
		}

		@Override
		public void ancestorMoved(AncestorEvent e) {
		}

		@Override
		public void ancestorRemoved(AncestorEvent e) {
		}
	}
}