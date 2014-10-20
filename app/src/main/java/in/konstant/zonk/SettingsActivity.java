package in.konstant.zonk;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.util.Log;

import java.util.List;

public class SettingsActivity extends PreferenceActivity {

    private static final String TAG = "ZonkSettings";
    private static final boolean DBG = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    public static class SettingsFragment
            extends PreferenceFragment
            implements SharedPreferences.OnSharedPreferenceChangeListener {

        private SliderPreference volume;
        private ListPreference effect;

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            if (DBG) Log.d(TAG, "onCreate");

            addPreferencesFromResource(R.xml.activity_settings);

            effect = (ListPreference) this.findPreference("key_effect");
            volume = (SliderPreference) this.findPreference("key_volume");

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);

            setVolume();
            setEffect();
        }

        @Override
        public void onPause() {
            super.onResume();

            if (DBG) Log.d(TAG, "onPause");

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onResume() {
            super.onResume();

            if (DBG) Log.d(TAG, "onResume");

            getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        }

        @Override
        public void onDestroy() {
            if (DBG) Log.d(TAG, "onDestroy");

            getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
            super.onDestroy();
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

            if (DBG) Log.d(TAG, "onSharedPReferenceChanged " + key);

            if (key.compareTo("key_volume") == 0) {
                setVolume();
            }

            if (key.compareTo("key_effect") == 0) {
                setEffect();
            }
        }

        private void setVolume() {
            if (DBG) Log.d(TAG, "setVolume");

            int volume = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).
                    getInt("key_volume", 50);

            this.volume.setSummary(this.getString(R.string.summary_volume, volume));
        }

        private void setEffect() {
            if (DBG) Log.d(TAG, "setEffect");

            String effect = PreferenceManager.getDefaultSharedPreferences(this.getActivity()).
                    getString("key_effect", getResources().getString(R.string.default_effect));

            this.effect.setSummary(effect);
        }

    }
}
