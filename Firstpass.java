import java.util.Scanner;
import java.awt.datatransfer.StringSelection;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.util.ArrayList;
import java.awt.Robot;
import java.awt.AWTException;
import java.awt.event.KeyEvent;
import java.io.File;
import QolPack.ConfirmationPrompt;
import QolPack.InputField;


public class Firstpass
{
	public static final String CurrentVersion = "1.4.1";

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
		if(!CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl"))
		{
			ConfirmationPrompt.custom(3, "\nPlease enter your password:\t", CorrectPassword);
		}        

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
			String MainAction = "placeholder";
			if(!UpdateAvailable)
			{
				MainAction = ConfirmationPrompt.customIgnoreCase(1, "\n\nActions:\t"+PURPLE+"[exit]"+RESET+" save and exit\t"+PURPLE+"[add]"+RESET+" add new entry\t\t"+PURPLE+"[del]"+RESET+" delete entry\t\t"+PURPLE+"[edit]"+RESET+" edit entry\n\t\t"+PURPLE+"[s]"+RESET+
																" search entries\t"+PURPLE+"[undo]"+RESET+" undo last deletion\t"+PURPLE+"[sort]"+RESET+" sort the entries\t\t"+PURPLE+"[set]"+RESET+" open settings menu\n\t\t"+PURPLE+"[gen]"+RESET+" password generator\n"
																, "exit", "add", "del", "s", "undo", "sort", "set", "edit", "gen");
			}
			else
			{
				MainAction = ConfirmationPrompt.customIgnoreCase(1, "\n\nActions:\t"+PURPLE+"[exit]"+RESET+" save and exit\t"+PURPLE+"[add]"+RESET+" add new entry\t\t"+PURPLE+"[del]"+RESET+" delete entry\t\t"+PURPLE+"[edit]"+RESET+" edit entry\n\t\t"+PURPLE+"[s]"+RESET+" search entries\t"+PURPLE+"[undo]"
																	+RESET+" undo last deletion\t"+PURPLE+"[sort]"+RESET+" sort the entries\t\t"+PURPLE+"[set]"+RESET+" open settings menu\n\t\t"+PURPLE+"[gen]"+RESET+" password generator"+CYAN+"\n\n\t\t[UPDATE] Update available! Install now!"+RESET
																	, "exit", "add", "del", "s", "undo", "sort", "set", "edit", "update", "gen");
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
				TempArray[0] = InputField.StringInput(0, "\nProvider:\t");
				TempArray[1] = InputField.StringInput(0, "\nUsername:\t");
				TempArray[2] = InputField.StringInput(0, "\nPassword:\t");
				TempArray[3] = InputField.StringInput(0, "\nURL:\t\t");
				AccountsArr.add(new String[] {TempArray[0],TempArray[1],TempArray[2],TempArray[3]});
			}
			if(MainAction.equalsIgnoreCase("del"))
			{
				if(!EmptyFile)
				{
					int EntryToDelete = InputField.IntInput(0, "\n\nPlease enter the number of the entry you wish to delete:  ", AccountsArr.size(), 0);

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
				System.out.println("\033\143");
				System.out.println("\nSettings:\n");				
				if(CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl"))
				{
					System.out.println("\n1.:\tSet password");
				} else {
					System.out.println("\n1.:\tChange password");
				}
				System.out.println("\n\n2.:\tRemove Password");
				SettingsAction = ConfirmationPrompt.customIgnoreCase(1, "\n\n\nEnter what you want to change:\t(1-2 / "+PURPLE+"[ret]"+RESET+" return)\n\n", "1", "2", "ret");

				if(SettingsAction.equals("1"))
				{
					System.out.println("\033\143");
					if (!CorrectPassword.equals("4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl")) {
						ConfirmationPrompt.custom(0, "\n\nEnter your old password:\t", CorrectPassword);
						System.out.println();
					}
					CorrectPassword = InputField.StringInput(0, "\nEnter your new password:\t");
				}
				if(SettingsAction.equals("2"))
				{
					Boolean Confirmation = ConfirmationPrompt.simple(0, "\nAre you sure you want to remove your password? (y/n])   ");
					if(Confirmation)
					{
						CorrectPassword = "4N5l9bz};wUPI^N>=77jZ5x#q!qX4oO_jhVbkf8[q4if3&6;vl";
					}
				}
			}
			if(MainAction.equalsIgnoreCase("s"))
			{
				boolean EntryFound = false;
				String search = InputField.StringInput(3, "\n\nEnter your search:\t");
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
					String action2 = ConfirmationPrompt.customIgnoreCase(1, "\n\nActions:\t"+PURPLE+"[del]"+RESET+" delete entry\t"+PURPLE+"[ret]"+RESET+" return", "del", "ret");
					if(action2.equalsIgnoreCase("del"))
					{
						System.out.print("\n\nPlease enter the number of the entry you wish to delete:  ");
						int EntryToDelete = InputField.IntInput(1, "\nActions:\t"+PURPLE+"[del]"+RESET+" delete entry\t"+PURPLE+"[ret]"+RESET+" return", AccountsArr.size(), 0);

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
					int EntryToEdit = InputField.IntInput(1, "\n\nPlease enter the number of the entry you wish to edit:  ", AccountsArr.size(), 0);
					
					while(true)
					{
						System.out.println("\033\143");
						System.out.println("\nEdit entry:\n");
						System.out.println("0.: Provider\t"+AccountsArr.get(EntryToEdit)[0]+"\n\n1.: Username\t"+AccountsArr.get(EntryToEdit)[1]+"\n\n2.: Password\t"+AccountsArr.get(EntryToEdit)[2]+"\n\n3.: URL\t\t"+AccountsArr.get(EntryToEdit)[3]);
						System.out.println("\nEnter what you want to edit (0-3 / [ret] to return)\n\n");
						String EntryPartToEdit = ConfirmationPrompt.customIgnoreCase(1, "\nEnter what you want to edit (0-3 / [ret] to return)\n\n", "0", "1", "2", "3", "ret");
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
				boolean Confirmation = ConfirmationPrompt.simple(2, ("\n\nAre you sure you want to update?: (y/n)   "));

				if(Confirmation == true)
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
					System.out.println("\033\143");
					System.out.println("\nYour generated password is:    "+GeneratedPassword);

					String ActionGeneratedPassword = ConfirmationPrompt.customIgnoreCase(1, "\n\n\n"+PURPLE+"[ret]"+RESET+" return\t"+PURPLE+"[copy]"+RESET+" Copy generated password to clipboard\n\n", "ret", "copy");

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
