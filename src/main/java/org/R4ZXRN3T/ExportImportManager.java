package org.R4ZXRN3T;

import org.json.JSONArray;
import org.json.JSONObject;
import org.R4ZXRN3T.interfaces.AccountService;
import org.R4ZXRN3T.interfaces.FileService;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import static org.R4ZXRN3T.Tools.returnOriginalValue;
import static org.R4ZXRN3T.Tools.validateForXML;

public class ExportImportManager {

	private final AccountService accountService;
	private final FileService fileService;

	public ExportImportManager(AccountService accountService, FileService fileService) {
		this.accountService = accountService;
		this.fileService = fileService;
	}

	private static JFileChooser setupFileChooser(int dialogType, String title, String lastLocation, boolean acceptAll, boolean saveDialog) {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setDialogType(dialogType);
		fileChooser.setDialogTitle(title);
		fileChooser.setPreferredSize(new Dimension(800, 600));
		fileChooser.setLocation(0, 0);
		fileChooser.setAcceptAllFileFilterUsed(acceptAll);
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setCurrentDirectory(new File(lastLocation));
		if (saveDialog) {
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Text File (*.txt)", "txt"));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("Comma Seperated Values (*.csv)", "csv"));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("JavaScript Object Notation (*.json)", "json"));
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("eXtensible Markup Language (*.xml)", "xml"));
		} else {
			FileNameExtensionFilter filter = new FileNameExtensionFilter("Supported file types (*.txt, *.csv, *.json, *.xml)", "txt", "csv", "json", "xml");
			fileChooser.addChoosableFileFilter(filter);
			fileChooser.setFileFilter(filter);
		}
		Action details = fileChooser.getActionMap().get("viewTypeDetails");
		details.actionPerformed(null);
		return fileChooser;
	}

	public void exportData(ArrayList<Account> accountList) {
		JFileChooser fileChooser = setupFileChooser(JFileChooser.SAVE_DIALOG, "Specify a file to save",
				Config.getConfig(Config.ConfigKey.LAST_EXPORT_LOCATION), false, true);

		File exportFile;
		String fileName;
		while (true) {
			int userSelection = fileChooser.showSaveDialog(null);
			if (userSelection != JFileChooser.APPROVE_OPTION || fileChooser.getSelectedFile() == null || fileChooser.getSelectedFile().getAbsolutePath().isEmpty())
				return;
			fileName = fileChooser.getSelectedFile().getAbsolutePath();
			if (!fileName.matches(".*\\.(txt|csv|json|xml)$")) {
				fileName += "." + ((FileNameExtensionFilter) fileChooser.getFileFilter()).getExtensions()[0];
			}
			exportFile = new File(fileName);
			if (exportFile.exists()) {
				int option = JOptionPane.showConfirmDialog(null, "File already exists. Do you want to overwrite it?", "File already exists", JOptionPane.YES_NO_OPTION);
				if (option == JOptionPane.YES_OPTION) break;
			} else break;
		}

		try {
			exportFile.createNewFile();
			PrintWriter writer = new PrintWriter(exportFile);
			String ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
			switch (ext) {
				case "txt" -> exportFields(accountList, writer, "\n", false);
				case "csv" -> exportFields(accountList, writer, ", ", true);
				case "json" -> exportJson(accountList, writer);
				case "xml" -> exportXml(accountList, writer);
				default -> throw new IllegalArgumentException("Unsupported export format: " + ext);
			}
			writer.close();
			Config.setConfig(Config.ConfigKey.LAST_EXPORT_LOCATION, exportFile.getParent());
			JOptionPane.showMessageDialog(null, "Data successfully exported under:\n" + exportFile.getAbsolutePath(), "Success", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while exporting the data", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static void exportFields(ArrayList<Account> accountList, PrintWriter writer, String sep, boolean singleLine) {
		for (Account account : accountList) {
			String[] fields = {account.getProvider(), account.getUsername(), account.getPassword(), account.getUrl(), account.getComment()};
			if (singleLine) writer.println(String.join(sep, fields));
			else for (String f : fields) writer.println(f);
		}
	}

	private static void exportJson(ArrayList<Account> accountList, PrintWriter writer) {
		JSONObject jsonObject = new JSONObject();
		for (Account account : accountList) {
			jsonObject.put(account.getProvider(), new String[]{account.getUsername(), account.getPassword(), account.getUrl(), account.getComment()});
		}
		writer.println(jsonObject.toString(4));
	}

	private static void exportXml(ArrayList<Account> accountList, PrintWriter writer) {
		writer.println("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
		writer.println("<accounts>");
		for (Account account : accountList) {
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

	public ArrayList<Account> importData(ArrayList<Account> accountList) {
		JFileChooser fileChooser = setupFileChooser(JFileChooser.OPEN_DIALOG, "Specify a file to import",
				Config.getConfig(Config.ConfigKey.LAST_IMPORT_LOCATION), true, false);

		int userSelection = fileChooser.showSaveDialog(null);
		if (userSelection != JFileChooser.APPROVE_OPTION) return null;

		File selectedFile = fileChooser.getSelectedFile();
		if (selectedFile == null || selectedFile.getAbsolutePath().isEmpty() || !selectedFile.exists() || !selectedFile.canRead()
				|| !selectedFile.getName().matches(".*\\.(txt|csv|json|xml)$")) {
			JOptionPane.showMessageDialog(null, "Invalid file selected", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		JLabel message = new JLabel("What do you want to do with the imported data?");
		String[] options = {"Merge", "Replace", "Cancel"};
		int option = JOptionPane.showOptionDialog(null, message, "Import", JOptionPane.DEFAULT_OPTION, JOptionPane.PLAIN_MESSAGE, null, options, options[0]);
		if (option == 2) return null;

		ArrayList<Account> importedAccounts;
		try {
			String importedExt = selectedFile.getName().substring(selectedFile.getName().lastIndexOf('.') + 1).toLowerCase(Locale.ROOT);
			switch (importedExt) {
				case "txt" -> importedAccounts = importFields(selectedFile, false);
				case "csv" -> importedAccounts = importFields(selectedFile, true);
				case "json" -> importedAccounts = importJson(selectedFile);
				case "xml" -> importedAccounts = importXml(selectedFile);
				default -> throw new IllegalArgumentException("Unsupported import format: " + importedExt);
			}
			accountList = (option == 0) ? mergeAccounts(accountList, importedAccounts) : importedAccounts;
			Config.setConfig(Config.ConfigKey.LAST_IMPORT_LOCATION, selectedFile.getParent());
			JOptionPane.showMessageDialog(null, "Data successfully imported", "Success", JOptionPane.INFORMATION_MESSAGE);
			// Main.refreshTable();
			// Main.changeMade = true;
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "An error occurred while importing the data", "Error", JOptionPane.ERROR_MESSAGE);
			return null;
		}
		return accountList;
	}

	private static ArrayList<Account> mergeAccounts(ArrayList<Account> original, ArrayList<Account> imported) {
		original.addAll(imported);
		return original;
	}

	private static ArrayList<Account> importFields(File importFile, boolean singleLine) throws IOException {
		ArrayList<Account> importedAccounts = new ArrayList<>();
		Scanner fileReader = new Scanner(importFile);
		while (fileReader.hasNextLine()) {
			Account newAccount;
			if (singleLine) {
				String[] data = fileReader.nextLine().split(", ");
				newAccount = new Account(data[0], data[1], data[2], data[3], data[4]);
			} else {
				newAccount = new Account();
				if (fileReader.hasNextLine()) newAccount.setProvider(fileReader.nextLine());
				if (fileReader.hasNextLine()) newAccount.setUsername(fileReader.nextLine());
				if (fileReader.hasNextLine()) newAccount.setPassword(fileReader.nextLine());
				if (fileReader.hasNextLine()) newAccount.setUrl(fileReader.nextLine());
				if (fileReader.hasNextLine()) newAccount.setComment(fileReader.nextLine());
			}
			importedAccounts.add(newAccount);
		}
		fileReader.close();
		return importedAccounts;
	}

	private static ArrayList<Account> importJson(File importFile) throws IOException {
		ArrayList<Account> importedAccounts = new ArrayList<>();
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
		return importedAccounts;
	}

	private static ArrayList<Account> importXml(File importFile) throws IOException {
		ArrayList<Account> importedAccounts = new ArrayList<>();
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
		fileReader.close();
		return importedAccounts;
	}
}