package org.R4ZXRN3T.firstpass;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

// It's not your time yet

class Updater {
	private static final String REPO_URL = "https://api.github.com/repos/R4ZXRN3T/Firstpass-password-manager/releases/latest";
	private static final String DOWNLOAD_URL = "https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/download/";
	private static final Logger logger = new org.R4ZXRN3T.firstpass.Logger(Config.LOG_PATH);
	private static boolean portableVersion = false;
	private static Firstpass firstpassInstance = null;

	public static void initialize(Firstpass firstpass) {
		firstpassInstance = firstpass;
		portableVersion = Config.isPortableVersion();
	}

	private static String getFileName() {
		return portableVersion ? "Firstpass_portable.jar" : "Firstpass_setup.exe";
	}


	public static String checkVersion(boolean showError) {

		String latestVersion = null;

		try {
			URL url = new URI(REPO_URL).toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

			if (conn.getResponseCode() != 200) {
				JOptionPane.showMessageDialog(null, "Github Version Not Found", "Error", JOptionPane.ERROR_MESSAGE);
				logger.error("Failed to check for updates: HTTP error code: " + conn.getResponseCode());
				throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
			}

			BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
			StringBuilder sb = new StringBuilder();
			String output;
			while ((output = br.readLine()) != null) {
				sb.append(output);
			}
			conn.disconnect();

			JSONObject jsonResponse = new JSONObject(sb.toString());
			latestVersion = jsonResponse.getString("tag_name");
		} catch (Exception e) {
			logger.error("Error checking for updates: " + e.getMessage());
			System.out.println("Error checking for updates: " + e.getMessage());
			if (showError)
				JOptionPane.showMessageDialog(null, "<html>Update check failed.<br>Please check your internet connection</html>", "Error", JOptionPane.ERROR_MESSAGE);
		}

		return latestVersion;
	}

	public static void update() {
		JLabel versionAvailableLabel = new JLabel("<html> <font color=\"#00b3ff\">Version " + checkVersion(true) + " is available.<br></font> Are you sure you want to update Firstpass?</html>");
		int option = JOptionPane.showConfirmDialog(firstpassInstance.getFrame(), versionAvailableLabel, "Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
		if (option != JOptionPane.YES_OPTION) return;

		SwingUtilities.invokeLater(() -> {
			JFrame updateFrame = new JFrame("Update");
			updateFrame.setSize(300, 100);
			updateFrame.setLocationRelativeTo(null);
			updateFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
			updateFrame.setResizable(false);
			updateFrame.setLayout(new FlowLayout());

			JLabel progressLabel = new JLabel("00%");

			JProgressBar progressBar = new JProgressBar(0, 100);
			progressBar.setBorder(BorderFactory.createEmptyBorder(8, 8, 8, 8));
			progressBar.setPreferredSize(new Dimension(240, 30));
			progressBar.setIndeterminate(false);
			progressBar.setValue(0);

			updateFrame.add(new JLabel("Downloading newest version..."));
			updateFrame.add(progressBar);
			updateFrame.add(progressLabel);
			updateFrame.setVisible(true);

			new Thread(() -> {
				try {
					String newestVersion = checkVersion(true);

					Path filePath = Config.getConfigFilePath().toAbsolutePath().getParent();
					if (!filePath.toFile().exists()) filePath.toFile().mkdirs();

					String link = DOWNLOAD_URL + newestVersion + "/" + getFileName();
					String fileName = portableVersion ? filePath + "/Firstpass_portable.jar.tmp" : filePath + "/Firstpass_setup.exe.tmp";

					URL url = new URI(link).toURL();
					HttpURLConnection http = (HttpURLConnection) url.openConnection();

					Map<String, List<String>> header = http.getHeaderFields();
					while (isRedirected(header)) {
						link = header.get("Location").getFirst();
						url = new URI(link).toURL();
						http = (HttpURLConnection) url.openConnection();
						header = http.getHeaderFields();
					}

					int fileSize = http.getContentLength();
					InputStream input = http.getInputStream();
					byte[] buffer = new byte[4096];
					int bytesRead;
					int totalBytesRead = 0;
					OutputStream output = Files.newOutputStream(new File(fileName).toPath());

					while ((bytesRead = input.read(buffer)) != -1) {
						output.write(buffer, 0, bytesRead);
						totalBytesRead += bytesRead;
						int progress = (int) (((double) totalBytesRead / fileSize) * 100);
						SwingUtilities.invokeLater(() -> progressBar.setValue(progress));
						SwingUtilities.invokeLater(() -> progressLabel.setText(progress < 10 ? progress + "0%" : progress + "%"));
					}

					output.close();
					input.close();
					if (firstpassInstance != null) firstpassInstance.save();
					updateFrame.dispose();
					installUpdate();
				} catch (IOException | URISyntaxException e) {
					throw new RuntimeException(e);
				}
			}).start();
		});
	}

	private static void installUpdate() {
		if (!portableVersion) {
			Path filePath = Config.getConfigFilePath().toAbsolutePath().getParent();
			if (!filePath.toFile().exists()) filePath.toFile().mkdirs();
			File tmp = new File(filePath + "\\Firstpass_setup.exe.tmp");
			File current = new File(filePath + "\\Firstpass_setup.exe");
			if (current.exists()) current.delete();
			tmp.renameTo(current);
			try {
				new ProcessBuilder("cmd", "/c", "start", filePath + "\\Firstpass_setup.exe").start();
				System.exit(0);
			} catch (IOException e) {
				logger.error("Failed to start installer: " + e.getMessage());
				System.out.println("Failed to start installer: " + e.getMessage());
				JOptionPane.showMessageDialog(null, "Failed to install update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}

		String tempFileName = "Firstpass_portable.jar.tmp";
		String currentFileName = "Firstpass_portable.jar";
		try {
			String script = System.getProperty("os.name").toLowerCase().contains("win") ? "rename.bat" : "rename.sh";
			PrintWriter writer = new PrintWriter(script);

			if (System.getProperty("os.name").toLowerCase().contains("win")) {
				writer.println("@echo off");
				writer.println("timeout /t 1 /nobreak");
				writer.println("del " + currentFileName);
				writer.println("rename " + tempFileName + " " + currentFileName);
				writer.println("start " + currentFileName);
				writer.println("exit");
			} else {
				writer.println("#!/bin/bash");
				writer.println("sleep 1");
				writer.println("rm " + currentFileName);
				writer.println("mv " + tempFileName + " " + currentFileName);
				writer.println("chmod +x " + currentFileName);
				writer.println("java -jar " + currentFileName);
			}
			writer.close();
			if (System.getProperty("os.name").toLowerCase().contains("win"))
				new ProcessBuilder("cmd", "/c", "start", script).start();
			else {
				new File(script).setExecutable(true);
				new ProcessBuilder("./" + script).start();
			}
			System.exit(0);
		} catch (IOException e) {
			logger.error("Failed to schedule update: " + e.getMessage());
			System.out.println("Failed to schedule update: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Failed to schedule update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static boolean isRedirected(Map<String, List<String>> header) {
		for (String hv : header.get(null)) if (hv.contains(" 301 ") || hv.contains(" 302 ")) return true;
		return false;
	}
}