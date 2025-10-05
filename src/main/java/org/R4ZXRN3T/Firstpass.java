package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.Stack;

import static org.R4ZXRN3T.Icons.*;

public class Firstpass {

	public static final String CURRENT_VERSION = resolveVersion();

	private static String resolveVersion() {
		Package pkg = Firstpass.class.getPackage();
		String v = (pkg != null) ? pkg.getImplementationVersion() : null;
		return (v != null) ? v : "DEV BUILD; NOT FOR PUBLIC USE";
	}

	// Instance variables - no longer static
	private final ArrayList<Account> accountList = new ArrayList<>();
	private final Stack<Account> undoStack = new Stack<>();
	private JFrame frame;
	private String correctPassword;
	private AccountTable table;
	private boolean changeMade = false;
	private boolean passwordSet = true;
	private boolean updateAvailable = false;
	private BottomToolBar bottomToolBar;
	private TopToolBar topToolBar;

	public void run() {

		// general initialization
		Locale.setDefault(Locale.ENGLISH);

		Config.init(this);

		// check whether an update is available
		// in a separate thread, as on slow internet connection this might take a while
		if (Boolean.parseBoolean(Config.getConfig(Config.ConfigKey.CHECK_FOR_UPDATES))) new Thread(() -> {
			updateAvailable = Updater.checkVersion(false).compareToIgnoreCase(CURRENT_VERSION) > 0;
			// Will be set after topToolBar is created
		}).start();

		new Thread(() -> {
			// delete installer files if existing
			// in new thread as I need a delay. If deletion happens too quickly the files are still open, thus can't be deleted
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				throw new RuntimeException(e);
			}
			new File("Firstpass_setup.msi").delete();
			new File("Firstpass_setup.msi.tmp").delete();
			new File("Firstpass_portable.jar.tmp").delete();
			new File("rename.bat").delete();
			new File("rename.sh").delete();
		}).start();

		// load and decrypt accounts
		correctPassword = checkPassword();
		if (correctPassword.isEmpty()) passwordSet = false;

		// Initialize Updater with this instance
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

	// add an account to the Account ArrayList
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

	// removes an account from the Account ArrayList
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

	// edit an account in the Account ArrayList
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

	// undo the last deletion from the undo stack
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

	// save accounts and config and exit the program
	public void exit() {
		save();
		System.exit(0);
	}

	public void save() {
		Files.saveAccounts(accountList, correctPassword);
		// generate new salt and thus also new encoded password; *VERY* important for security :)
		String newSalt = Tools.generateRandomString(16);
		Config.setConfig(Config.ConfigKey.SALT, newSalt);
		Config.setConfig(Config.ConfigKey.PASSWORD, Tools.encodePassword(correctPassword, newSalt));
		Config.saveConfig();
	}

	public void fullDelete() {
		accountList.clear();
		new File("accounts.txt").delete();
		new File("config.json").delete();
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

	// adds position in main ArrayList to each Account object
	public void refreshIndices() {
		for (int i = 0; i < accountList.size(); i++) accountList.get(i).setIndex(i);
	}

	// redraws the entire frame with the given Account ArrayList
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

				// check if user wants to save changes
				int option = JOptionPane.showOptionDialog(null, message, "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, options, options[0]);
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

	// refreshes only the table with the current Account ArrayList
	public void refreshTable() {
		refreshTable(accountList);
	}

	// redraws only the table with the given Account ArrayList
	public void refreshTable(ArrayList<Account> accountsArr) {
		table.setContent(accountsArr);
		table.revalidate();                        // self-explanatory
		table.repaint();
	}

	// provides input dialog for password and checks if it's correct. AiO basically :)
	private String checkPassword() {
		String currentSalt = Config.getConfig(Config.ConfigKey.SALT);
		String encodedPassword = Config.getConfig(Config.ConfigKey.PASSWORD);

		// If no password is set (null, empty, or equals hash of empty password with current salt) skip prompt
		if (encodedPassword == null || currentSalt == null || encodedPassword.isEmpty() || Objects.equals(encodedPassword, Tools.encodePassword("", currentSalt))) {
			return ""; // no password set
		}

		// initialize variables for inputDialog, in order to make the code more readable
		String enteredPassword = null;
		JLabel label = new JLabel();
		String promptMessage = "Please Enter your password: ";
		String title = "Firstpass Password Manager";

		JFrame tempFrame = new JFrame("Firstpass Password Manager");

		tempFrame.setUndecorated(true);
		tempFrame.setVisible(true);
		tempFrame.setLocationRelativeTo(null);
		tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tempFrame.setIconImage(FIRSTPASS_ICON.getImage());


		// loop until correct password is entered or the user exits
		do {
			// set message to incorrect password message if run the second time
			if (enteredPassword != null) {
				promptMessage = "Incorrect password. Please try again: ";
				label.setForeground(Color.RED);
			}
			// finally show the input dialog
			label.setText(promptMessage);
			enteredPassword = (String) JOptionPane.showInputDialog(tempFrame, label, title, JOptionPane.PLAIN_MESSAGE, null, null, null);

			// exit if user presses cancel
			if (enteredPassword == null) System.exit(0);
		} while (!Tools.encodePassword(enteredPassword, currentSalt).equals(encodedPassword));

		tempFrame.dispose();
		return enteredPassword;
	}

	// Getters for encapsulation
	public ArrayList<Account> getAccountList() {
		return accountList;
	}

	public Stack<Account> getUndoStack() {
		return undoStack;
	}

	public JFrame getFrame() {
		return frame;
	}

	public String getCorrectPassword() {
		return correctPassword;
	}

	public void setCorrectPassword(String correctPassword) {
		this.correctPassword = correctPassword;
	}

	public AccountTable getTable() {
		return table;
	}

	public void setChangeMade(boolean changeMade) {
		this.changeMade = changeMade;
	}

	public boolean isPasswordSet() {
		return passwordSet;
	}

	public void setPasswordSet(boolean passwordSet) {
		this.passwordSet = passwordSet;
	}

	public boolean isUpdateAvailable() {
		return updateAvailable;
	}

	public void setUpdateAvailable(boolean updateAvailable) {
		this.updateAvailable = updateAvailable;
	}

	public TopToolBar getTopToolBar() {
		return topToolBar;
	}
}

