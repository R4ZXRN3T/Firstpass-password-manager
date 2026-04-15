import org.R4ZXRN3T.firstpass.Account;
import org.R4ZXRN3T.firstpass.AccountLoader;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.*;

class AccountLoaderTests {

	private String originalUserHome;
	private String originalOsName;

	private static Path getVaultPath() throws Exception {
		Method method = AccountLoader.class.getDeclaredMethod("getAccountFilePath");
		method.setAccessible(true);
		return (Path) method.invoke(null);
	}


	@BeforeEach
	void setup() throws Exception {
		originalUserHome = System.getProperty("user.home");
		originalOsName = System.getProperty("os.name");

		Path tempHome = Files.createTempDirectory("firstpass-vault-test-");
		System.setProperty("user.home", tempHome.toString());
		System.setProperty("os.name", "Windows 11");
	}

	@AfterEach
	void teardown() {
		System.setProperty("user.home", originalUserHome);
		System.setProperty("os.name", originalOsName);
	}

	@Test
	void savesAndLoadsVaultRoundTrip() {
		ArrayList<Account> accounts = new ArrayList<>();
		accounts.add(new Account("GitHub", "user1", "pw1", "https://github.com", "main"));
		accounts.add(new Account("Mail", "user2", "pw2", "https://mail.example", "backup"));

		AccountLoader.saveAccounts(accounts, "master-password");
		ArrayList<Account> loaded = AccountLoader.getAccounts("master-password");

		assertEquals(accounts, loaded);
	}

	@Test
	void rejectsWrongPassword() {
		ArrayList<Account> accounts = new ArrayList<>();
		accounts.add(new Account("X", "Y", "Z", "u", "c"));
		AccountLoader.saveAccounts(accounts, "correct-password");

		IllegalStateException ex = assertThrows(IllegalStateException.class,
				() -> AccountLoader.getAccounts("wrong-password"));
		assertTrue(ex.getMessage().contains("Vault decryption failed"));
	}

	@Test
	void rejectsTamperedCiphertext() throws Exception {
		ArrayList<Account> accounts = new ArrayList<>();
		accounts.add(new Account("A", "B", "C", "D", "E"));
		AccountLoader.saveAccounts(accounts, "master-password");

		Path vaultPath = getVaultPath();
		JSONObject vault = new JSONObject(Files.readString(vaultPath, StandardCharsets.UTF_8));
		byte[] ciphertext = Base64.getDecoder().decode(vault.getString("ciphertext"));
		ciphertext[0] ^= 0x01;
		vault.put("ciphertext", Base64.getEncoder().encodeToString(ciphertext));
		Files.writeString(vaultPath, vault.toString(), StandardCharsets.UTF_8);

		assertThrows(IllegalStateException.class, () -> AccountLoader.getAccounts("master-password"));
	}
}