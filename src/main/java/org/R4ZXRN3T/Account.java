package org.R4ZXRN3T;

import org.jasypt.util.text.StrongTextEncryptor;

import java.util.Objects;

public class Account {

	// way too many methods
	// all of them are luckily self-explanatory
	private String provider;
	private String username;
	private String password;
	private String url;
	private String comment;
	private int index;

	public Account() {
		provider = null;
		username = null;
		password = null;
		url = null;
		comment = null;
		index = 0;
	}

	public Account(String provider, String username, String password, String url, String comment) {
		this.provider = provider;
		this.username = username;
		this.password = password;
		this.url = url;
		this.comment = comment;
		index = 0;
	}

	public Account(String provider, String username, String password, String url, String comment, int index) {
		this.provider = provider;
		this.username = username;
		this.password = password;
		this.url = url;
		this.comment = comment;
		this.index = index;
	}

	private static boolean stringContainsIgnoreCase(String string1, String string2) {
		return string1.toLowerCase().contains(string2.toLowerCase());
	}

	public String getProvider() {
		return provider;
	}

	public void setProvider(String provider) {
		this.provider = provider;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

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

	public String[] toArray() {
		String[] returnArray = new String[5];

		returnArray[0] = provider;
		returnArray[1] = username;
		returnArray[2] = password;
		returnArray[3] = url;
		returnArray[4] = comment;

		return returnArray;
	}

	@Override
	public int hashCode() {
		return Objects.hash(provider, username, password, url, comment);
	}

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

	public boolean isEmpty() {
		if ((provider == null && username == null && password == null && url == null && comment == null)) return true;
		assert provider != null;
		return provider.isEmpty() && username.isEmpty() && password.isEmpty() && url.isEmpty() && comment.isEmpty();
	}

	public enum SearchableField {
		ALL(0),
		PROVIDER(1),
		USERNAME(2),
		PASSWORD(3),
		URL(4),
		COMMENT(5);

		private final int value;

		SearchableField(int value) {
			this.value = value;
		}

		public static SearchableField fromValue(int value) {
			for (SearchableField field : SearchableField.values()) if (field.value == value) return field;
			throw new IllegalArgumentException("Invalid value: " + value);
		}
	}
}