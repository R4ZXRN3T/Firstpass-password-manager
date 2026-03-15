package org.R4ZXRN3T.firstpass;

import org.json.JSONArray;
import org.json.JSONObject;

import javax.crypto.AEADBadTagException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Base64;

import static org.R4ZXRN3T.firstpass.Config.isPortableVersion;

public class AccountLoader {
	public static final String ACCOUNTS_PATH = String.valueOf(getAccountFilePath());
	private static final Logger logger = new Logger(Config.LOG_PATH);

	private static final int VAULT_VERSION = 1;
	private static final int PBKDF2_ITERATIONS = 210_000;
	private static final int PBKDF2_KEY_LENGTH = 256;
	private static final int SALT_LENGTH_BYTES = 16;
	private static final int IV_LENGTH_BYTES = 12;
	private static final int GCM_TAG_LENGTH_BITS = 128;
	private static final SecureRandom RNG = new SecureRandom();

	/**
	 * Decrypts the {@code accounts.vault} file with a master password using AES/GCM.
	 *
	 * @param masterPassword The user password, with which the accounts get decrypted.
	 * @return An {@link ArrayList} of {@link Account}, containing all account data from the vault.
	 */
	public static ArrayList<Account> getAccounts(String masterPassword) {
		File accountFile = getAccountFilePath().toFile().getAbsoluteFile();
		Path accountPath = accountFile.toPath();
		ensureParentDirectoryExists(accountPath);
		if (!accountFile.exists() || accountFile.length() == 0) return new ArrayList<>();

		try {
			String vaultJson = java.nio.file.Files.readString(accountPath, StandardCharsets.UTF_8).trim();
			if (vaultJson.isEmpty()) return new ArrayList<>();

			JSONObject vault = new JSONObject(vaultJson);
			int version = vault.getInt("version");
			if (version != VAULT_VERSION) throw new IOException("Unsupported vault version: " + version);

			int kdfIterations = vault.optInt("kdfIterations", PBKDF2_ITERATIONS);
			byte[] salt = Base64.getDecoder().decode(vault.getString("salt"));
			byte[] iv = Base64.getDecoder().decode(vault.getString("iv"));
			byte[] ciphertext = Base64.getDecoder().decode(vault.getString("ciphertext"));

			SecretKey key = deriveAesKey(masterPassword, salt, kdfIterations);
			byte[] plaintext = decrypt(ciphertext, key, iv);

			JSONArray accountsJson = new JSONArray(new String(plaintext, StandardCharsets.UTF_8));
			return jsonToAccounts(accountsJson);
		} catch (AEADBadTagException e) {
			throw new IllegalStateException("Vault decryption failed. Wrong password or modified vault file.", e);
		} catch (Exception e) {
			logger.error("Error loading account vault: " + e.getMessage());
			throw new IllegalStateException("Could not load account vault.", e);
		}
	}

	/**
	 * Encrypts the {@code accounts.vault} file with a master password using AES/GCM.
	 *
	 * @param accounts       An {@link ArrayList} of {@link Account}, with the user data.
	 * @param masterPassword The user password, with which the accounts get encrypted.
	 */
	public static void saveAccounts(ArrayList<Account> accounts, String masterPassword) {
		File accountFile = getAccountFilePath().toFile().getAbsoluteFile();
		Path accountPath = accountFile.toPath();
		ensureParentDirectoryExists(accountPath);

		try {
			byte[] salt = randomBytes(SALT_LENGTH_BYTES);
			byte[] iv = randomBytes(IV_LENGTH_BYTES);
			SecretKey key = deriveAesKey(masterPassword, salt, PBKDF2_ITERATIONS);

			byte[] plaintext = accountsToJson(accounts).toString().getBytes(StandardCharsets.UTF_8);
			byte[] ciphertext = encrypt(plaintext, key, iv);

			JSONObject vault = new JSONObject();
			vault.put("version", VAULT_VERSION);
			vault.put("kdf", "PBKDF2WithHmacSHA256");
			vault.put("kdfIterations", PBKDF2_ITERATIONS);
			vault.put("cipher", "AES/GCM/NoPadding");
			vault.put("salt", Base64.getEncoder().encodeToString(salt));
			vault.put("iv", Base64.getEncoder().encodeToString(iv));
			vault.put("ciphertext", Base64.getEncoder().encodeToString(ciphertext));

			Path tempPath = accountPath.resolveSibling(accountPath.getFileName() + ".tmp");
			java.nio.file.Files.writeString(tempPath, vault.toString(4), StandardCharsets.UTF_8);
			try {
				java.nio.file.Files.move(tempPath, accountPath, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE);
			} catch (IOException ignored) {
				java.nio.file.Files.move(tempPath, accountPath, StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (Exception e) {
			logger.error("Error saving account vault: " + e.getMessage());
			throw new IllegalStateException("Could not save account vault.", e);
		}
	}

	/**
	 * Deletes the vault file. Only used when the vault file is corrupted.
	 */
	public static void deleteAccountsFile() {
		getAccountFilePath().toFile().getAbsoluteFile().delete();
	}

	/**
	 * Converts an {@link ArrayList} of {@link Account} to a {@link JSONArray} for easier saving.
	 *
	 * @param accounts An {@link ArrayList} of {@link Account} to convert.
	 * @return The converted {@link JSONArray}.
	 */
	private static JSONArray accountsToJson(ArrayList<Account> accounts) {
		JSONArray array = new JSONArray();
		for (Account account : accounts) {
			JSONObject obj = new JSONObject();
			obj.put("provider", account.getProvider());
			obj.put("username", account.getUsername());
			obj.put("password", account.getPassword());
			obj.put("url", account.getUrl());
			obj.put("comment", account.getComment());
			array.put(obj);
		}
		return array;
	}

	/**
	 * Converts a {@link JSONArray} to an {@link ArrayList} of {@link Account}.
	 *
	 * @param array A {@link JSONArray} to convert.
	 * @return The converted {@link ArrayList} of {@link Account}.
	 */
	private static ArrayList<Account> jsonToAccounts(JSONArray array) {
		ArrayList<Account> accounts = new ArrayList<>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject obj = array.getJSONObject(i);
			accounts.add(new Account(
					obj.optString("provider", ""),
					obj.optString("username", ""),
					obj.optString("password", ""),
					obj.optString("url", ""),
					obj.optString("comment", "")
			));
		}
		return accounts;
	}

	/**
	 * Encrypts plaintext bytes with AES/GCM.
	 *
	 * @param plaintext The bytes to encrypt.
	 * @param key       The AES key used for encryption.
	 * @param iv        The initialization vector (nonce) for GCM.
	 * @return The encrypted bytes including the authentication tag.
	 * @throws GeneralSecurityException If encryption fails.
	 */
	private static byte[] encrypt(byte[] plaintext, SecretKey key, byte[] iv) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.ENCRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
		return cipher.doFinal(plaintext);
	}

	/**
	 * Decrypts ciphertext bytes with AES/GCM.
	 *
	 * @param ciphertext The bytes to decrypt.
	 * @param key        The AES key used for decryption.
	 * @param iv         The initialization vector (nonce) for GCM.
	 * @return The decrypted plaintext bytes.
	 * @throws GeneralSecurityException If decryption fails or authentication does not validate.
	 */
	private static byte[] decrypt(byte[] ciphertext, SecretKey key, byte[] iv) throws GeneralSecurityException {
		Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
		cipher.init(Cipher.DECRYPT_MODE, key, new GCMParameterSpec(GCM_TAG_LENGTH_BITS, iv));
		return cipher.doFinal(ciphertext);
	}

	/**
	 * Derives an AES key from a password using PBKDF2-HMAC-SHA256.
	 *
	 * @param password   The password used for key derivation.
	 * @param salt       The salt used for key derivation.
	 * @param iterations The PBKDF2 iteration count.
	 * @return The derived AES key.
	 * @throws GeneralSecurityException If key derivation fails.
	 */
	private static SecretKey deriveAesKey(String password, byte[] salt, int iterations) throws GeneralSecurityException {
		PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, PBKDF2_KEY_LENGTH);
		SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
		byte[] keyBytes = factory.generateSecret(spec).getEncoded();
		spec.clearPassword();
		return new SecretKeySpec(keyBytes, "AES");
	}

	/**
	 * Generates cryptographically secure random bytes.
	 *
	 * @param length The number of bytes to generate.
	 * @return A byte array filled with random bytes.
	 */
	private static byte[] randomBytes(int length) {
		byte[] bytes = new byte[length];
		RNG.nextBytes(bytes);
		return bytes;
	}

	/**
	 * Ensures the parent directory of the provided file path exists.
	 *
	 * @param filePath The target file path.
	 * @throws IllegalStateException If the directory does not exist and cannot be created.
	 */
	private static void ensureParentDirectoryExists(Path filePath) {
		Path parentDir = filePath.getParent();
		if (parentDir == null || parentDir.toFile().exists()) return;
		if (!parentDir.toFile().mkdirs()) {
			throw new IllegalStateException("Could not create account directory: " + parentDir);
		}
	}

	/**
	 * Resolves the location of the {@code accounts.vault} file for the current runtime.
	 *
	 * @return The path to the vault file, using a local file in portable mode or an OS-specific
	 * application data directory otherwise.
	 */
	public static Path getAccountFilePath() {
		String os = System.getProperty("os.name").toLowerCase();
		String parentDir = "Firstpass";
		String fileName = "accounts.vault";
		String userHome = System.getProperty("user.home");
		if (isPortableVersion()) {
			return Paths.get(fileName);
		} else if (os.contains("win")) {
			return Paths.get(userHome, "AppData", "Roaming", parentDir, fileName);
		} else if (os.contains("mac")) {
			return Paths.get(userHome, "Library", "Application Support", parentDir, fileName);
		} else { // Linux and others
			return Paths.get(userHome, ".local", "share", parentDir, fileName);
		}
	}
}