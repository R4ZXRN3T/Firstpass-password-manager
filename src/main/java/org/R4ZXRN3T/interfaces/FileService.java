package org.R4ZXRN3T.interfaces;

import org.R4ZXRN3T.Account;

import java.util.ArrayList;

public interface FileService {
    ArrayList<Account> getAccounts(String password);
    void saveAccounts(ArrayList<Account> accounts, String password);
}

