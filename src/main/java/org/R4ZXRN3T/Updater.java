package org.R4ZXRN3T;

import org.json.JSONObject;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.util.List;
import java.util.Map;

// It's not your time yet

class Updater {
	private static final String REPO_URL = "https://api.github.com/repos/R4ZXRN3T/Firstpass-password-manager/releases/latest";
	private static final String DOWNLOAD_URL = "https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/download/";
	private static final String FILE_NAME = Main.portableVersion ? "Firstpass_portable.jar" : "Firstpass_setup.msi";


	public static String checkVersion() {

		String latestVersion = null;

		try {
			URL url = new URL(REPO_URL);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

			if (conn.getResponseCode() != 200) {
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
			e.printStackTrace();
		}

		return (latestVersion);
	}

	public static void update() {
		int option = JOptionPane.showConfirmDialog(null, "Are you sure you want to update Firstpass?", "Update", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
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
					String newestVersion = checkVersion();

					String link = DOWNLOAD_URL + newestVersion + "/" + FILE_NAME;
					String fileName = Main.portableVersion ? "Firstpass_portable.jar.tmp" : "Firstpass_setup.msi.tmp";

					URL url = new URL(link);
					HttpURLConnection http = (HttpURLConnection) url.openConnection();

					Map<String, List<String>> header = http.getHeaderFields();
					while (isRedirected(header)) {
						link = header.get("Location").get(0);
						url = new URL(link);
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
					Main.save();
					updateFrame.dispose();
					installUpdate();
				} catch (IOException e) {
					throw new RuntimeException(e);
				}
			}).start();
		});
	}

	private static void installUpdate() {
		if (!Main.portableVersion) {
			File tmp = new File("Firstpass_setup.msi.tmp");
			File current = new File("Firstpass_setup.msi");
			if (current.exists()) {
				current.delete();
			}
			tmp.renameTo(current);
			try {
				Runtime.getRuntime().exec("cmd /c start Firstpass_setup.msi");
				System.exit(0);
			} catch (IOException e) {
				e.printStackTrace();
				JOptionPane.showMessageDialog(null, "Failed to install update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
			}
		}
		String tempFileName = "Firstpass_portable.jar.tmp";
		String currentFileName = "Firstpass_portable.jar";
		try {
			String script = System.getProperty("os.name").toLowerCase().contains("win") ? "rename.bat" : "rename.sh";
			PrintWriter writer = new PrintWriter(script, "UTF-8");

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
				writer.println("nohup ./" + currentFileName + " &");
			}
			writer.close();
			Runtime.getRuntime().exec("cmd /c start " + script);
			System.exit(0);
		} catch (IOException e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Failed to schedule update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	private static boolean isRedirected(Map<String, List<String>> header) {

		for (String hv : header.get(null)) {
			if (hv.contains(" 301 ") || hv.contains(" 302 ")) {
				return true;
			}
		}

		return false;
	}
}