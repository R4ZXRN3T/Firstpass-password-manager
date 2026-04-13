package org.R4ZXRN3T.firstpass;

import org.json.JSONObject;

import java.awt.*;
import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;
import java.util.jar.Manifest;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.swing.*;

public class Updater {
	private static final String REPO_URL = "https://api.github.com/repos/R4ZXRN3T/Firstpass-password-manager/releases/latest";
	private static final Logger logger = new org.R4ZXRN3T.firstpass.Logger(Config.LOG_PATH);
	private static Firstpass firstpassInstance = null;
	private static String latestVersion = "";

	public static void initialize(Firstpass firstpass) {
		firstpassInstance = firstpass;
	}

	private static String normalizeOsName() {
		String osName = System.getProperty("os.name").toLowerCase(Locale.ROOT);
		if (osName.contains("win")) return "Windows";
		else if (osName.contains("mac")) return "MacOS";
		else if (osName.contains("nux") || osName.contains("nix")) return "Linux";
		else return osName.replaceAll("\\s+", "");
	}

	private static String normalizeArch() {
		String arch = System.getProperty("os.arch").toLowerCase(Locale.ROOT);
		if (arch.equals("amd64") || arch.equals("x86_64")) return "x86_64";
		else if (arch.equals("aarch64") || arch.equals("arm64")) return "aarch64";
		else return arch.replaceAll("\\s+", "");
	}

	private static String getDownloadFileName() {
		return switch (getDistributionType()) {
			case INSTALLER -> "Firstpass-" + latestVersion + "-setup.exe";
			case PACKAGED -> "Firstpass-" + latestVersion + "-" + normalizeOsName() + "-" + normalizeArch() + ".zip";
			case PORTABLE -> "Firstpass-" + latestVersion + "-portable.jar";
			default -> "";
		};
	}

	public static String checkVersion(boolean showError, boolean forceCheck) {
		if (!forceCheck && !latestVersion.isEmpty()) return latestVersion;

		if (Config.getDistributionType() == DistributionType.UNKNOWN) {
			if (showError)
				JOptionPane.showMessageDialog(null, "Unknown distribution type. Please check for updates manually or perform them with your package manager.", "Error", JOptionPane.ERROR_MESSAGE);
			return "";
		}

		String remoteVersion = null;

		try {
			URL url = new URI(REPO_URL).toURL();
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("Accept", "application/vnd.github+json");

			if (conn.getResponseCode() != 200) {
				if (showError)
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
			remoteVersion = jsonResponse.getString("tag_name");
		} catch (Exception e) {
			logger.error("Error checking for updates: " + e.getMessage());
			System.out.println("Error checking for updates: " + e.getMessage());
			if (showError)
				JOptionPane.showMessageDialog(null, "<html>Update check failed.<br>Please check your internet connection</html>", "Error", JOptionPane.ERROR_MESSAGE);
		}
		latestVersion = remoteVersion;
		return remoteVersion;
	}

	private static void downloadUpdate(JFrame updateFrame, JProgressBar progressBar, JLabel progressLabel) {
		String newestVersion = checkVersion(true, true);

		Path filePath = AccountLoader.getAccountFilePath().toAbsolutePath().getParent();
		if (!filePath.toFile().exists()) filePath.toFile().mkdirs();

		String desiredAssetName = getDownloadFileName();
		Path tmpFile = filePath.resolve(desiredAssetName + ".tmp");

		try {
			// Fetch release JSON and assets list
			org.json.JSONObject releaseJson = getReleaseJson(newestVersion);
			if (releaseJson == null) {
				throw new IOException("Failed to fetch release information for " + newestVersion);
			}
			org.json.JSONArray assets = releaseJson.getJSONArray("assets");

			// Try to find the primary asset; fallback to legacy names if necessary
			org.json.JSONObject asset = findAssetForDownload(assets, desiredAssetName);
			if (asset == null) throw new IOException("No suitable release asset found for " + desiredAssetName);

			String downloadUrl = asset.getString("browser_download_url");
			long contentLength = asset.optLong("size", -1);

			// Download via buffered stream with larger buffer and try-with-resources
			URL durl = new URI(downloadUrl).toURL();
			HttpURLConnection http = (HttpURLConnection) durl.openConnection();
			http.setInstanceFollowRedirects(true);
			http.setRequestProperty("Accept", "application/octet-stream");
			http.setConnectTimeout(15000);
			http.setReadTimeout(30000);

			try (InputStream is = new BufferedInputStream(http.getInputStream()); OutputStream os = Files.newOutputStream(tmpFile)) {
				byte[] buffer = new byte[64 * 1024]; // 64 KiB buffer
				int read;
				long total = 0;
				if (contentLength > 0) {
					SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(false));
				} else {
					SwingUtilities.invokeLater(() -> progressBar.setIndeterminate(true));
				}
				while ((read = is.read(buffer)) != -1) {
					os.write(buffer, 0, read);
					total += read;
					if (contentLength > 0) {
						int prog = (int) (((double) total / contentLength) * 100);
						SwingUtilities.invokeLater(() -> progressBar.setValue(Math.min(100, prog)));
						SwingUtilities.invokeLater(() -> progressLabel.setText((prog < 10 ? prog + "0%" : prog + "%")));
					}
				}
			}

			// Get checksum directly from GitHub API (digest field)
			String expectedChecksum = null;

			if (asset.has("digest") && !asset.isNull("digest")) {
				String digest = asset.getString("digest"); // format: "sha256:abc123..."
				if (digest.startsWith("sha256:")) {
					expectedChecksum = digest.substring("sha256:".length());
				} else {
					logger.error("Unsupported digest algorithm: " + digest);
				}
			}

			if (expectedChecksum == null || expectedChecksum.isEmpty()) {
				logger.error("No SHA-256 digest available for asset " + asset.getString("name"));
				JOptionPane.showMessageDialog(updateFrame,
						"Failed to verify update: no checksum provided by GitHub.",
						"Error",
						JOptionPane.ERROR_MESSAGE);
				Files.deleteIfExists(tmpFile);
				return;
			}

			String actualChecksum = computeSha256(tmpFile);
			if (!expectedChecksum.equalsIgnoreCase(actualChecksum)) {
				logger.error("Checksum mismatch. expected=" + expectedChecksum + " actual=" + actualChecksum);
				JOptionPane.showMessageDialog(updateFrame, "Downloaded update file is corrupt (checksum mismatch). Update aborted.", "Error", JOptionPane.ERROR_MESSAGE);
				Files.deleteIfExists(tmpFile);
				return;
			}

			if (firstpassInstance != null) firstpassInstance.save();
			updateFrame.dispose();
			installUpdate();
		} catch (IOException | URISyntaxException | NoSuchAlgorithmException e) {
			logger.error("Error while downloading or verifying update: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Update failed: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
			try {
				Files.deleteIfExists(tmpFile);
			} catch (IOException ignored) {
			}
		}
	}

	private static org.json.JSONObject getReleaseJson(String tag) throws IOException, URISyntaxException {
		String apiUrl = "https://api.github.com/repos/R4ZXRN3T/Firstpass-password-manager/releases/tags/" + URLEncoder.encode(tag, StandardCharsets.UTF_8);
		URL url = new URI(apiUrl).toURL();
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/vnd.github.v3+json");
		conn.setConnectTimeout(15000);
		conn.setReadTimeout(15000);
		if (conn.getResponseCode() != 200) {
			logger.error("Failed to fetch release assets: HTTP " + conn.getResponseCode());
			return null;
		}
		try (BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()))) {
			StringBuilder sb = new StringBuilder();
			String line;
			while ((line = br.readLine()) != null) sb.append(line);
			return new org.json.JSONObject(sb.toString());
		} finally {
			conn.disconnect();
		}
	}

	private static org.json.JSONObject findAssetForDownload(org.json.JSONArray assets, String desiredName) {
		String lname = desiredName.toLowerCase(Locale.ROOT);
		for (int i = 0; i < assets.length(); i++) {
			org.json.JSONObject a = assets.getJSONObject(i);
			String name = a.getString("name");
			String nlow = name.toLowerCase(Locale.ROOT);
			if (nlow.equals(lname) || nlow.endsWith(lname)) {
				return a;
			}
		}
		return null;
	}

	private static String computeSha256(Path file) throws IOException, NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		try (InputStream is = Files.newInputStream(file)) {
			byte[] buffer = new byte[8192];
			int read;
			while ((read = is.read(buffer)) != -1) md.update(buffer, 0, read);
		}
		return bytesToHex(md.digest());
	}

	private static String bytesToHex(byte[] bytes) {
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (byte b : bytes) sb.append(String.format("%02x", b & 0xff));
		return sb.toString();
	}

	public static void update() {
		JLabel versionAvailableLabel = new JLabel("<html> <font color=\"#00b3ff\">Version " + checkVersion(true, false) + " is available.<br></font> Are you sure you want to update Firstpass?</html>");
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
				// Centralize download + verification in downloadUpdate to avoid code duplication
				downloadUpdate(updateFrame, progressBar, progressLabel);
			}).start();
			updateFrame.dispose();
		});
	}

	private static void installUpdate() {
		// Handle different distribution types separately. Installer behavior remains the same
		// Portable remains largely unchanged (uses a small script to swap the jar)
		// Packaged: the release is a zip archive; extract it to a temporary folder and schedule a swap
		DistributionType dist = getDistributionType();
		Path filePath = AccountLoader.getAccountFilePath().toAbsolutePath().getParent();
		if (!filePath.toFile().exists()) filePath.toFile().mkdirs();

		try {
			if (dist == DistributionType.INSTALLER) {
				File tmp = new File(filePath + "\\" + getDownloadFileName() + ".tmp");
				File current = new File(filePath + "\\" + getDownloadFileName());
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
				return;
			} else if (dist == DistributionType.PORTABLE) {
				// Keep current portable behavior, but try to use the actual downloaded temp name
				String tempFileName = getDownloadFileName() + ".tmp";
				String currentFileName = getDownloadFileName();
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
						writer.println("#!/usr/bin/env bash");
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
					logger.error("Failed to schedule portable update: " + e.getMessage());
					JOptionPane.showMessageDialog(null, "Failed to schedule update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
				}
				return;
			} else if (dist == DistributionType.PACKAGED) {
				// For packaged releases we expect a zip archive. Extract it to a temp folder and schedule a swap
				String zipName = getDownloadFileName();
				Path tmpZip = filePath.resolve(zipName + ".tmp");
				if (!Files.exists(tmpZip)) {
					JOptionPane.showMessageDialog(null, "Update package not found.", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}

				// Determine installation directory (parent of running code location)
				Path installDir;
				try {
					installDir = Path.of(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).toAbsolutePath().getParent().getParent();
				} catch (Exception e) {
					installDir = filePath; // fallback
				}

				Path extractedDir = filePath.resolve("firstpass_update_" + System.currentTimeMillis());
				Files.createDirectories(extractedDir);
				// unzip
				try (InputStream fis = Files.newInputStream(tmpZip);
				     ZipInputStream zis = new ZipInputStream(fis)) {
					ZipEntry entry;
					while ((entry = zis.getNextEntry()) != null) {
						Path out = extractedDir.resolve(entry.getName());
						if (entry.isDirectory()) {
							Files.createDirectories(out);
						} else {
							Files.createDirectories(out.getParent());
							Files.copy(zis, out, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
						}
						zis.closeEntry();
					}
				}

				// Find current running jar name to restart later
				String currentJarName = "";
				try {
					currentJarName = Path.of(Updater.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getFileName().toString();
				} catch (Exception ignored) {
				}

				// Create a platform-specific script that will mirror extracted content into the install dir and start the app
				String scriptName = System.getProperty("os.name").toLowerCase().contains("win") ? "rename.bat" : "rename.sh";
				try (PrintWriter writer = new PrintWriter(scriptName)) {
					if (System.getProperty("os.name").toLowerCase().contains("win")) {
						writer.println("@echo off");
						writer.println("timeout /t 1 /nobreak >nul");
						// Use robocopy to mirror extracted content into installation directory
						writer.println("robocopy \"" + extractedDir.toString() + "\" \"" + installDir.toString() + "\" /mir");
						if (!currentJarName.isEmpty())
							writer.println("start java -jar \"" + installDir.resolve(currentJarName).toString() + "\"");
						writer.println("rd /s /q \"" + extractedDir.toString() + "\"");
						writer.println("exit");
					} else {
						writer.println("#!/usr/bin/env bash");
						writer.println("sleep 1");
						writer.println("rsync -a --delete \"" + extractedDir.toString() + "/\" \"" + installDir.toString() + "/\"");
						if (!currentJarName.isEmpty())
							writer.println("java -jar \"" + installDir.resolve(currentJarName).toString() + "\" &");
						writer.println("rm -rf \"" + extractedDir.toString() + "\"");
					}
				}

				if (System.getProperty("os.name").toLowerCase().contains("win")) {
					new ProcessBuilder("cmd", "/c", "start", scriptName).start();
				} else {
					new File(scriptName).setExecutable(true);
					new ProcessBuilder("./" + scriptName).start();
				}
				System.exit(0);
			}
		} catch (IOException e) {
			logger.error("Failed to schedule update: " + e.getMessage());
			System.out.println("Failed to schedule update: " + e.getMessage());
			JOptionPane.showMessageDialog(null, "Failed to schedule update.\nPlease contact the developer if this issue persists", "Error", JOptionPane.ERROR_MESSAGE);
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
				writer.println("#!/usr/bin/env bash");
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

	public static DistributionType getDistributionType() {
		// Try to read the application's manifest from the classpath/JAR and parse
		// the custom 'Distribution-Type' attribute which is set at build time.
		try (InputStream is = Updater.class.getResourceAsStream("/META-INF/MANIFEST.MF")) {
			if (is == null) {
				return DistributionType.UNKNOWN;
			}
			Manifest mf = new Manifest(is);
			String dist = mf.getMainAttributes().getValue("Distribution-Type");
			if (dist == null) return DistributionType.UNKNOWN;
			return switch (dist.toLowerCase(Locale.ROOT).trim()) {
				case "portable" -> DistributionType.PORTABLE;
				case "installer" -> DistributionType.INSTALLER;
				case "packaged" -> DistributionType.PACKAGED;
				default -> DistributionType.UNKNOWN;
			};
		} catch (IOException e) {
			logger.error("Failed to read manifest: " + e.getMessage());
			return DistributionType.UNKNOWN;
		}
	}

	public enum DistributionType {
		PORTABLE,
		PACKAGED,
		INSTALLER,
		UNKNOWN
	}
}