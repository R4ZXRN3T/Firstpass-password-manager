import java.util.InputMismatchException;
import java.util.Scanner;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.File;

public class Firstpass
{
    public static final String CurrentVersion = "1.4.0";

    public static final String RESET	= "\u001B[0m";
    public static final String GREEN	= "\u001B[32m";
    public static final String RED		= "\u001B[31m";
    public static final String YELLOW	= "\u001B[33m";
    public static final String PURPLE	= "\u001B[35m";
    public static final String BOLD		= "\u001B[1m";
    public static final String CYAN		= "\u001B[36m";

    public static void main(String[] args) throws Throwable
	{
        File f = new File("Firstpass_setup.msi");
        if(f.exists() && !f.isDirectory())
        { 
            f.delete();
        }
        String NewestVersion = CurrentVersion;
        boolean UpdateAvailable = false;
        boolean UpdaterNotAvailable = false;
        try
        {
            NewestVersion = check_version.get();
        }
        catch(NoClassDefFoundError e)
        {
            UpdaterNotAvailable = true;
        }
        
        if(NewestVersion != null && !UpdaterNotAvailable)
        {
            if(NewestVersion.compareToIgnoreCase(CurrentVersion) > 0)
            {
                UpdateAvailable = true;
            }
        }
		Scanner input = new Scanner(System.in);
       
        System.out.println("\033\143");

        String CorrectPassword = Files.getCorrectPassword();
        
        String EnteredPassword = "PlpqVDkAuMes";

        String[] DeletedEntry = new String[4];
        int DeletedEntryNumber = -1;

        try
        {
            Robot robot = new Robot();
            robot.keyPress(KeyEvent.VK_CONTROL);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_MINUS);
            robot.keyPress(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_MINUS);
            robot.keyRelease(KeyEvent.VK_CONTROL);
        }
        catch(AWTException e)
        {
            e.printStackTrace();
        }

        while(!EnteredPassword.equals(CorrectPassword) && !CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl"))
        {
            System.out.println("\033\143");
            System.out.println("Please enter your password:\n");
            EnteredPassword = input.nextLine();
        }
        EnteredPassword = "PlpqVDkAuMes";

        ArrayList<String[]> AccountsArr = Files.getAccounts();
        
        while(true)
        {
            System.out.println("\033\143");
            System.out.println(BOLD+GREEN+"Firstpass Password manager v"+CurrentVersion+RESET+"\nby "+BOLD+RED+"R4ZXRN3T"+RESET+"\n\n\n");
            int n = 0;
            while (n < AccountsArr.size())
            {
                System.out.println(n+". "+YELLOW+AccountsArr.get(n)[0]+RESET+":\n");
                System.out.println(CYAN+"\tUsername:\t"+RESET+AccountsArr.get(n)[1]+"\n");
                System.out.println(CYAN+"\tPassword:\t"+RESET+AccountsArr.get(n)[2]+"\n");
                System.out.println(CYAN+"\tURL:\t\t"+RESET+AccountsArr.get(n)[3]+"\n");
                n++;
            }
            boolean EmptyFile = false;
            if(AccountsArr.isEmpty())
            {
                System.out.println("\n\t\t\t[File empty]\n");
                EmptyFile = true;
            }

            if(!UpdateAvailable)
            {
                System.out.println( "\nActions:\t"+PURPLE+"[exit]"+RESET+" save and exit\t"+PURPLE+"[add]"+RESET+" add new entry\t\t"+PURPLE+"[del]"+RESET+" delete entry\t\t"+PURPLE+"[edit]"+RESET+
                                    " edit entry\n\t\t"+PURPLE+"[s]"+RESET+" search entries\t"+PURPLE+"[undo]"+RESET+" undo last deletion\t"+PURPLE+"[sort]"+RESET+" sort the entries\t\t"+PURPLE+"[set]"+RESET+" open settings menu\n\t\t"+PURPLE+"[gen]"+RESET+" password generator\n");
            }
            else
            {
                System.out.println( "\nActions:\t"+PURPLE+"[exit]"+RESET+" save and exit\t"+PURPLE+"[add]"+RESET+" add new entry\t\t"+PURPLE+"[del]"+RESET+" delete entry\t\t"+PURPLE+"[edit]"+RESET+
                                    " edit entry\n\t\t"+PURPLE+"[s]"+RESET+" search entries\t"+PURPLE+"[undo]"+RESET+" undo last deletion\t"+PURPLE+"[sort]"+RESET+" sort the entries\t\t"+PURPLE+"[set]"+RESET+" open settings menu\n\t\t"+PURPLE+"[gen]"+RESET+" password generator"+CYAN+"\n\n\t\t[UPDATE] Update available! Install now!"+RESET);
            }
            String MainAction = "placeholder";
            if(!UpdateAvailable)
            {
                while(!MainAction.equalsIgnoreCase("exit") && !MainAction.equalsIgnoreCase("add") && !MainAction.equalsIgnoreCase("del") && !MainAction.equalsIgnoreCase("s") && !MainAction.equalsIgnoreCase("undo") && !MainAction.equalsIgnoreCase("sort") && !MainAction.equalsIgnoreCase("set") && !MainAction.equalsIgnoreCase("edit") && !MainAction.equalsIgnoreCase("gen"))
                {
                    MainAction = input.nextLine();
                }
            }
            else
            {
                while(!MainAction.equalsIgnoreCase("exit") && !MainAction.equalsIgnoreCase("add") && !MainAction.equalsIgnoreCase("del") && !MainAction.equalsIgnoreCase("s") && !MainAction.equalsIgnoreCase("undo") && !MainAction.equalsIgnoreCase("sort") && !MainAction.equalsIgnoreCase("set") && !MainAction.equalsIgnoreCase("edit") && !MainAction.equalsIgnoreCase("UPDATE") && !MainAction.equalsIgnoreCase("gen"))
                {
                    MainAction = input.nextLine();
                }
            }
            if(MainAction.equalsIgnoreCase("exit"))
            {
                Files.Save(CorrectPassword, AccountsArr);
                input.close();
                return;
            }
            if(MainAction.equalsIgnoreCase("add"))
            {
                String[] TempArray = new String[4];
                System.out.println("\033\143");
                System.out.println("\nAdd new entry:\n");
                System.out.print("\nProvider:\t");
                TempArray[0] = input.nextLine();
                System.out.print("\nUsername:\t");
                TempArray[1] = input.nextLine();
                System.out.print("\nPassword:\t");
                TempArray[2] = input.nextLine();
                System.out.print("\nURL:\t\t");
                TempArray[3] = input.nextLine();
                AccountsArr.add(new String[] {TempArray[0],TempArray[1],TempArray[2],TempArray[3]});
            }
            if(MainAction.equalsIgnoreCase("del"))
            {
                if(!EmptyFile)
                {
                    System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                    int EntryToDelete = 1000000000;
                    do
                    {
                        try
                        {
                            EntryToDelete = input.nextInt();
                            input.nextLine();
                        }
                        catch(InputMismatchException | ArrayIndexOutOfBoundsException e)
                        {
                            input.nextLine();
                            System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                        }
                    }
                    while(EntryToDelete >= n);

                    DeletedEntry = AccountsArr.get(EntryToDelete);
                    DeletedEntryNumber = EntryToDelete;

                    AccountsArr.remove(EntryToDelete);
                }
                else
                {
                    System.out.println("\nNothing to delete here :)");
                    System.out.println("\nPress Enter to continue...");
                    System.in.read();
                }
            }
            if(MainAction.equalsIgnoreCase("undo"))
            {
                if (DeletedEntryNumber != -1)
                {
                    AccountsArr.add(DeletedEntryNumber, DeletedEntry);
                }
                else
                {
                    System.out.println("\nNothing to undo here :)");
                    System.out.println("\nPress Enter to continue...");
                    System.in.read();
                }
            }
            if(MainAction.equalsIgnoreCase("sort"))
            {
                AccountsArr = methods.sort2DStringArray(AccountsArr);
            }
            if(MainAction.equalsIgnoreCase("set"))
            {
                String SettingsAction = "placeholder";
                while (!SettingsAction.equals("1") && !SettingsAction.equals("2") && !SettingsAction.equals("ret"))
                {
                    if(!CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl"))
                    {
                        System.out.println("\033\143");
                        System.out.println("\nSettings:\n");
                        System.out.println("\n1.:\tChange password\n\n2.:\tRemove Password");
                    }
                    else
                    {
                        System.out.println("\033\143");
                        System.out.println("\nSettings:\n");
                        System.out.println("\n1.:\tSet password\n\n2.:\tRemove Password");
                    }
                    System.out.println("\n\nEnter what you want to change:\t(1-2 / "+PURPLE+"[ret]"+RESET+" return)\n");
                    SettingsAction = input.nextLine();
                }
                if(SettingsAction.equals("1"))
                {
                    System.out.println("\033\143");
                    while (!EnteredPassword.equals(CorrectPassword) && !CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl"))
                    {
                        System.out.print("\n\nEnter your old password:\t");
                        EnteredPassword = input.nextLine();
                        System.out.println();
                    }
                    System.out.print("\nEnter your new password:\t");
                    CorrectPassword = input.nextLine();
                }
                if(SettingsAction.equals("2"))
                {
                    System.out.println("\033\143");
                    String Confirmation = "placeholder";
                    while(!Confirmation.equalsIgnoreCase("Y") && !Confirmation.equalsIgnoreCase("N"))
                    {
                        System.out.println("\033\143");
                        System.out.print("\nAre you sure you want to remove your password? ("+PURPLE+"[Y]"+RESET+"/"+PURPLE+"[N]"+RESET+")   ");
                        Confirmation = input.nextLine();
                    }
                    if(Confirmation.equalsIgnoreCase("Y"))
                    {
                        CorrectPassword = "4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl";
                    }
                }
            }
            if(MainAction.equalsIgnoreCase("s"))
            {
                boolean EntryFound = false;
                System.out.print("\nEnter your search:\t");
                String search = input.nextLine();
                System.out.println("\033\143");
                for(int i=0;i<n;i++)
                {
                    if(methods.containsIgnoreCase(AccountsArr.get(i)[0], search))
                    {
                        System.out.println(i+". "+YELLOW+AccountsArr.get(i)[0]+RESET+":\n");
                        System.out.println(CYAN+"\tUsername:\t"+RESET+AccountsArr.get(i)[1]+"\n");
                        System.out.println(CYAN+"\tPassword:\t"+RESET+AccountsArr.get(i)[2]+"\n");
                        System.out.println(CYAN+"\tURL:\t\t"+RESET+AccountsArr.get(i)[3]+"\n");
                        EntryFound = true;
                    }
                }
                if(EntryFound)
                {
                    System.out.println("\nActions:\t"+PURPLE+"[del]"+RESET+" delete entry\t"+PURPLE+"[ret]"+RESET+" return");
                    String action2 = "placeholder";
                    while(!action2.equalsIgnoreCase("del") && !action2.equalsIgnoreCase("ret"))
                    {
                        action2 = input.nextLine();
                    }
                    if(action2.equalsIgnoreCase("del"))
                    {
                        System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                        int EntryToDelete = 1000000000;
                        do
                        {
                            try
                            {
                                EntryToDelete = input.nextInt();
                                input.nextLine();
                            }
                            catch(InputMismatchException | ArrayIndexOutOfBoundsException e)
                            {
                                input.nextLine();
                                System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                            }
                        }
                        while(EntryToDelete >= n);

                        DeletedEntry = AccountsArr.get(EntryToDelete);
                        DeletedEntryNumber = EntryToDelete;

                        AccountsArr.remove(EntryToDelete);
                    }
                }
                else
                {
                    System.out.println("\n\n[no matching entry found]");
                    System.out.println("\n\nPress Enter to return...");
                    System.in.read();
                }
            }
            if(MainAction.equalsIgnoreCase("edit"))
            {
                if(!EmptyFile)
                {
                    System.out.print("\n\nPlease enter the number of the entry you wish to edit:  ");
                    int EntryToEdit = 1000000000;
                    do
                    {
                        try
                        {
                            EntryToEdit = input.nextInt();
                            input.nextLine();
                        }
                        catch(InputMismatchException | ArrayIndexOutOfBoundsException e)
                        {
                            input.nextLine();
                            System.out.print("\n\nPlease enter the number of the entry you wish to edit:  ");
                        }
                    }
                    while(EntryToEdit >= n);
                    
                    while(true)
                    {
                        System.out.println("\033\143");
                        System.out.println("\nEdit entry:\n");
                        System.out.println("0.: Provider\t"+AccountsArr.get(EntryToEdit)[0]+"\n\n1.: Username\t"+AccountsArr.get(EntryToEdit)[1]+"\n\n2.: Password\t"+AccountsArr.get(EntryToEdit)[2]+"\n\n3.: URL\t\t"+AccountsArr.get(EntryToEdit)[3]);
                        System.out.println("\nEnter what you want to edit (0-3 / [ret] to return)\n\n");
                        String EntryPartToEdit = "1000000000";
                        while(!EntryPartToEdit.equals("0") && !EntryPartToEdit.equals("1") && !EntryPartToEdit.equals("2") && !EntryPartToEdit.equals("3") && !EntryPartToEdit.equalsIgnoreCase("ret"))
                        {
                            EntryPartToEdit = input.nextLine();
                        }
                        if(EntryPartToEdit.equalsIgnoreCase("ret"))
                        {
                            break;
                        }
                        else
                        {
                            int EntryPartToEditNumber = Integer.valueOf(EntryPartToEdit);
                            if(EntryPartToEditNumber == 0)
                            {
                                System.out.println("\033\143");
                                System.out.print("\n0.: Provider:\t"+AccountsArr.get(EntryToEdit)[EntryPartToEditNumber]+"  ->  ");
                                AccountsArr.get(EntryToEdit)[EntryPartToEditNumber] = input.nextLine();
                            }
                            else if(EntryPartToEditNumber == 1)
                            {
                                System.out.println("\033\143");
                                System.out.print("\n1.: Username:\t"+AccountsArr.get(EntryToEdit)[EntryPartToEditNumber]+"  ->  ");
                                AccountsArr.get(EntryToEdit)[EntryPartToEditNumber] = input.nextLine();
                            }
                            else if(EntryPartToEditNumber == 2)
                            {
                                System.out.println("\033\143");
                                System.out.print("\n2.: Password:\t"+AccountsArr.get(EntryToEdit)[EntryPartToEditNumber]+"  ->  ");
                                AccountsArr.get(EntryToEdit)[EntryPartToEditNumber] = input.nextLine();
                            }
                            else if(EntryPartToEditNumber == 3)
                            {
                                System.out.println("\033\143");
                                System.out.print("\n3.: URL:\t\t"+AccountsArr.get(EntryToEdit)[EntryPartToEditNumber]+"  ->  ");
                                AccountsArr.get(EntryToEdit)[EntryPartToEditNumber] = input.nextLine();
                            }
                        }
                    }
                }
                else
                {
                    System.out.println("\nNothing to edit here :)");
                    System.out.println("\nPress Enter to continue...");
                    System.in.read();
                }
            }
            if(MainAction.equalsIgnoreCase("UPDATE"))
            {
                String Confirmation = "A";
                while(!Confirmation.equalsIgnoreCase("Y") && !Confirmation.equalsIgnoreCase("N"))
                {
                    System.out.println("\033\143");
                    System.out.print("\nAre you sure you want to update? ("+PURPLE+"[Y]"+RESET+"/"+PURPLE+"[N]"+RESET+")   ");
                    Confirmation = input.nextLine();
                }
                if(Confirmation.equalsIgnoreCase("Y"))
                {
                    Files.Save(CorrectPassword, AccountsArr);
                    input.close();
                    updater.Updater();
                    break;
                }
            }
            if(MainAction.equalsIgnoreCase("gen"))
            {
                String GeneratedPassword = methods.GenerateRandomPassword();
                if(GeneratedPassword != null)
                {
                    String ActionGeneratedPassword = "placeholder";
                    while(!ActionGeneratedPassword.equalsIgnoreCase("ret") && !ActionGeneratedPassword.equalsIgnoreCase("copy"))
                    {
                        System.out.println("\033\143");
                        System.out.println("\nYour generated password is:    "+GeneratedPassword);
                        System.out.println("\n\n"+PURPLE+"[ret]"+RESET+" return\t"+PURPLE+"[copy]"+RESET+" Copy generated password to clipboard\n");
                        ActionGeneratedPassword = input.nextLine();
                    }
                    if(ActionGeneratedPassword.equalsIgnoreCase("copy"))
                    {
                        StringSelection stringSelection = new StringSelection(GeneratedPassword);
                        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                        clipboard.setContents(stringSelection, null);
                        System.out.println("\nPassword successfully copied! ");
                        System.out.println("\nPress enter to return...");
                        System.in.read();
                    }      
                }                          
            }
        }
    }
}
