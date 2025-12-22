import static org.junit.jupiter.api.Assertions.*;

import org.R4ZXRN3T.firstpass.Account;
import org.junit.jupiter.api.Test;

class AccountTests {

	@Test
	void testAccountCreation() {
		Account account = new Account("Google", "user@example.com", "password123", "google.com", "My Google account");
		assertEquals("Google", account.getProvider());
		assertEquals("user@example.com", account.getUsername());
		assertEquals("password123", account.getPassword());
		assertEquals("google.com", account.getUrl());
		assertEquals("My Google account", account.getComment());
	}

	@Test
	void testEquality() {
		Account account1 = new Account("Google", "user1@example.com", "pass1", "google.com", "");
		Account account2 = new Account("Google", "user2@example.com", "pass2", "google.com", "");
		Account account3 = new Account("Microsoft", "user1@example.com", "pass1", "microsoft.com", "");

		// Test equality based on provider
		assertTrue(account1.equals(account2, Account.SearchableField.PROVIDER));
		assertFalse(account1.equals(account3, Account.SearchableField.PROVIDER));

		// Test equality based on username
		assertTrue(account1.equals(account3, Account.SearchableField.USERNAME));
		assertFalse(account1.equals(account2, Account.SearchableField.USERNAME));
	}

	@Test
	void testContainsIgnoreCase() {
		Account account = new Account("Google", "user@example.com", "password123", "google.com", "My Google account");

		// Test search in different fields
		assertTrue(account.containsIgnoreCase("GOOGLE", Account.SearchableField.PROVIDER));
		assertTrue(account.containsIgnoreCase("example", Account.SearchableField.USERNAME));
		assertTrue(account.containsIgnoreCase("pass", Account.SearchableField.PASSWORD));
		assertTrue(account.containsIgnoreCase("my google", Account.SearchableField.COMMENT));

		// Test search in all fields
		assertTrue(account.containsIgnoreCase("google", Account.SearchableField.ALL));
		assertTrue(account.containsIgnoreCase("example", Account.SearchableField.ALL));
	}
}