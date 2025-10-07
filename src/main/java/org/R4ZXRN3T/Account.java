package org.R4ZXRN3T;

import org.jasypt.util.text.StrongTextEncryptor;

import java.util.Objects;

/**
 * Represents an account with provider, username, password, URL, and comment fields.
 * Supports encryption/decryption and searching/filtering by fields.
 */
public class Account {

	// way too many methods
	// all of them are luckily self-explanatory
	private String provider;
	private String username;
	private String password;
	private String url;
	private String comment;
	private int index;

	/**
	 * Default constructor. Initializes all fields to null or zero.
	 */
	public Account() {
		provider = null;
		username = null;
		password = null;
		url = null;
		comment = null;
		index = 0;
	}

	/**
	 * Constructs an Account with specified values.
	 * @param provider the account provider
	 * @param username the account username
	 * @param password the account password
	 * @param url the account URL
	 * @param comment additional comments
	 */
	public Account(String provider, String username, String password, String url, String comment) {
		this.provider = provider;
		this.username = username;
		this.password = password;
		this.url = url;
		this.comment = comment;
		index = 0;
	}

	/**
	 * Constructs an Account with specified values and index.
	 * @param provider the account provider
	 * @param username the account username
	 * @param password the account password
	 * @param url the account URL
	 * @param comment additional comments
	 * @param index the index of the account in a list
	 */
	public Account(String provider, String username, String password, String url, String comment, int index) {
		this.provider = provider;
		this.username = username;
		this.password = password;
		this.url = url;
		this.comment = comment;
		this.index = index;
	}

	/**
	 * Checks if string1 contains string2, ignoring case.
	 * @param string1 the string to search in
	 * @param string2 the string to search for
	 * @return if string1 contains string2 (case-insensitive), false otherwise
	 */
	private static boolean stringContainsIgnoreCase(String string1, String string2) {
		return string1.toLowerCase().contains(string2.toLowerCase());
	}

	/**
	 * Gets the provider.
	 * @return the provider
	 */
	public String getProvider() {
		return provider;
	}

	/**
	 * Sets the provider.
	 * @param provider the provider to set
	 */
	public void setProvider(String provider) {
		this.provider = provider;
	}

	/**
	 * Gets the username.
	 * @return the username
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the username.
	 * @param username the username to set
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * Gets the password.
	 * @return the password
	 */
	public String getPassword() {
		return password;
	}

	/**
	 * Sets the password.
	 * @param password the password to set
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * Gets the URL.
	 * @return the URL
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * Sets the URL.
	 * @param url the URL to set
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	/**
	 * Gets the comment.
	 * @return the comment
	 */
	public String getComment() {
		return comment;
	}

	/**
	 * Sets the comment.
	 * @param comment the comment to set
	 */
	public void setComment(String comment) {
		this.comment = comment;
	}

	/**
	 * Gets the index.
	 * @return the index
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * Sets the index.
	 * @param index the index to set
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 * Checks equality with another object.
	 * @param o the object to compare
	 * @return true if equal, false otherwise
	 */
	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Account account = (Account) o;
		return Objects.equals(provider, account.provider) &&
				Objects.equals(username, account.username) &&
				Objects.equals(password, account.password) &&
				Objects.equals(url, account.url) &&
				Objects.equals(comment, account.comment);
	}

	/**
	 * Checks equality with another Account, using a specific field.
	 * @param accountToCompareWith the account to compare
	 * @param searchableField the field to compare
	 * @return true if equal on the specified field, false otherwise
	 */
	public boolean equals(Account accountToCompareWith, SearchableField searchableField) {
		return switch (searchableField) {
			case ALL -> equals(accountToCompareWith);
			case PROVIDER -> Objects.equals(provider, accountToCompareWith.getProvider());
			case USERNAME -> Objects.equals(username, accountToCompareWith.getUsername());
			case PASSWORD -> Objects.equals(password, accountToCompareWith.getPassword());
			case URL -> Objects.equals(url, accountToCompareWith.getUrl());
			case COMMENT -> Objects.equals(comment, accountToCompareWith.getComment());
		};
	}

	/**
	 * Checks if the specified string is contained in the account fields, ignoring case.
	 * @param s the string to search for
	 * @param searchableField the field to search in
	 * @return true if contained, false otherwise
	 */
	public boolean containsIgnoreCase(String s, SearchableField searchableField) {
		return switch (searchableField) {
			case ALL -> stringContainsIgnoreCase(provider, s) ||
					stringContainsIgnoreCase(username, s) ||
					stringContainsIgnoreCase(password, s) ||
					stringContainsIgnoreCase(url, s) ||
					stringContainsIgnoreCase(comment, s);
			case PROVIDER -> stringContainsIgnoreCase(provider, s);
			case USERNAME -> stringContainsIgnoreCase(username, s);
			case PASSWORD -> stringContainsIgnoreCase(password, s);
			case URL -> stringContainsIgnoreCase(url, s);
			case COMMENT -> stringContainsIgnoreCase(comment, s);
		};
	}

	/**
	 * Converts the account fields to an array.
	 * @return an array of account fields
	 */
	public String[] toArray() {
		String[] returnArray = new String[5];

		returnArray[0] = provider;
		returnArray[1] = username;
		returnArray[2] = password;
		returnArray[3] = url;
		returnArray[4] = comment;

		return returnArray;
	}

	/**
	 * Computes the hash code for the account.
	 * @return the hash code
	 */
	@Override
	public int hashCode() {
		return Objects.hash(provider, username, password, url, comment);
	}

	/**
	 * Encrypts all account fields using the specified password.
	 * @param encryptionPassword the password for encryption
	 */
	public void encrypt(String encryptionPassword) {
		if (isEmpty() || encryptionPassword == null || encryptionPassword.isEmpty()) return;

		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPasswordCharArray(encryptionPassword.toCharArray());

		if (provider != null && !provider.isEmpty()) provider = textEncryptor.encrypt(provider);
		if (username != null && !username.isEmpty()) username = textEncryptor.encrypt(username);
		if (password != null && !password.isEmpty()) password = textEncryptor.encrypt(password);
		if (url != null && !url.isEmpty()) url = textEncryptor.encrypt(url);
		if (comment != null && !comment.isEmpty()) comment = textEncryptor.encrypt(comment);
	}

	/**
	 * Decrypts all account fields using the specified password.
	 * @param decryptionPassword the password for decryption
	 */
	public void decrypt(String decryptionPassword) {
		if (isEmpty() || decryptionPassword == null || decryptionPassword.isEmpty()) return;

		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPasswordCharArray(decryptionPassword.toCharArray());

		if (provider != null && !provider.isEmpty()) provider = textEncryptor.decrypt(provider);
		if (username != null && !username.isEmpty()) username = textEncryptor.decrypt(username);
		if (password != null && !password.isEmpty()) password = textEncryptor.decrypt(password);
		if (url != null && !url.isEmpty()) url = textEncryptor.decrypt(url);
		if (comment != null && !comment.isEmpty()) comment = textEncryptor.decrypt(comment);
	}

	/**
	 * Checks if all account fields are empty or null.
	 * @return true if empty, false otherwise
	 */
	public boolean isEmpty() {
		if ((provider == null && username == null && password == null && url == null && comment == null)) return true;
		assert provider != null;
		return provider.isEmpty() && username.isEmpty() && password.isEmpty() && url.isEmpty() && comment.isEmpty();
	}

	/**
	 * Enum representing searchable fields in the Account.
	 */
	public enum SearchableField {
		ALL(0),
		PROVIDER(1),
		USERNAME(2),
		PASSWORD(3),
		URL(4),
		COMMENT(5);

		private final int value;

		/**
		 * Constructs a SearchableField with the specified value.
		 * @param value the integer value of the field
		 */
		SearchableField(int value) {
			this.value = value;
		}

		/**
		 * Gets the SearchableField corresponding to the specified value.
		 * @param value the integer value
		 * @return the corresponding SearchableField
		 * @throws IllegalArgumentException if the value is invalid
		 */
		public static SearchableField fromValue(int value) {
			for (SearchableField field : SearchableField.values()) if (field.value == value) return field;
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
}