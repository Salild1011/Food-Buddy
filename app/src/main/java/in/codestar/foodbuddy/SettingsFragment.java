package in.codestar.foodbuddy;

import android.os.Bundle;
import android.preference.PreferenceFragment;

/**
 * Fragment for handling settings and preferences
 */

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_nutrition);
    }
}
