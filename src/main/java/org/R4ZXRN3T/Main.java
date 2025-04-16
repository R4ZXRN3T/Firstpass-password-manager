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

	public static final String CURRENT_VERSION = "2.0.9";

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
	public static boolean portableVersion = false;

	public static void main(String[] args) {

		portableVersion = Main.class.getResource("/assets/firstpass_icon.png") != null;

		// general initialization
		Locale.setDefault(Locale.ENGLISH);
		darkMode = setLookAndFeel(Objects.requireNonNull(Files.getConfig(Files.LOOK_AND_FEEL)));
		Tools.checkConfig();

		// check whether an update is available
		// in a separate thread, as on slow internet connection this might take a while
		if (Boolean.parseBoolean(Files.getConfig(Files.CHECK_FOR_UPDATES))) {
			new Thread(() -> {
				updateAvailable = Updater.checkVersion(false).compareToIgnoreCase(CURRENT_VERSION) > 0;
				TopToolBar.updateButton.setVisible(updateAvailable);
			}).start();
		}

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

		// initialize frame
		frame = new JFrame("Firstpass Password Manager v" + CURRENT_VERSION);
		frame.setSize(1000, 650);
		frame.setMinimumSize(new Dimension(690, 270));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(FIRSTPASS_ICON.getImage());

		table = new AccountTable(new ArrayList<>());

		new Thread(() -> {
			accountList = Files.getAccounts(correctPassword);
			table.setContent(accountList);
		}).start();

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
		initializeFrame();
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
		int option = JOptionPane.showConfirmDialog(frame, message, "Add Account", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, icon);
		if (option == JOptionPane.OK_OPTION) {
			Account newAccount = new Account(providerField.getText(), usernameField.getText(), passwordField.getText(), URLField.getText(), commentField.getText());
			for (Account account : accountList) {
				if (account.getProvider().equals(newAccount.getProvider())) {
					int keepOption = JOptionPane.showConfirmDialog(frame, "Account already exists! Do you still want to add it?", "Account already exists", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);
					if (keepOption == JOptionPane.NO_OPTION) {
						return;
					} else {
						break;
					}
				}
			}
			accountList.add(newAccount);
			refreshTable();
			changeMade = true;
		}
	}

	// removes an account from the Account ArrayList
	public static void removeAccount(int rowIndex) {

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
		BottomToolBar.refreshUndoButton();
		changeMade = true;
	}

	// edit an account in the Account ArrayList
	public static void editAccount(int rowIndex) {

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
		ImageIcon icon = darkMode ? EDIT_ICON_WHITE_SCALED : EDIT_ICON_SCALED;

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
		refreshTable();
		changeMade = true;
	}

	// undo the last deletion from the undo stack
	public static void undoDeletion() {
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
		BottomToolBar.refreshUndoButton();
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
		new File("accounts.txt").delete();
		new File("config.json").delete();
		System.exit(0);
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
	public static void refreshIndices() {
		for (int i = 0; i < accountList.size(); i++) {
			accountList.get(i).setIndex(i);
		}
	}

	// redraws the entire frame with the given Account ArrayList
	public static void initializeFrame() {

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
		String currentSalt = Files.getConfig(Files.SALT);
		String encodedPassword = Files.getConfig(Files.PASSWORD);

		JFrame tempFrame = new JFrame("Firstpass Password Manager");

		tempFrame.setUndecorated(true);
		tempFrame.setVisible(true);
		tempFrame.setLocationRelativeTo(null);
		tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tempFrame.setIconImage(FIRSTPASS_ICON.getImage());

		if (Files.getConfig(Files.PASSWORD) == null || Objects.equals(Files.getConfig(Files.PASSWORD), Tools.encodePassword(Files.getConfig(Files.SALT), "")) || Objects.equals(Files.getConfig(Files.PASSWORD), "")) {
			tempFrame.dispose();
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
			enteredPassword = (String) JOptionPane.showInputDialog(tempFrame, label, title, JOptionPane.PLAIN_MESSAGE, null, null, null);

			// exit if user presses cancel
			if (enteredPassword == null) {
				System.exit(0);
			}
		} while (!Tools.encodePassword(enteredPassword, currentSalt).equals(encodedPassword));

		tempFrame.dispose();
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