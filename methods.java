import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import org.jasypt.util.text.BasicTextEncryptor;
import QolPack.ConfirmationPrompt;
import QolPack.InputField;

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
		System.out.println("\033\143");
		boolean AddLetters = ConfirmationPrompt.simple(0, "\nDo you want to add Letters?: (y/n)\t");
		boolean AddSymbols = ConfirmationPrompt.simple(0, "\n\nDo you want to add symbols?: (y/n)\t");
		boolean AddNumbers = ConfirmationPrompt.simple(0, "\n\nDo you want to add numbers? (y/n)\t");
		String Letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
		String Numbers = "1234567890";
		String SpecialCharacters = "!§$%&/()=?{[]}<>|,;.:-_#'+*~\\\"^°";
		int PasswordLength = InputField.IntInput(0, "\n\nHow long should the password be?:  (Max. 100 characters)\t", 100, 1);
		
		String FinalCharacterCollection = "";

		if(AddLetters) {FinalCharacterCollection = FinalCharacterCollection + Letters;}

		if(AddNumbers) {FinalCharacterCollection = FinalCharacterCollection + Numbers;}

		if(AddSymbols) {FinalCharacterCollection = FinalCharacterCollection + SpecialCharacters;}

		SecureRandom random = new SecureRandom();
		
		StringBuilder sb = new StringBuilder(PasswordLength);
		for(int i=0;i<PasswordLength;i++)
		{
			sb.append(FinalCharacterCollection.charAt(random.nextInt(FinalCharacterCollection.length())));
		}		
		return sb.toString();		
	}
}
