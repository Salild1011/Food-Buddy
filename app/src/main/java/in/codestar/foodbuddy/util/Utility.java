package in.codestar.foodbuddy.util;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.preference.PreferenceManager;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;

import in.codestar.foodbuddy.BuildConfig;
import in.codestar.foodbuddy.R;
import in.codestar.foodbuddy.model.SearchItemModel;

/**
 * Utility class for common methods
 */

public class Utility {

    private static final String[] BANNER_KEYWORDS = { "veg", "cake", "mutton"};

    public static String formatNutritionValues(String str) {
        float val = Float.parseFloat(str);
        return String.format(Locale.US, "%.2f", val);
    }

    public static String buildSearchUrl(Context context, String search) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BuildConfig.SEARCH_AUTHORITY)
                .appendPath("search")
                .appendQueryParameter("q", search)
                .appendQueryParameter("app_id", BuildConfig.RECIPE_APP_ID)
                .appendQueryParameter("app_key", BuildConfig.RECIPE_KEY);

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        int diet = Integer.parseInt(preferences.getString(
                context.getString(R.string.pref_category_diet),
                context.getString(R.string.category_diet_default)));

        int health = Integer.parseInt(preferences.getString(
                context.getString(R.string.pref_category_health),
                context.getString(R.string.category_health_default)));

        switch (diet) {
            case 0:
                builder.appendQueryParameter("diet", "low-carb");
                break;
            case 1:
                builder.appendQueryParameter("diet", "low-fat");
                break;
            case 2:
                builder.appendQueryParameter("diet", "high-fiber");
                break;
        }

        switch (health) {
            case 0:
                builder.appendQueryParameter("health", "vegan");
                break;
            case 1:
                builder.appendQueryParameter("health", "vegetarian");
                break;
            case 2:
                builder.appendQueryParameter("health", "gluten-free");
                break;
        }
        return builder.build().toString();
    }

    public static String buildBannerUrl(String from, String to) {
        String search = BANNER_KEYWORDS[(new Random().nextInt(3))];

        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BuildConfig.SEARCH_AUTHORITY)
                .appendPath("search")
                .appendQueryParameter("q", search)
                .appendQueryParameter("app_id", BuildConfig.RECIPE_APP_ID)
                .appendQueryParameter("app_key", BuildConfig.RECIPE_KEY)
                .appendQueryParameter("from", from)
                .appendQueryParameter("to", to);

        return builder.build().toString();
    }

    public static String buildRecipeUrl(String recipeUri) {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme("https")
                .authority(BuildConfig.SEARCH_AUTHORITY)
                .appendPath("search")
                .appendQueryParameter("r", recipeUri)
                .appendQueryParameter("app_id", BuildConfig.RECIPE_APP_ID)
                .appendQueryParameter("app_key", BuildConfig.RECIPE_KEY);

        return builder.build().toString();
    }

    public static ArrayList<SearchItemModel> parseSearchResults(String result) {
        ArrayList<SearchItemModel> searchItemModels = null;

        try {
            searchItemModels = new ArrayList<>();
            JSONObject obj = new JSONObject(result);
            JSONArray hits = obj.getJSONArray("hits");

            for (int i = 0; i < hits.length(); i++) {
                JSONObject hitObj = hits.getJSONObject(i);
                JSONObject recipeObj = hitObj.getJSONObject("recipe");

                String title = recipeObj.getString("label");
                String subtitle = recipeObj.getString("source");
                String imageUrl = recipeObj.getString("image");

                SearchItemModel model = new SearchItemModel(title, subtitle, imageUrl, recipeObj);
                searchItemModels.add(model);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return searchItemModels;
    }

    public static String getRecipeUri(String recipeStr) {
        String uri = null;

        try {
            JSONObject recipeObj = new JSONObject(recipeStr);
            uri = recipeObj.getString("uri");
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }

        return uri;
    }

    public static ArrayList<String> readRecentsFile(Context context) {
        // Create recents file if does not exist
        File recentsFile = new File(context.getFilesDir(), "recents.csv");
        boolean existed = recentsFile.exists();

        if (!existed) {
            try {
                if (!recentsFile.createNewFile()) {
                    return null;
                }
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        String recentsPath = context.getFilesDir() + "/recents.csv";
        ArrayList<String> fileLines = new ArrayList<>();

        // Read entire file
        if (existed) {
            try {
                BufferedReader br = new BufferedReader(new FileReader(recentsPath));
                String str;
                while ((str = br.readLine()) != null) {
                    fileLines.add(str);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                return null;
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        return fileLines;
    }

    public static boolean updateRecentsFile(Context context, String uri) {
        String recentsPath = context.getFilesDir() + "/recents.csv";
        ArrayList<String> fileLines = readRecentsFile(context);

        // Re-enter details if already existed
        if (fileLines.contains(uri)) {
            fileLines.remove(uri);
        }

        // Keep only 10 items
        while (fileLines.size() >= 10) {
            fileLines.remove(0);
        }
        fileLines.add(uri);

        // Write new entries to recents file
        BufferedWriter listWriter;
        try {
            listWriter = new BufferedWriter(new FileWriter(recentsPath, false));

            for (String line : fileLines) {
                listWriter.write(line);
                listWriter.newLine();
            }
            listWriter.flush();
            listWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isGooglePlayServicesAvailable(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(status != ConnectionResult.SUCCESS) {
            if(googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }
}
