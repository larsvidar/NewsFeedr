package no.larsvidar.NewsFeedr;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;


public class SettingsActivity extends AppCompatActivity {

    /**
     * onCreate method
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
    }

    /**
     * Method for NewsReferenceFragment
     */
    public static class NewsPreferenceFragment extends PreferenceFragment implements Preference.OnPreferenceChangeListener {
        @Override
        public void onCreate(Bundle savedInstanceState) {

            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.settings_main);

            Preference newsCategory = findPreference(getString(R.string.news_category_key));
            bindPreferenceSummaryToValue(newsCategory);

            Preference sortOrder = findPreference(getString(R.string.sort_order_key));
            bindPreferenceSummaryToValue(sortOrder);
        }

        /**
         * Method to track changes in preferences.
         * @param preference
         * @param value
         * @return true
         */
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int prefIndex = listPreference.findIndexOfValue(stringValue);
                if (prefIndex >= 0) {
                    CharSequence[] labels = listPreference.getEntries();
                    preference.setSummary(labels[prefIndex]);
                }
            } else {
                preference.setSummary(stringValue);
            }
            return true;
        }

        /**
         * bindPreferenceSummaryToValue method
         * @param preference
         */
        private void bindPreferenceSummaryToValue(Preference preference) {
            //Setting change listener
            preference.setOnPreferenceChangeListener(this);

            //Creating reference to the changed preference
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(preference.getContext());

            //Set preference value
            String preferenceString = preferences.getString(preference.getKey(), "");
            onPreferenceChange(preference, preferenceString);

        }
    }
}
