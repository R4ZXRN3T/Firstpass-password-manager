import java.net.URL;
import java.net.HttpURLConnection;
import java.util.Map;
import java.util.List;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class updater
{
    private static boolean isRedirected(Map<String,List<String>>header)
    {
        for( String hv : header.get( null ))
        {
            if(hv.contains(" 301 ") || hv.contains(" 302 "))
            {
                return true;
            }
        }
        return false;
    }
    public static void main(String[] args) throws Throwable
    {
        System.out.println("\033\143");
        System.out.println("\nFirstpass update service\n");

        System.out.println("Checking newest version...\n");
        String NewestVersion = check_version.get();
        System.out.println("Done!\n");

        System.out.println("Downloading installer...\n");
        String link = "https://github.com/R4ZXRN3T/Firstpass-password-manager/releases/download/"+NewestVersion+"/Firstpass_setup.msi";
        String fileName = "Firstpass.jar";
        URL url  = new URL( link );
        HttpURLConnection http = (HttpURLConnection)url.openConnection();
        Map < String, List< String >> header = http.getHeaderFields();
        while(isRedirected(header))
        {
            link = header.get("Location").get(0);
            url = new URL(link);
            http = (HttpURLConnection)url.openConnection();
            header = http.getHeaderFields();
        }
        InputStream input = http.getInputStream();
        byte[] buffer = new byte[4096];
        int n = -1;
        OutputStream output = new FileOutputStream(new File(fileName));
        while ((n = input.read(buffer)) != -1)
        {
            output.write(buffer,0,n);
        }
        output.close();
        System.out.println("Done!");
    }
}