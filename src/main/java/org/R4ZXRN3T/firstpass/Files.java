package org.R4ZXRN3T.firstpass;

import org.jasypt.util.text.StrongTextEncryptor;

import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

// all file shit in here
class Files {

	public static final String ACCOUNTS_PATH = String.valueOf(getAccountFilePath());
	private static final Logger logger = new org.R4ZXRN3T.firstpass.Logger(Config.LOG_PATH);
	private static final int ACCOUNT_FIELDS_COUNT = 5;

	// retrieve Accounts ArrayList from accounts.txt. Only called on program launch
	public static ArrayList<Account> getAccounts(String decryptionKey) {
		ArrayList<Account> accountsArr = new ArrayList<>();
		JDialog frame = createProgressDialog("Loading and decrypting accounts...", "Decrypting accounts...");
		JProgressBar progressBar = (JProgressBar) frame.getContentPane().getComponent(1);

		SwingWorker<ArrayList<Account>, Integer> worker = new SwingWorker<>() {
			@Override
			protected ArrayList<Account> doInBackground() throws Exception {
				File accountsFile = new File(new File(ACCOUNTS_PATH).getAbsolutePath());
				Path parentDir = accountsFile.toPath().getParent();
				if (parentDir != null && !parentDir.toFile().exists()) parentDir.toFile().mkdirs();
				if (!accountsFile.exists()) accountsFile.createNewFile();

				List<String> fileContent = java.nio.file.Files.readAllLines(accountsFile.toPath());
				if (fileContent.size() < ACCOUNT_FIELDS_COUNT) {
					if (!fileContent.isEmpty()) {
						logger.error("Account file contains incomplete account data (less than " + ACCOUNT_FIELDS_COUNT + " lines). Skipping.");
					}
					return new ArrayList<>();
				}
				if (decryptionKey.isEmpty()) return stringListToAccountList(fileContent);

				int totalAccounts = fileContent.size() / ACCOUNT_FIELDS_COUNT;
				progressBar.setMaximum(totalAccounts);

				StrongTextEncryptor decryptor = new StrongTextEncryptor();
				decryptor.setPassword(decryptionKey);
				ArrayList<Account> result = new ArrayList<>();
				for (int i = 0; i <= fileContent.size() - ACCOUNT_FIELDS_COUNT; i += ACCOUNT_FIELDS_COUNT) {
					result.add(new Account(
							decryptor.decrypt(fileContent.get(i)),
							decryptor.decrypt(fileContent.get(i + 1)),
							decryptor.decrypt(fileContent.get(i + 2)),
							decryptor.decrypt(fileContent.get(i + 3)),
							decryptor.decrypt(fileContent.get(i + 4))
					));
					publish(i / ACCOUNT_FIELDS_COUNT + 1);
				}
				return result;
			}

			@Override
			protected void process(List<Integer> chunks) {
				progressBar.setValue(chunks.getLast());
			}

			@Override
			protected void done() {
				try {
					ArrayList<Account> result = get();
					accountsArr.addAll(result);
				} catch (Exception e) {
					logger.error("Error reading accounts: " + e.getMessage());
					System.out.println("Error reading accounts: " + e.getMessage());
				}
				frame.dispose();
			}
		};

		worker.execute();
		frame.setVisible(true);
		return accountsArr;
	}

	// save accounts to accounts.txt. Called only on save
	public static void saveAccounts(ArrayList<Account> accountsArr, String encryptionKey) {
		JDialog frame = createProgressDialog("Saving Accounts", "Encrypting and saving accounts...");
		JProgressBar progressBar = (JProgressBar) frame.getContentPane().getComponent(1);

		SwingWorker<Void, Integer> worker = new SwingWorker<>() {
			@Override
			protected Void doInBackground() throws Exception {
				File accountsFile = new File(new File(ACCOUNTS_PATH).getAbsolutePath());
				Path parentDir = accountsFile.toPath().getParent();
				if (parentDir != null && !parentDir.toFile().exists()) parentDir.toFile().mkdirs();
				if (!accountsFile.exists()) accountsFile.createNewFile();

				try (FileWriter writer = new FileWriter(accountsFile, false)) {
					if (encryptionKey.isEmpty()) {
						writeListUnencrypted(writer, accountsArr);
						return null;
					}

					progressBar.setMaximum(accountsArr.size());
					StrongTextEncryptor encryptor = new StrongTextEncryptor();
					encryptor.setPassword(encryptionKey);

					int currentAccount = 0;
					for (Account account : accountsArr) {
						writer.write(encryptor.encrypt(account.getProvider()) + "\n");
						writer.write(encryptor.encrypt(account.getUsername()) + "\n");
						writer.write(encryptor.encrypt(account.getPassword()) + "\n");
						writer.write(encryptor.encrypt(account.getUrl()) + "\n");
						writer.write(encryptor.encrypt(account.getComment()) + "\n");
						currentAccount++;
						publish(currentAccount);
					}
				}
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				progressBar.setValue(chunks.getLast());
			}

			@Override
			protected void done() {
				frame.dispose();
			}
		};

		worker.execute();
		frame.setVisible(true);
		try {
			worker.get();
		} catch (Exception e) {
			logger.error("Error saving accounts: " + e.getMessage());
			System.out.println("Error saving accounts: " + e.getMessage());
		}
	}

	private static ArrayList<Account> stringListToAccountList(List<String> inputList) {
		ArrayList<Account> accounts = new ArrayList<>();
		int fullAccounts = inputList.size() / ACCOUNT_FIELDS_COUNT;
		for (int i = 0; i < fullAccounts; i++) {
			int idx = i * ACCOUNT_FIELDS_COUNT;
			accounts.add(new Account(
					inputList.get(idx),
					inputList.get(idx + 1),
					inputList.get(idx + 2),
					inputList.get(idx + 3),
					inputList.get(idx + 4)
			));
		}
		if (inputList.size() % ACCOUNT_FIELDS_COUNT != 0) {
			logger.error("Account file contains leftover lines (" + (inputList.size() % ACCOUNT_FIELDS_COUNT) + "). Ignoring incomplete account data at end of file.");
		}
		return accounts;
	}

	private static void writeListUnencrypted(FileWriter writer, ArrayList<Account> accounts) {
		try (writer) {
			for (Account account : accounts) {
				writer.write(account.getProvider() + "\n");
				writer.write(account.getUsername() + "\n");
				writer.write(account.getPassword() + "\n");
				writer.write(account.getUrl() + "\n");
				writer.write(account.getComment() + "\n");
			}
		} catch (IOException e) {
			logger.error("Error saving accounts: " + e.getMessage());
			System.out.println("Error saving accounts: " + e.getMessage());
		}
	}

	public static Path getAccountFilePath() {
		String os = System.getProperty("os.name").toLowerCase();
		String fileName = "Firstpass/accounts.txt";
		if (Config.isPortableVersion()) {
			return Paths.get("accounts.txt");
		} else if (os.contains("win")) {
			String appData = System.getenv("APPDATA");
			return Paths.get(appData, fileName);
		} else if (os.contains("mac")) {
			String userHome = System.getProperty("user.home");
			return Paths.get(userHome, "Library", "Application Support", fileName);
		} else { // Linux and others
			String userHome = System.getProperty("user.home");
			return Paths.get(userHome, ".config", fileName);
		}
	}

	private static JDialog createProgressDialog(String title, String message) {
		JDialog dialog = new JDialog((JFrame) null, title, true);
		dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		dialog.setSize(300, 100);
		dialog.setResizable(false);
		dialog.setLayout(new FlowLayout());

		JLabel label = new JLabel(message);
		dialog.add(label);

		JProgressBar progressBar = new JProgressBar(0, 100);
		progressBar.setPreferredSize(new Dimension(240, 20));
		progressBar.setStringPainted(true);
		dialog.add(progressBar);

		dialog.setLocationRelativeTo(null);

		return dialog;
	}
}