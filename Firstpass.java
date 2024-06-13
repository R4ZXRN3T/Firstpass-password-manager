import java.util.Scanner;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;

public class Firstpass
{
    public static final String ANSI_RESET = "\u001B[0m";
    public static final String ANSI_GREEN = "\u001B[32m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_YELLOW = "\u001B[33m";
    public static final String ANSI_PURPLE = "\u001B[35m";
    public static final String ANSI_BOLD = "\u001B[1m";
    public static final String ANSI_CYAN = "\u001B[36m";

    public static void main(String[] args) throws Exception
	{
		Scanner input = new Scanner(System.in);
       
        System.out.println("\033\143");

        String[][] AccountsArr = Files.getAccounts();

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
        catch (AWTException e)
        {
            e.printStackTrace();
        }

        while(!EnteredPassword.equals(CorrectPassword))
        {
            System.out.println("\033\143");
            System.out.println("Please enter your password:\n");
            EnteredPassword = input.nextLine();
        }
        EnteredPassword = "PlpqVDkAuMes";
        while(true)
        {
            System.out.println("\033\143");
            System.out.println(ANSI_BOLD+ANSI_GREEN+"Firstpass Password manager"+ANSI_RESET+"\nby "+ANSI_BOLD+ANSI_RED+"R4ZXRN3T"+ANSI_RESET+"\n\n\n");
            int n = 0;
            while (n < AccountsArr.length && AccountsArr[n][0] != null)
            {
                System.out.println(n+". "+ANSI_YELLOW+AccountsArr[n][0]+ANSI_RESET+":\n");
                System.out.println(ANSI_CYAN+"\tUsername:\t"+ANSI_RESET+AccountsArr[n][1]+"\n");
                System.out.println(ANSI_CYAN+"\tPassword:\t"+ANSI_RESET+AccountsArr[n][2]+"\n");
                System.out.println(ANSI_CYAN+"\tURL:\t\t"+ANSI_RESET+AccountsArr[n][3]+"\n");
                n++;
            }
            boolean EmptyFile = false;
            if(AccountsArr[0][0] == null)
            {
                System.out.println("\n\t[File empty]\n");
                EmptyFile = true;
            }

            System.out.println( "\nActions:\t"+ANSI_PURPLE+"[exit]"+ANSI_RESET+" save and exit\t"+ANSI_PURPLE+"[add]"+ANSI_RESET+" add new entry\t\t"+ANSI_PURPLE+"[del]"+ANSI_RESET+" delete entry\t\t"+ANSI_PURPLE+"[edit]"+ANSI_RESET+
                                " edit entry\n\t\t"+ANSI_PURPLE+"[s]"+ANSI_RESET+" search entries\t"+ANSI_PURPLE+"[undo]"+ANSI_RESET+" undo last deletion\t"+ANSI_PURPLE+"[sort]"+ANSI_RESET+" sort the entries\t\t"+ANSI_PURPLE+"[pass]"+ANSI_RESET+" change password\n");
            String action = "placeholder";
            while(!action.equals("exit") && !action.equals("add") && !action.equals("del") && !action.equals("s") && !action.equals("undo") && !action.equals("sort") && !action.equals("pass") && !action.equals("edit"))
            {
                action = input.nextLine();
            }
            if(action.equals("exit"))
            {
                Files.Save(CorrectPassword, AccountsArr);
                input.close();
                return;
            }
            if(action.equals("add"))
            {
                System.out.println("\033\143");
                System.out.println("\nAdd new entry:\n");
                System.out.print("\nProvider:\t");
                AccountsArr[n][0] = input.nextLine();
                System.out.print("\nUsername:\t");
                AccountsArr[n][1] = input.nextLine();
                System.out.print("\nPassword:\t");
                AccountsArr[n][2] = input.nextLine();
                System.out.print("\nURL:\t\t");
                AccountsArr[n][3] = input.nextLine();
            }
            if(action.equals("del"))
            {
                if(!EmptyFile)
                {
                    System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                    int EntryToDelete = 1000000000;
                    while(EntryToDelete >= n)
                    {
                        EntryToDelete = input.nextInt();
                    }
                    input.nextLine();

                    DeletedEntry = AccountsArr[EntryToDelete];
                    DeletedEntryNumber = EntryToDelete;

                    for(int i = EntryToDelete; i < n - 1; i++)
                    {
                        AccountsArr[i] = AccountsArr[i + 1];
                    }
                    AccountsArr[n - 1] = new String[4];
                }
                else
                {
                    System.out.println("\nNothing to delete here :)");
                    System.out.println("\nPress Enter to continue");
                    System.in.read();
                }
            }
            if(action.equals("undo"))
            {
                if (DeletedEntryNumber != -1)
                {
                    for(int i=n;i>DeletedEntryNumber;i--)
                    {
                        AccountsArr[i] = AccountsArr[i - 1];
                    }
                    AccountsArr[DeletedEntryNumber] = DeletedEntry;
                    DeletedEntry = new String[4];
                    DeletedEntryNumber = -1;
                }
                else
                {
                    System.out.println("\nNothing to undo here :)");
                    System.out.println("\nPress Enter to continue");
                    System.in.read();
                }
            }
            if(action.equals("sort"))
            {
                AccountsArr = methods.sort2DStringArray(AccountsArr);
            }
            if(action.equals("pass"))
            {
                System.out.println("\033\143");
                while(!EnteredPassword.equals(CorrectPassword))
                {
                    System.out.print("\n\nEnter your old password:\t");
                    EnteredPassword = input.nextLine();
                    System.out.println();
                }
                System.out.print("\nEnter your new password:\t");
                CorrectPassword = input.nextLine();
            }
            if(action.equals("s"))
            {
                boolean EntryFound = false;
                System.out.print("\nEnter your search:\t");
                String search = input.nextLine();
                System.out.println("\033\143");
                for(int i=0;i<n;i++)
                {
                    if(methods.containsIgnoreCase(AccountsArr[i][0], search))
                    {
                        System.out.println(i+". "+ANSI_YELLOW+AccountsArr[i][0]+ANSI_RESET+":\n");
                        System.out.println(ANSI_CYAN+"\tUsername:\t"+ANSI_RESET+AccountsArr[i][1]+"\n");
                        System.out.println(ANSI_CYAN+"\tPassword:\t"+ANSI_RESET+AccountsArr[i][2]+"\n");
                        System.out.println(ANSI_CYAN+"\tURL:\t\t"+ANSI_RESET+AccountsArr[i][3]+"\n");
                        EntryFound = true;
                    }
                }
                if(EntryFound)
                {
                    System.out.println("\nActions:\t"+ANSI_PURPLE+"[del]"+ANSI_RESET+" delete entry\t"+ANSI_PURPLE+"[ret]"+ANSI_RESET+" return");
                    String action2 = "placeholder";
                    while(!action2.equals("del") && !action2.equals("ret"))
                    {
                        action2 = input.nextLine();
                    }
                    if(action2.equals("del"))
                    {
                        System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
                        int EntryToDelete = 1000000000;
                        while(EntryToDelete >= n)
                        {
                            EntryToDelete = input.nextInt();
                        }
                        input.nextLine();

                        DeletedEntry = AccountsArr[EntryToDelete];
                        DeletedEntryNumber = EntryToDelete;

                        for(int i = EntryToDelete; i < n - 1; i++)
                        {
                            AccountsArr[i] = AccountsArr[i + 1];
                        }
                        AccountsArr[n - 1] = new String[4];
                    }
                }
                else
                {
                    System.out.println("\n\n[no matching entry found]");
                    System.out.println("\n\nPress Enter to return");
                    System.in.read();
                }
            }
            if(action.equals("edit"))
            {
                System.out.print("\n\nPlease enter the number of the entry you wish to edit:  ");
                int EntryToEdit = 1000000000;
                while(EntryToEdit > n)
                {
                    EntryToEdit = input.nextInt();
                    input.nextLine();
                }
                while(true)
                {
                    System.out.println("\033\143");
                    System.out.println("\nEdit entry:\n");
                    System.out.println("0.: Provider\t"+AccountsArr[EntryToEdit][0]+"\n\n1.: Username\t"+AccountsArr[EntryToEdit][1]+"\n\n2.: Password\t"+AccountsArr[EntryToEdit][2]+"\n\n3.: URL\t\t"+AccountsArr[EntryToEdit][3]);
                    System.out.println("\nEnter what you want to edit (0-3 / [ret] to return)\n\n");
                    String EntryPartToEdit = "1000000000";
                    while(!EntryPartToEdit.equals("0") && !EntryPartToEdit.equals("1") && !EntryPartToEdit.equals("2") && !EntryPartToEdit.equals("3") && !EntryPartToEdit.equals("ret"))
                    {
                        EntryPartToEdit = input.nextLine();
                    }
                    if(EntryPartToEdit.equals("ret"))
                    {
                        break;
                    }
                    else
                    {
                        int EntryPartToEditNumber = Integer.valueOf(EntryPartToEdit);
                        if(EntryPartToEditNumber == 0)
                        {
                            System.out.println("\033\143");
                            System.out.print("\n0.: Provider:\t");
                            AccountsArr[EntryToEdit][EntryPartToEditNumber] = input.nextLine();
                        }
                        else if(EntryPartToEditNumber == 1)
                        {
                            System.out.println("\033\143");
                            System.out.print("\n1.: Username:\t");
                            AccountsArr[EntryToEdit][EntryPartToEditNumber] = input.nextLine();
                        }
                        else if(EntryPartToEditNumber == 2)
                        {
                            System.out.println("\033\143");
                            System.out.print("\n2.: Password:\t");
                            AccountsArr[EntryToEdit][EntryPartToEditNumber] = input.nextLine();
                        }
                        else if(EntryPartToEditNumber == 3)
                        {
                            System.out.println("\033\143");
                            System.out.print("\n3.: URL:\t\t");
                            AccountsArr[EntryToEdit][EntryPartToEditNumber] = input.nextLine();
                        }
                    }
                }
            }
        }
    }
}