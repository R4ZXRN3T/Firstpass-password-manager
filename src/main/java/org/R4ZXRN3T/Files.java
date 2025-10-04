package org.R4ZXRN3T;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import org.R4ZXRN3T.interfaces.FileService;

public class FileServiceImpl implements FileService {

	// retrieve Accounts ArrayList from accounts.txt. Only called on program launch
	@Override
	public ArrayList<Account> getAccounts(String decryptionKey) {

		ArrayList<Account> accountsArr = new ArrayList<>();
		JProgressBar progressBar = new JProgressBar();
		JLabel message = new JLabel("Loading and decrypting accounts...");
		progressBar.setPreferredSize(new Dimension(240, 20));
		progressBar.setStringPainted(true);
		JDialog frame = new JDialog(Main.frame, "", true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent windowEvent) {
				System.exit(0);
			}
		});

		frame.setSize(300, 100);
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		frame.add(message);
		frame.add(progressBar);
		frame.setLocationRelativeTo(null);
		frame.requestFocus();

		new Thread(() -> frame.setVisible(true)).start();

		try {
			System.out.println("\nFinding file...");
			File accountsFile = new File("accounts.txt");
			accountsFile.createNewFile();
			Scanner readAcc = new Scanner(accountsFile);
			System.out.println("File found.");

			// Calculate the total number of accounts
			int totalLines = 0;
			while (readAcc.hasNextLine()) {
				readAcc.nextLine();
				totalLines++;
			}
			readAcc.close();

			int totalAccounts = totalLines / 5;
			progressBar.setMaximum(totalAccounts);

			readAcc = new Scanner(accountsFile);
			System.out.println("\nReading and decrypting accounts...");
			int currentAccount = 0;

			while (readAcc.hasNextLine()) {
				Account newAccount = new Account();

				if (readAcc.hasNextLine()) newAccount.setProvider(readAcc.nextLine());
				if (readAcc.hasNextLine()) newAccount.setUsername(readAcc.nextLine());
				if (readAcc.hasNextLine()) newAccount.setPassword(readAcc.nextLine());
				if (readAcc.hasNextLine()) newAccount.setUrl(readAcc.nextLine());
				if (readAcc.hasNextLine()) newAccount.setComment(readAcc.nextLine());
				else break;

				newAccount.decrypt(decryptionKey);
				accountsArr.add(newAccount);

				currentAccount++;
				progressBar.setValue(currentAccount);
			}
			System.out.println("Accounts successfully fetched and decrypted.");
			readAcc.close();
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("Error reading accounts.");
			accountsArr = null;
		} finally {
			frame.dispose();
		}
		return accountsArr;
	}

	// save accounts to accounts.txt. Called only on save
	@Override
	public void saveAccounts(ArrayList<Account> accountsArr, String encryptionKey) {

		JProgressBar progressBar = new JProgressBar();
		JLabel message = new JLabel("Encrypting and saving accounts...");
		progressBar.setPreferredSize(new Dimension(240, 20));
		progressBar.setStringPainted(true);

		// Create the dialog and set it to be a child of the main frame
		JDialog frame = new JDialog(Main.frame, "Saving Accounts", true);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setSize(300, 100);
		frame.setResizable(false);
		frame.setLayout(new FlowLayout());
		frame.add(message);
		frame.add(progressBar);
		frame.setLocationRelativeTo(null);

		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				try (FileWriter writer = new FileWriter("accounts.txt")) {
					progressBar.setMaximum(accountsArr.size());

					int currentAccount = 0;
					for (Account account : accountsArr) {
						account.encrypt(encryptionKey);
						writer.write(account.getProvider() + "\n");
						writer.write(account.getUsername() + "\n");
						writer.write(account.getPassword() + "\n");
						writer.write(account.getUrl() + "\n");
						writer.write(account.getComment() + "\n");

						currentAccount++;
						publish(currentAccount);
					}
				}
				return null;
			}

			@Override
			protected void process(List<Integer> chunks) {
				progressBar.setValue(chunks.get(chunks.size() - 1));
			}

			@Override
			protected void done() {
				frame.dispose();
			}
		};

		worker.execute(); // Start background task

		frame.setVisible(true);

		// Block execution until the worker is done
		try {
			worker.get(); // Waits for the background task to complete
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}