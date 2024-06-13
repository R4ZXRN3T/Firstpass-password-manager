import org.jasypt.util.text.BasicTextEncryptor;
public class methods
{
	public static String[][] sort2DStringArray(String[][] InitialArray)
	{
		int n = 0;
		while(InitialArray[n][0] != null)
		{
			n++;
		}
		String[] temp;
		for(int i=0;i<n;i++)
		{
			for(int j=0;j<n;j++)
			{
				if(j<(n-1))
				{
					if(InitialArray[j][0].compareToIgnoreCase(InitialArray[j+1][0]) > 0)
					{
						temp = InitialArray[j];
						InitialArray[j] = InitialArray[j+1];
						InitialArray[j+1] = temp;
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
	public static String[][] EncryptArray(String[][] InitialArray) throws Exception
	{
		String[][] FinalArray = new String[InitialArray.length][InitialArray[0].length];
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
		int n = 0;
		while(n<InitialArray.length && InitialArray[n][0] != null)
		{
			n++;
		}
		for(int i=0;i<n;i++)
		{
			FinalArray[i][0] = textEncryptor.encrypt(InitialArray[i][0]);
			FinalArray[i][1] = textEncryptor.encrypt(InitialArray[i][1]);
			FinalArray[i][2] = textEncryptor.encrypt(InitialArray[i][2]);
			FinalArray[i][3] = textEncryptor.encrypt(InitialArray[i][3]);
		}
		return FinalArray;
	}
	public static String[][] DecryptArray(String[][] InitialArray) throws Exception
	{
		String[][] FinalArray = new String[InitialArray.length][InitialArray[0].length];
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		String Password = "CieS~L*2lf#r[0I)pAi3aR5C7n-_}@^RGdAo|V;+";
		textEncryptor.setPasswordCharArray(Password.toCharArray());
		int n = 0;
		while(n<InitialArray.length && InitialArray[n][0] != null)
		{
			n++;
		}
		for(int i=0;i<n;i++)
		{
			FinalArray[i][0] = textEncryptor.decrypt(InitialArray[i][0]);
			FinalArray[i][1] = textEncryptor.decrypt(InitialArray[i][1]);
			FinalArray[i][2] = textEncryptor.decrypt(InitialArray[i][2]);
			FinalArray[i][3] = textEncryptor.decrypt(InitialArray[i][3]);
		}
		return FinalArray;
	}
}