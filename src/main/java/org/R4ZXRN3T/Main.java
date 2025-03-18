package org.R4ZXRN3T;

import com.formdev.flatlaf.*;
import com.formdev.flatlaf.themes.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.*;
import java.io.File;

import static org.R4ZXRN3T.Icons.*;

class Main {

	public static final String CURRENT_VERSION = "2.0.0";

	// global variables, important for not having to pass them around
	public static ArrayList<Account> accountList = new ArrayList<>();
	public static Stack<Account> undoStack = new Stack<>();
	public static JFrame frame;
	public static boolean darkMode;
	public static String correctPassword;
	public static AccountTable table;
	public static boolean changeMade = false;
	public static boolean passwordSet = true;
	public static boolean updateAvailable = false;

	public static void main(String[] args) {

		if (CURRENT_VERSION.compareToIgnoreCase(Updater.checkVersion()) < 0) updateAvailable = true;

		File installerFile = new File("Firstpass_setup.msi");
		if (installerFile.exists()) installerFile.delete();

		// general initialization
		Locale.setDefault(Locale.ENGLISH);
		darkMode = setLookAndFeel(Objects.requireNonNull(Files.getConfig(Files.LOOK_AND_FEEL)));
		Tools.checkConfig();

		// load and decrypt accounts
		correctPassword = checkPassword();
		if (correctPassword.isEmpty()) {
			passwordSet = false;
		}
		accountList = Files.getAccounts(correctPassword);
		table = new AccountTable(accountList);

		// initialize frame
		frame = new JFrame("Firstpass Password Manager");
		frame.setSize(1000, 650);
		frame.setMinimumSize(new Dimension(690, 270));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(FIRSTPASS_ICON.getImage());

		// add save prompt on exit
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				String message = "Do you want to save before exiting the program?";
				String[] options = {"Save", "Don't save", "Cancel"};

				// exit if no changes were made
				if (!changeMade) {
					System.exit(0);
				}

				// check if user wants to save changes
				int option = JOptionPane.showOptionDialog(null, message, "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, darkMode ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, options, options[0]);
				switch (option) {
					case JOptionPane.YES_OPTION:
						exit();
						break;
					case JOptionPane.NO_OPTION:
						System.exit(0);
						break;
					default:
						break;
				}
			}
		});

		/*frame.addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				Dimension size = frame.getSize();
				System.out.println("Current frame size: " + size.width + "x" + size.height);
			}
		});*/

		refreshFrame();
	}

	// add an account to the Account ArrayList
	public static void addAccount() {

		// initialize text fields
		JTextField providerField = new JTextField();
		JTextField usernameField = new JTextField();
		JTextField passwordField = new JTextField();
		JTextField URLField = new JTextField();
		JTextField commentField = new JTextField();

		// create message object
		Object[] message = {"Provider:", providerField, "Username:", usernameField, "Password:", passwordField, "URL:", URLField, "Comment:", commentField};

		// set correct icon
		ImageIcon icon = darkMode ? ADD_ICON_WHITE_SCALED : ADD_ICON_SCALED;

		// show dialog and add account if OK is pressed
		int option = JOptionPane.showConfirmDialog(null, message, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
		if (option == JOptionPane.OK_OPTION) {
			Account newAccount = new Account(providerField.getText(), usernameField.getText(), passwordField.getText(), URLField.getText(), commentField.getText());
			accountList.add(newAccount);
			refreshFrame();
			changeMade = true;
		}
	}

	// removes an account from the Account ArrayList
	public static void removeAccount(int rowIndex) {

		// check if row index is valid
		if (rowIndex >= 0 && rowIndex < table.getRowCount()) {

			// get correct account to put into undo stack
			Account removedAccount = table.getAccountAt(rowIndex);
			// set index of removed account, in order to get correct results from undoDeletion()
			removedAccount.setIndex(accountList.indexOf(removedAccount));
			// remove account from ArrayList if row index is valid
			accountList.removeIf(account -> account.equals(removedAccount));
			// add removed account to undo stack
			undoStack.push(removedAccount);
			// refresh stuff
			refreshFrame();
			BottomToolBar.refreshUndoButton();
			changeMade = true;
		} else {
			// very unlikely error
			JOptionPane.showMessageDialog(null, "No row selected or invalid row index.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// edit an account in the Account ArrayList
	public static void editAccount(int rowIndex) {

		// check if row index is valid
		if (rowIndex >= 0 && rowIndex < table.getRowCount()) {
			// get account to edit
			Account account = table.getAccountAt(rowIndex);
			// set index of account, in order to put it back into the correct position in the ArrayList
			account.setIndex(accountList.indexOf(account));

			// initialize text fields
			JTextField providerField = new JTextField(account.getProvider());
			JTextField usernameField = new JTextField(account.getUsername());
			JTextField passwordField = new JTextField(account.getPassword());
			JTextField URLField = new JTextField(account.getUrl());
			JTextField commentField = new JTextField(account.getComment());

			// create message object
			Object[] message = {"Provider:", providerField, "Username:", usernameField, "Password:", passwordField, "URL:", URLField, "Comment:", commentField};

			// set correct icon
			ImageIcon icon = darkMode ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED;

			// show dialog and edit account if OK is pressed
			int option = JOptionPane.showConfirmDialog(null, message, "Edit Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
			if (option == JOptionPane.OK_OPTION) {
				account.setProvider(providerField.getText());
				account.setUsername(usernameField.getText());
				account.setPassword(passwordField.getText());
				account.setUrl(URLField.getText());
				account.setComment(commentField.getText());
				removeAccount(rowIndex);
				accountList.add(account.getIndex(), account);
				refreshFrame();
				changeMade = true;
			}
		} else {
			JOptionPane.showMessageDialog(null, "No row selected or invalid row index.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// undo the last deletion from the undo stack
	public static void undoDeletion() {
		if (!undoStack.isEmpty()) {
			Account newAccount = undoStack.pop();
			// get index from Account Object, in order to put it back into the correct position in the ArrayList
			accountList.add(newAccount.getIndex(), newAccount);
			// refresh stuff
			refreshIndices();
			refreshFrame();
			BottomToolBar.refreshUndoButton();
			changeMade = true;
		} else {
			// very unlikely error
			JOptionPane.showMessageDialog(null, "No deletions to undo.", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	// sort using Bubblesort. expects a field for sorting, which is determined by the user clicking on the corresponding column
	public static void sort(int sortField, boolean isDescending) {

		accountList.sort((account1, account2) -> account1.compareTo(account2, sortField));

		if (!isDescending) {
			Collections.reverse(accountList);
		}

		refreshFrame();
		refreshIndices();
		changeMade = true;
	}

	// save accounts and config and exit the program
	public static void exit() {
		save();
		System.exit(0);
	}

	public static void save() {
		Files.saveAccounts(accountList, correctPassword);
		// generate new salt and thus also new encoded password; *VERY* important for security :)
		String newSalt = Tools.generateRandomString(16);
		Files.setConfig(Files.SALT, newSalt);
		Files.setConfig(Files.PASSWORD, Tools.encodePassword(correctPassword, newSalt));
	}

	public static void fullDelete() {
		accountList.clear();
		File accountsFile = new File("accounts.txt");
		accountsFile.delete();
		File configFile = new File("config.json");
		configFile.delete();
		Tools.restart();
	}

	// actually filters not searches, but we're not so picky with terminology :)
	public static void search(String searchQuery) {
		// creates new ArrayList to store search results for the table
		ArrayList<Account> searchResults = new ArrayList<>();
		if (searchQuery != null) {
			// adds results to searchResults ArrayList
			for (Account account : accountList) {
				if (account.contains(searchQuery, SearchPanel.getSelectedSearchOption())) {
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
	private static void refreshIndices() {
		for (int i = 0; i < accountList.size(); i++) {
			accountList.get(i).setIndex(i);
		}
	}

	// refreshes the frame with the current Account ArrayList
	public static void refreshFrame() {
		refreshFrame(accountList);
	}

	// redraws the entire frame with the given Account ArrayList
	public static void refreshFrame(ArrayList<Account> accountsArr) {

		table.setContent(accountsArr);       // populates the table
		refreshIndices();                    // maybe unnecessary, but just to be sure :)

		// create and initialize center panel
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(4, 4));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 32, 16));
		centerPanel.add(table.getScrollPane(), BorderLayout.CENTER);
		centerPanel.add(BottomToolBar.getToolBar(), BorderLayout.SOUTH);
		centerPanel.add(new SearchPanel(), BorderLayout.NORTH);

		// re-add stuff to frame
		frame.getContentPane().removeAll();
		frame.setJMenuBar(TopToolBar.getTopToolBar());
		frame.add(centerPanel);
		frame.revalidate();
		frame.repaint();
		frame.setVisible(true);
		frame.setEnabled(true);
	}

	// refreshes only the table with the current Account ArrayList
	public static void refreshTable() {
		refreshTable(accountList);
	}

	// redraws only the table with the given Account ArrayList
	public static void refreshTable(ArrayList<Account> accountsArr) {
		table.setContent(accountsArr);
		table.revalidate();                        // self-explanatory
		table.repaint();
	}

	// provides input dialog for password and checks if it's correct. AiO basically :)
	private static String checkPassword() {

		// initialize variables for inputDialog, in order to make the code more readable
		String enteredPassword = "[placeholder]";
		JLabel label = new JLabel();
		String promptMessage = "Please Enter your password: ";
		String title = "Firstpass Password Manager";

		if (Files.getConfig(Files.PASSWORD) == null || Objects.equals(Files.getConfig(Files.PASSWORD), Tools.encodePassword(Files.getConfig(Files.SALT), "")) || Objects.equals(Files.getConfig(Files.PASSWORD), "")) {
			return "";
		}

		// loop until correct password is entered or the user exits
		do {
			// set message to incorrect password message if run the second time
			if (!enteredPassword.equals("[placeholder]")) {
				promptMessage = "Incorrect password. Please try again: ";
				label.setForeground(Color.RED);
			}
			// finally show the input dialog
			label.setText(promptMessage);
			enteredPassword = (String) JOptionPane.showInputDialog(null, label, title, JOptionPane.PLAIN_MESSAGE, null, null, null);

			// exit if user presses cancel
			if (enteredPassword == null) {
				System.exit(0);
			}
		} while (!Tools.encodePassword(enteredPassword, Files.getConfig(Files.SALT)).equals(Files.getConfig(Files.PASSWORD)));

		return enteredPassword;
	}

	// sets the look and feel of the program. Also returns whether the look and feel is a dark mode theme
	private static boolean setLookAndFeel(String LaFIndex) {
		boolean isDarkTheme = false;
		System.out.println("\nSetting Look and Feel...");
		try {
			switch (LaFIndex) {
				case "0":
					UIManager.setLookAndFeel(new FlatLightLaf());
					break;
				case "1":
					UIManager.setLookAndFeel(new FlatDarkLaf());
					isDarkTheme = true;
					break;
				case "2":
					UIManager.setLookAndFeel(new FlatMacLightLaf());
					break;
				case "3":
					UIManager.setLookAndFeel(new FlatMacDarkLaf());
					isDarkTheme = true;
					break;
				case "4":
					UIManager.setLookAndFeel(new FlatIntelliJLaf());
					break;
				case "5":
					UIManager.setLookAndFeel(new FlatDarculaLaf());
					isDarkTheme = true;
					break;
				case "6":
					UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
					break;
				default:
					UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
					break;
			}
			UIManager.put("Component.focusWidth", 1);
			UIManager.put("Button.background", isDarkTheme ? new Color(50, 50, 50) : Color.lightGray);
			UIManager.put("Button.disabledBackground", new Color(0, 0, 0, 0));
			System.out.println("Look and Feel successfully set and custom settings applied.");
			return isDarkTheme;
		} catch (UnsupportedLookAndFeelException | ClassNotFoundException | InstantiationException |
				 IllegalAccessException e) {
			e.printStackTrace();
			System.out.println("Error setting Look and Feel. Exiting program...");
			System.exit(1);
		}
		return false;
	}




}