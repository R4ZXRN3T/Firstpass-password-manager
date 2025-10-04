package org.R4ZXRN3T;

import org.R4ZXRN3T.interfaces.FileService;

import java.util.ArrayList;

public class FileServiceImpl implements FileService {

    @Override
    public ArrayList<Account> getAccounts(String password) {
        return Files.getAccounts(password);
    }

    @Override
    public void saveAccounts(ArrayList<Account> accounts, String password) {
        Files.saveAccounts(accounts, password);
    }
}

