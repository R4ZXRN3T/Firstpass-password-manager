import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

public class check_version
{
    private static final String REPO_URL = "https://api.github.com/repos/R4ZXRN3T/Firstpass-password-manager/releases/latest";

    public static String get()
    {
        String latestVersion = null;
        try
        {
            URL url = new URL(REPO_URL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/vnd.github.v3+json");

            if (conn.getResponseCode() != 200)
            {
                throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
            }

            BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
            StringBuilder sb = new StringBuilder();
            String output;
            while ((output = br.readLine()) != null)
            {
                sb.append(output);
            }
            conn.disconnect();

            JSONObject jsonResponse = new JSONObject(sb.toString());
            latestVersion = jsonResponse.getString("tag_name");
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return(latestVersion);
    }
}