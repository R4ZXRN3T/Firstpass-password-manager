package org.R4ZXRN3T;

import static org.R4ZXRN3T.Tools.returnOriginalValue;
import static org.R4ZXRN3T.Tools.validateForXML;

import org.json.JSONArray;
import org.json.JSONObject;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImportExportManager {

	private static final Logger logger = new org.R4ZXRN3T.Logger(Config.LOG_PATH);

	public static void exportData(Firstpass firstpass) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.SAVE_DIALOG);
		fileChooser.setDialogTitle("Specify a file to save");
		fileChooser.setPreferredSize(new Dimension(800, 600));
		fileChooser.setLocation(0, 0);
		fileChooser.setAcceptAllFileFilterUsed(false);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setCurrentDirectory(new File(Config.getConfig(Config.ConfigKey.LAST_EXPORT_LOCATION)));
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);

		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Comma Seperated Values (*.csv)", "csv"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JavaScript Object Notation (*.json)", "json"));
		fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("eXtensible Markup Language (*.xml)", "xml"));

		File exportFile;
		String fileName;

		while (true) {
			int userSelection = fileChooser.showSaveDialog(null);

			if (userSelection != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFile() == null || fileChooser.getSelectedFile().getAbsolutePath().isEmpty())
				return;

			fileName = fileChooser.getSelectedFile().getAbsolutePath();
			if (!fileName.endsWith(".txt") && !fileName.endsWith(".csv") && !fileName.endsWith(".json") && !fileName.endsWith(".xml"))
				fileName += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];

			exportFile = new File(fileName);

			if (exportFile.exists()) {
				int option = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?", "File already exists", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) break;
			} else break;
		}

		try {
			exportFile.createNewFile();
			PrintWriter writer = new PrintWriter(exportFile);
			if (fileName.endsWith(".txt")) for (Account account : firstpass.getAccountList()) {
				writer.println(account.getProvider());
				writer.println(account.getUsername());
				writer.println(account.getPassword());
				writer.println(account.getUrl());
				writer.println(account.getComment());
			}
			else if (fileName.endsWith(".csv")) for (Account account : firstpass.getAccountList())
				writer.println(account.getProvider() + ", " + account.getUsername() + ", " + account.getPassword() + ", " + account.getUrl() + ", " + account.getComment());
			else if (fileName.endsWith(".json")) {
				JSONObject jsonObject = new JSONObject();
				for (Account account : firstpass.getAccountList())
					jsonObject.put(account.getProvider(), new String[]{account.getUsername(), account.getPassword(), account.getUrl(), account.getComment()});
				writer.println(jsonObject.toString(4));
			} else if (fileName.endsWith(".xml")) {
				writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
				writer.println("<accounts>");
				for (Account account : firstpass.getAccountList()) {
					writer.println("\t<account>");
					writer.println("\t\t<provider>" + validateForXML(account.getProvider()) + "</provider>");
					writer.println("\t\t<username>" + validateForXML(account.getUsername()) + "</username>");
					writer.println("\t\t<password>" + validateForXML(account.getPassword()) + "</password>");
					writer.println("\t\t<url>" + validateForXML(account.getUrl()) + "</url>");
					writer.println("\t\t<comment>" + validateForXML(account.getComment()) + "</comment>");
					writer.println("\t</account>");
				}
				writer.println("</accounts>");
			}
			writer.close();
			Config.setConfig(Config.ConfigKey.LAST_EXPORT_LOCATION, exportFile.getParent());
			JOptionPane.showMessageDialog(null, "Data successfully exported under:\n" + exportFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			logger.error("Error exporting data: " + e.getMessage());
			IO.println("Error exporting data: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "An error occurred while exporting the data", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	public static void importData(Firstpass firstpass) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(JFileChooser.OPEN_DIALOG);
		fileChooser.setDialogTitle("Specify a file to import");
		fileChooser.setPreferredSize(new Dimension(800, 600));
		fileChooser.setLocation(0, 0);
		fileChooser.setAcceptAllFileFilterUsed(true);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setCurrentDirectory(new File(Config.getConfig(Config.ConfigKey.LAST_IMPORT_LOCATION)));

		FileNameExtensionFilter filter = new FileNameExtensionFilter("Supported file types (*.txt, *.csv, *.json, *.xml)", "txt", "csv", "json", "xml");
		fileChooser.addChoosableFileFilter(filter);
		fileChooser.setFileFilter(filter);
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);

		int userSelection = fileChooser.showSaveDialog(null);

		if (userSelection != JFileChooser.APPROVE_OPTION) return;

		if (fileChooser.getSelectedFile() == null
				|| fileChooser.getSelectedFile().getAbsolutePath().isEmpty()
				|| !fileChooser.getSelectedFile().exists()
				|| !fileChooser.getSelectedFile().canRead()
				|| (!fileChooser.getSelectedFile().getName().endsWith("txt")
				&& !fileChooser.getSelectedFile().getName().endsWith("csv")
				&& !fileChooser.getSelectedFile().getName().endsWith("json")
				&& !fileChooser.getSelectedFile().getName().endsWith("xml"))) {
			JOptionPane.showMessageDialog(null, "Invalid file selected", "Error", JOptionPane.ERROR_MESSAGE);
			return;
		}

		JLabel message = new JLabel("What do you want to do with the imported data?");
		String[] options = {"Merge", "Replace", "Cancel"};

		int option = JOptionPane.showOptionDialog(null, message, "Import", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (option == 2) return;

		File importFile = fileChooser.getSelectedFile();
		ArrayList<Account> importedAccounts = new ArrayList<>();

		try {
			if (fileChooser.getSelectedFile().getName().endsWith("txt")) {
				Scanner fileReader = new Scanner(importFile);
				while (fileReader.hasNextLine()) {
					Account newAccount = new Account();
					if (fileReader.hasNextLine()) newAccount.setProvider(fileReader.nextLine());
					if (fileReader.hasNextLine()) newAccount.setUsername(fileReader.nextLine());
					if (fileReader.hasNextLine()) newAccount.setPassword(fileReader.nextLine());
					if (fileReader.hasNextLine()) newAccount.setUrl(fileReader.nextLine());
					if (fileReader.hasNextLine()) newAccount.setComment(fileReader.nextLine());
					importedAccounts.add(newAccount);
				}
			} else if (fileChooser.getSelectedFile().getName().endsWith("csv")) {
				Scanner fileReader = new Scanner(importFile);
				while (fileReader.hasNextLine()) {
					String[] data = fileReader.nextLine().split(", ");
					Account newAccount = new Account(data[0], data[1], data[2], data[3], data[4]);
					importedAccounts.add(newAccount);
				}
			} else if (fileChooser.getSelectedFile().getName().endsWith("json")) {
				String content = new String(java.nio.file.Files.readAllBytes(java.nio.file.Paths.get(importFile.getAbsolutePath())));
				JSONObject jsonObject = new JSONObject(content);
				for (String key : jsonObject.keySet()) {
					Account newAccount = new Account();

					JSONArray accountData = jsonObject.getJSONArray(key);

					newAccount.setProvider(key);
					newAccount.setUsername(accountData.getString(0));
					newAccount.setPassword(accountData.getString(1));
					newAccount.setUrl(accountData.getString(2));
					newAccount.setComment(accountData.getString(3));

					importedAccounts.add(newAccount);
				}
			} else if (fileChooser.getSelectedFile().getName().endsWith("xml")) {
				Scanner fileReader = new Scanner(importFile);
				while (fileReader.hasNextLine()) {
					String line = fileReader.nextLine();
					if (line.contains("<account>")) {
						Account newAccount = new Account();
						while (!line.contains("</account>")) {
							line = fileReader.nextLine();
							if (line.contains("<provider>"))
								newAccount.setProvider(returnOriginalValue(line.substring(12, line.length() - 11)));
							else if (line.contains("<username>"))
								newAccount.setUsername(returnOriginalValue(line.substring(12, line.length() - 11)));
							else if (line.contains("<password>"))
								newAccount.setPassword(returnOriginalValue(line.substring(12, line.length() - 11)));
							else if (line.contains("<url>"))
								newAccount.setUrl(returnOriginalValue(line.substring(7, line.length() - 6)));
							else if (line.contains("<comment>"))
								newAccount.setComment(returnOriginalValue(line.substring(11, line.length() - 10)));
						}
						importedAccounts.add(newAccount);
					}
				}
			}

			if (option == 0) {
				firstpass.getAccountList().addAll(importedAccounts);
			} else {
				firstpass.getAccountList().clear();
				firstpass.getAccountList().addAll(importedAccounts);
			}

			Config.setConfig(Config.ConfigKey.LAST_IMPORT_LOCATION, importFile.getParent());

			JOptionPane.showMessageDialog(null, "Data successfully imported", "Success", JOptionPane.INFORMATION_MESSAGE);
			firstpass.refreshTable();
			firstpass.setChangeMade(true);
		} catch (IOException e) {
			logger.error("Error importing data: " + e.getMessage());
			IO.println("Error importing data: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "An error occurred while importing the data", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}
}