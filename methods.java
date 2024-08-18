import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Scanner;

import org.jasypt.util.text.BasicTextEncryptor;
public class methods
{
	public static ArrayList<String[]> sort2DStringArray(ArrayList<String[]> InitialArray)
	{
		String[] temp;
		for(int i=0;i<InitialArray.size();i++)
		{
			for(int j=0;j<InitialArray.size();j++)
			{
				if(j<(InitialArray.size()-1))
				{					
					if(InitialArray.get(j)[0].compareToIgnoreCase(InitialArray.get(j+1)[0]) > 0)
					{
						temp = InitialArray.get(j);
						InitialArray.set(j, InitialArray.get(j+1));
						InitialArray.set(j+1, temp);
					}
				}				
			}
		}
		return InitialArray;
	}
	public static boolean containsIgnoreCase(String String1, String String2)
	{
		boolean containsIsTrue = false;
		if(String1.toLowerCase().contains(String2.toLowerCase()))
		{
			containsIsTrue = true;
		}		
		return containsIsTrue;
	}
	public static ArrayList<String[]> EncryptArray(ArrayList<String[]> InitialArray) throws Exception
	{
		ArrayList<String[]> FinalArray = new ArrayList<String[]>();
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
		String[] TempArray = new String[4];
		for(int i=0;i<InitialArray.size();i++)
		{
			TempArray = InitialArray.get(i);
			for(int f=0;f<TempArray.length;f++)
			{
				TempArray[f] = textEncryptor.encrypt(TempArray[f]);
			}
			FinalArray.add(TempArray);
		}
		return FinalArray;
	}
	public static ArrayList<String[]> DecryptArray(ArrayList<String[]> InitialArray) throws Exception
	{
		ArrayList<String[]> FinalArray = new ArrayList<String[]>();
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
		String[] TempArray = new String[4];
		for(int i=0;i<InitialArray.size();i++)
		{
			TempArray = InitialArray.get(i);
			for(int f=0;f<TempArray.length;f++)
			{
				TempArray[f] = textEncryptor.decrypt(TempArray[f]);
			}
			FinalArray.add(TempArray);
		}
		return FinalArray;
	}
	public static String GenerateRandomPassword() throws IOException
	{
		@SuppressWarnings("resource")
		Scanner input = new Scanner(System.in);
		boolean InputFailed = true;
		boolean AddLetters = false;
		boolean AddSpecialCharacters = false;
		boolean AddNumbers = false;
		String Letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String Numbers = "1234567890";
		String SpecialCharacters = "!§$%&/()=?{[]}<>|,;.:-_#'+*~\\\"^°";
		int PasswordLength = 1;
		while(InputFailed)
		{
			System.out.println("\033\143");
			System.out.println("Do you want to add Letters?\n");
			try
			{
				if(input.nextBoolean())
				{
					AddLetters = true;
				}
				InputFailed = false;
				input.nextLine();
			}
			catch(InputMismatchException e)
			{
				InputFailed = true;
				input.nextLine();
			}
		}
		InputFailed = true;
		while(InputFailed)
		{
			System.out.println("\033\143");
			System.out.println("Do you want to add special characters?\n");
			try
			{
				if(input.nextBoolean())
				{
					AddSpecialCharacters = true;
				}
				InputFailed = false;
				input.nextLine();
			}
			catch(InputMismatchException e)
			{
				InputFailed = true;
				input.nextLine();
			}
		}
		InputFailed = true;
		while(InputFailed)
		{
			System.out.println("\033\143");
			System.out.println("Do you want to add numbers?\n");
			try
			{
				if(input.nextBoolean())
				{
					AddNumbers = true;
				}
				InputFailed = false;
				input.nextLine();
			}
			catch(InputMismatchException e)
			{
				InputFailed = true;
				input.nextLine();
			}
		}
		InputFailed = true;
		if(!AddLetters && !AddNumbers && !AddSpecialCharacters)
		{
			System.out.println("You need to select one of the options");
			System.out.println("\n\nPress Enter to return...");
			System.in.read();
			return null;
		}
		do
		{
			System.out.println("\033\143");
			System.out.println("How long should the password be?    (Max. 100 characters)\n");
			try
			{
				PasswordLength = input.nextInt();
				if(PasswordLength > 100)
				{
					PasswordLength = 100;
				}
				InputFailed = false;
				input.nextLine();
			}
			catch(InputMismatchException e)
			{
				InputFailed = true;
				input.nextLine();
			}
		}
		while (InputFailed);
		
		String FinalCharacterCollection = "";

		if(AddLetters) {FinalCharacterCollection = FinalCharacterCollection + Letters;}

		if(AddNumbers) {FinalCharacterCollection = FinalCharacterCollection + Numbers;}

		if(AddSpecialCharacters) {FinalCharacterCollection = FinalCharacterCollection + SpecialCharacters;}

		SecureRandom random = new SecureRandom();
		
		StringBuilder sb = new StringBuilder(PasswordLength);
		for(int i=0;i<PasswordLength;i++)
		{
			sb.append(FinalCharacterCollection.charAt(random.nextInt(FinalCharacterCollection.length())));
		}		
		return sb.toString();		
	}
}
