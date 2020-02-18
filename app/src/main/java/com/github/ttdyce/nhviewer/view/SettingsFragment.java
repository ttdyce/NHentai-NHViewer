package com.github.ttdyce.nhviewer.view;

import android.os.Bundle;
import android.util.Log;

import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.github.ttdyce.nhviewer.BuildConfig;
import com.github.ttdyce.nhviewer.R;

public class SettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("SettingsFragment", "onCreate: version name=" + versionName);
        Log.i("SettingsFragment", "onCreate: version code=" + versionCode);
        showVersionName(versionName);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void showVersionName(String versionName) {
        PreferenceScreen editTextPreference = findPreference(MainActivity.KEY_PREF_VERSION);
        editTextPreference.setSummary(versionName);
    }

}