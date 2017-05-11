package in.codestar.foodbuddy.loader;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

/**
 * Loader for downloading data from the internet
 */

public class DataAsyncLoader extends AsyncTaskLoader<String> {

    private URL mUrl;
    private boolean mSecure;
    private final String LOG_TAG = getClass().getSimpleName();

    public DataAsyncLoader(Context context, URL url, boolean secure) {
        super(context);
        mUrl = url;
        mSecure = secure;
    }

    @Override
    public String loadInBackground() {

        Log.d(LOG_TAG, "Loader started");

        //Fetch String from url echo
        StringBuilder strBuilder = new StringBuilder();
        try {
            InputStream inputStream;

            if (mSecure) {
                HttpsURLConnection connection = (HttpsURLConnection) mUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                inputStream = connection.getInputStream();
            }
            else {
                HttpURLConnection connection = (HttpURLConnection) mUrl.openConnection();
                connection.setRequestMethod("GET");
                connection.connect();
                inputStream = connection.getInputStream();
            }

            if (inputStream == null) {
                Log.w(LOG_TAG, "InputStream is null");
                return null;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String str;

            while ((str = br.readLine()) != null) {
                strBuilder.append(str);
            }

            inputStream.close();
            br.close();

            if (strBuilder.length() == 0) {
                Log.w(LOG_TAG, "No String Received");
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return strBuilder.toString();
    }
}
