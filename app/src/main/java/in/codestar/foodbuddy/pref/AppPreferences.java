package in.codestar.foodbuddy.pref;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Class for managing the SharedPreferences for the Food Buddy App
 */

public class AppPreferences {

    // Preference keys
    private static final String PREF_FIRST_RUN = "is_first_run";

    // Check if the app is being run for the first time
    public static boolean isFirstRun(Context context) {
        SharedPreferences prefReader = PreferenceManager.getDefaultSharedPreferences(context);
        boolean firstRun = prefReader.getBoolean(PREF_FIRST_RUN, true);

        if (firstRun) {
            SharedPreferences.Editor editor = prefReader.edit();
            editor.putBoolean(PREF_FIRST_RUN, false);
            editor.apply();
        }
        return firstRun;
    }
}
