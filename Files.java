import java.util.ArrayList;
import java.util.Scanner;
import org.jasypt.util.text.BasicTextEncryptor;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;

public class Files
{
    public static ArrayList<String[]> getAccounts() throws Exception
    {
        File AccountsFile = new File("accounts.txt");
        AccountsFile.createNewFile();
        Scanner ReadAcc = new Scanner(AccountsFile);
        ArrayList<String[]> AccountsArr = new ArrayList<String[]>();
        if(ReadAcc.hasNextLine())
        {
            ReadAcc.nextLine();
        }
        while(ReadAcc.hasNextLine())
        {
            String[] TempArray = new String[4];
            if (ReadAcc.hasNextLine()) {
                TempArray[0] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                TempArray[1] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                TempArray[2] = ReadAcc.nextLine();
            } else break;

            if (ReadAcc.hasNextLine()) {
                TempArray[3] = ReadAcc.nextLine();
            } else break;
            AccountsArr.add(new String[] {TempArray[0],TempArray[1],TempArray[2],TempArray[3]});
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
    public static void Save(String password, ArrayList<String[]> accounts) throws Exception
    {
        BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
        password = textEncryptor.encrypt(password);
        accounts = methods.EncryptArray(accounts);
        PrintWriter pw = new PrintWriter(new FileWriter("accounts.txt"));
        pw.println(password);
        int g = 0;
        while(g<accounts.size())
        {
            pw.println(accounts.get(g)[0]);
            pw.println(accounts.get(g)[1]);
            pw.println(accounts.get(g)[2]);
            pw.println(accounts.get(g)[3]);
            g++;
        }
        pw.close();
    }
}
