import java.util.Scanner;
import org.jasypt.util.text.BasicTextEncryptor;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Files
{
    public static String[][] getAccounts() throws Exception
    {
        File AccountsFile = new File("accounts.txt");
        AccountsFile.createNewFile();
        Scanner ReadAcc = new Scanner(AccountsFile);
        String[][] AccountsArr = new String[10000][4];
        if(ReadAcc.hasNextLine())
        {
            ReadAcc.nextLine();
        }        
        int m = 0;
        while(ReadAcc.hasNextLine() && m < AccountsArr.length)
        {
            if (ReadAcc.hasNextLine()) {
                AccountsArr[m][0] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                AccountsArr[m][1] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                AccountsArr[m][2] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                AccountsArr[m][3] = ReadAcc.nextLine();
            } else break;
            m++;
        }
        AccountsArr = methods.DecryptArray(AccountsArr);
        ReadAcc.close();

        return AccountsArr;
    }
    public static String getCorrectPassword() throws Exception
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
        String CorrectPassword = "password";
        File AccountsFile = new File("accounts.txt");
        Scanner ReadAcc = new Scanner(AccountsFile);
        if(AccountsFile.length() > 0)
        {
            CorrectPassword = ReadAcc.nextLine();
            CorrectPassword = textEncryptor.decrypt(CorrectPassword);
        }
        ReadAcc.close();
        return CorrectPassword;
    }
    public static void Save(String password, String[][] accounts) throws Exception
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
        password = textEncryptor.encrypt(password);
        accounts = methods.EncryptArray(accounts);
        PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt"));
        pw.println(password);
        int g = 0;
        while(accounts[g][0] != null)
        {
            pw.println(accounts[g][0]);
            pw.println(accounts[g][1]);
            pw.println(accounts[g][2]);
            pw.println(accounts[g][3]);
            g++;
        }
        pw.close();
    }
}