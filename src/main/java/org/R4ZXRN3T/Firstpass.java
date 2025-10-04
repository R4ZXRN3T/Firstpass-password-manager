package org.R4ZXRN3T;

import org.R4ZXRN3T.Config.ConfigKey;
import org.R4ZXRN3T.interfaces.AccountService;
import org.R4ZXRN3T.interfaces.FileService;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static org.R4ZXRN3T.Icons.*;

public class Firstpass {
	public static final String CURRENT_VERSION = "2.0.0";
	public JFrame frame;
	public String correctPassword;
	public AccountTable table;
	public boolean changeMade = false;
	public boolean passwordSet = true;
	public boolean updateAvailable = false;
	public boolean portableVersion = false;

	private final AccountService accountService;
	private final FileService fileService;

	public Firstpass(AccountService accountService, FileService fileService) {
		this.accountService = accountService;
		this.fileService = fileService;
	}

	public static class RestartException extends RuntimeException {
	}

	public void setChangeMade(boolean changeMade) {
		this.changeMade = changeMade;
	}

	public void run() {
		portableVersion = Firstpass.class.getResource("/assets/firstpass_icon.png") != null;
		Locale.setDefault(Locale.ENGLISH);
		if (Boolean.parseBoolean(Config.getConfig(ConfigKey.CHECK_FOR_UPDATES))) {
			new Thread(() -> {
				updateAvailable = Updater.checkVersion(false, true).compareToIgnoreCase(CURRENT_VERSION) > 0;
				TopToolBar.updateButton.setVisible(updateAvailable);
			}).start();
		}
		new Thread(() -> {
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
		correctPassword = checkPassword();
		if (correctPassword.isEmpty()) passwordSet = false;
		frame = new JFrame("Firstpass Password Manager v" + CURRENT_VERSION);
		frame.setSize(1000, 650);
		frame.setMinimumSize(new Dimension(690, 270));
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setIconImage(FIRSTPASS_ICON.getImage());
		table = new AccountTable(new ArrayList<>(), accountService);
		new Thread(() -> {
			ArrayList<Account> accounts = fileService.getAccounts(correctPassword);
			accountService.setAccounts(accounts);
			table.setContent(accounts);
		}).start();
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				String message = "Do you want to save before exiting the program?";
				String[] options = {"Save", "Don't save", "Cancel"};
				if (!changeMade) {
					System.exit(0);
				}
				int option = JOptionPane.showOptionDialog(null, message, "Save changes?", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, Config.getDarkMode() ? EXIT_ICON_WHITE_SCALED : EXIT_ICON_SCALED, options, options[0]);
				switch (option) {
					case JOptionPane.YES_OPTION -> exit();
					case JOptionPane.NO_OPTION -> System.exit(0);
					default -> {
					}
				}
			}
		});
		initializeFrame();
	}

	public void exit() {
		save();
		System.exit(0);
	}

	public void save() {
		fileService.saveAccounts(accountService.getAccounts(), correctPassword);
		String newSalt = Tools.generateRandomString(16);
		Config.setConfig(Config.ConfigKey.SALT, newSalt);
		Config.setConfig(Config.ConfigKey.PASSWORD, Tools.encodePassword(correctPassword, newSalt));
	}

	public void fullDelete() {
		accountService.setAccounts(new ArrayList<>());
		new File("accounts.txt").delete();
		new File("config.json").delete();
		System.exit(0);
	}

	public void initializeFrame() {
		JPanel centerPanel = new JPanel();
		centerPanel.setLayout(new BorderLayout(4, 4));
		centerPanel.setBorder(BorderFactory.createEmptyBorder(8, 16, 32, 16));
		centerPanel.add(table.getScrollPane(), BorderLayout.CENTER);
		centerPanel.add(BottomToolBar.getToolBar(accountService, this), BorderLayout.SOUTH);
		centerPanel.add(new SearchPanel(accountService), BorderLayout.NORTH);
		frame.getContentPane().removeAll();
		frame.setJMenuBar(new TopToolBar(this));
		frame.add(centerPanel);
		frame.revalidate();
		frame.repaint();
		frame.setVisible(true);
	}

	public void refreshTable() {
		refreshTable(accountService.getAccounts());
	}

	public void refreshTable(ArrayList<Account> accountsArr) {
		table.setContent(accountsArr);
		table.revalidate();
		table.repaint();
	}

	private String checkPassword() {
		String enteredPassword = "[placeholder]";
		JLabel label = new JLabel();
		String promptMessage = "Please Enter your password: ";
		String title = "Firstpass Password Manager";
		String currentSalt = Config.getConfig(Config.ConfigKey.SALT);
		String encodedPassword = Config.getConfig(Config.ConfigKey.PASSWORD);
		JFrame tempFrame = new JFrame("Firstpass Password Manager");
		tempFrame.setUndecorated(true);
		tempFrame.setVisible(true);
		tempFrame.setLocationRelativeTo(null);
		tempFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		tempFrame.setIconImage(FIRSTPASS_ICON.getImage());
		if (Config.getConfig(Config.ConfigKey.PASSWORD) == null || Objects.equals(Config.getConfig(Config.ConfigKey.PASSWORD), Tools.encodePassword(Config.getConfig(Config.ConfigKey.SALT), "")) || Objects.equals(Config.getConfig(Config.ConfigKey.PASSWORD), "")) {
			tempFrame.dispose();
			return "";
		}
		do {
			if (!enteredPassword.equals("[placeholder]")) {
				promptMessage = "Incorrect password. Please try again: ";
				label.setForeground(Color.RED);
			}
			label.setText(promptMessage);
			enteredPassword = (String) JOptionPane.showInputDialog(tempFrame, label, title, JOptionPane.PLAIN_MESSAGE, null, null, null);
			if (enteredPassword == null) {
				System.exit(0);
			}
		} while (!Tools.encodePassword(enteredPassword, currentSalt).equals(encodedPassword));
		tempFrame.dispose();
		return enteredPassword;
	}
}