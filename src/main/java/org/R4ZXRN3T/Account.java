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

	public boolean equalsField(Account accountToCompareWith, SearchableField searchableField) {
		return switch (searchableField) {
			case ALL -> equals(accountToCompareWith);
			case PROVIDER -> Objects.equals(provider, accountToCompareWith.getProvider());
			case USERNAME -> Objects.equals(username, accountToCompareWith.getUsername());
			case PASSWORD -> Objects.equals(password, accountToCompareWith.getPassword());
			case URL -> Objects.equals(url, accountToCompareWith.getUrl());
			case COMMENT -> Objects.equals(comment, accountToCompareWith.getComment());
		};
	}

	public boolean contains(String s) {
		return provider.contains(s) ||
				username.contains(s) ||
				password.contains(s) ||
				url.contains(s) ||
				comment.contains(s);
	}

	public boolean contains(String s, SearchableField searchableField) {
		return switch (searchableField) {
			case ALL -> contains(s);
			case PROVIDER -> provider.contains(s);
			case USERNAME -> username.contains(s);
			case PASSWORD -> password.contains(s);
			case URL -> url.contains(s);
			case COMMENT -> comment.contains(s);
		};
	}

	public boolean containsIgnoreCase(String s) {
		return containsIgnoreCase(s, SearchableField.ALL);
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

	public int compareTo(Account accountToCompareWith) {
		return compareTo(accountToCompareWith, SearchableField.ALL);
	}

	public int compareTo(Account accountToCompareWith, SearchableField searchableField) {
		return switch (searchableField) {
			case PROVIDER -> provider.compareTo(accountToCompareWith.getProvider());
			case USERNAME -> username.compareTo(accountToCompareWith.getUsername());
			case PASSWORD -> password.compareTo(accountToCompareWith.getPassword());
			case URL -> url.compareTo(accountToCompareWith.getUrl());
			case COMMENT -> comment.compareTo(accountToCompareWith.getComment());
			default -> 0;
		};
	}

	public int compareToIgnoreCase(Account accountToCompareWith) {
		return compareToIgnoreCase(accountToCompareWith, SearchableField.ALL);
	}

	public int compareToIgnoreCase(Account accountToCompareWith, SearchableField searchableField) {
		return switch (searchableField) {
			case PROVIDER -> provider.compareToIgnoreCase(accountToCompareWith.getProvider());
			case USERNAME -> username.compareToIgnoreCase(accountToCompareWith.getUsername());
			case PASSWORD -> password.compareToIgnoreCase(accountToCompareWith.getPassword());
			case URL -> url.compareToIgnoreCase(accountToCompareWith.getUrl());
			case COMMENT -> comment.compareToIgnoreCase(accountToCompareWith.getComment());
			default -> 0;
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

		if (provider != null && !provider.isEmpty()) {
			provider = textEncryptor.encrypt(provider);
		}
		if (username != null && !username.isEmpty()) {
			username = textEncryptor.encrypt(username);
		}
		if (password != null && !password.isEmpty()) {
			password = textEncryptor.encrypt(password);
		}
		if (url != null && !url.isEmpty()) {
			url = textEncryptor.encrypt(url);
		}
		if (comment != null && !comment.isEmpty()) {
			comment = textEncryptor.encrypt(comment);
		}
	}

	public void decrypt(String decryptionPassword) {
		if (isEmpty() || decryptionPassword == null || decryptionPassword.isEmpty()) return;

		StrongTextEncryptor textEncryptor = new StrongTextEncryptor();
		textEncryptor.setPasswordCharArray(decryptionPassword.toCharArray());

		if (provider != null && !provider.isEmpty()) {
			provider = textEncryptor.decrypt(provider);
		}
		if (username != null && !username.isEmpty()) {
			username = textEncryptor.decrypt(username);
		}
		if (password != null && !password.isEmpty()) {
			password = textEncryptor.decrypt(password);
		}
		if (url != null && !url.isEmpty()) {
			url = textEncryptor.decrypt(url);
		}
		if (comment != null && !comment.isEmpty()) {
			comment = textEncryptor.decrypt(comment);
		}
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
			for (SearchableField field : SearchableField.values()) {
				if (field.value == value) {
					return field;
				}
			}
			throw new IllegalArgumentException("Invalid value: " + value);
		}

		@Override
		public String toString() {
			return switch (value) {
				case 0 -> "All";
				case 1 -> "Provider";
				case 2 -> "Username";
				case 3 -> "Password";
				case 4 -> "URL";
				case 5 -> "Comment";
				default -> throw new RuntimeException("This shouldn't happen");
			};
		}

		public static SearchableField fromString(String input) {
			return switch (input) {
				case "All" -> fromValue(0);
				case "Provider" -> fromValue(1);
				case "Username" -> fromValue(2);
				case "Password" -> fromValue(3);
				case "URL" -> fromValue(4);
				case "Comment" -> fromValue(5);
				default -> throw new IllegalArgumentException("Invalid input: " + input);
			};
		}

		public int getValue() {
			return this.value;
		}
	}
}