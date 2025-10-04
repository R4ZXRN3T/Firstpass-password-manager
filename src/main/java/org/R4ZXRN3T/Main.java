package org.R4ZXRN3T;

import org.R4ZXRN3T.interfaces.AccountService;
import org.R4ZXRN3T.interfaces.FileService;

public class Main {
	public static void main() {
		// Create JFrame
		Firstpass firstpass = createFirstpass();
		firstpass.run();
	}

	private static Firstpass createFirstpass() {
		// Create services
		FileService fileService = new FileServiceImpl();
		// Need to create Firstpass first because AccountManager needs a reference to it
		Firstpass firstpass = new Firstpass(null, fileService);
		// Now create AccountManager with reference to Firstpass
		AccountService accountService = new AccountManager(firstpass.frame, firstpass);
		// Set the account service in Firstpass
		// This is a bit of a circular dependency, but it works
		firstpass = new Firstpass(accountService, fileService);

		return firstpass;
	}
}