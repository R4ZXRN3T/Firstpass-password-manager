package org.R4ZXRN3T.interfaces;

import org.R4ZXRN3T.Account;

import java.util.ArrayList;

public interface AccountService {
    void addAccount();
    void removeAccount(int rowIndex);
    void editAccount(int rowIndex);
    void undoDeletion();
    void search(String searchQuery);
    ArrayList<Account> getAccounts();
    void setAccounts(ArrayList<Account> accounts);
    boolean isUndoAvailable();
    void refreshIndices();
}

