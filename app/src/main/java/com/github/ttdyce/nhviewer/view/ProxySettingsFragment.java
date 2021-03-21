package com.github.ttdyce.nhviewer.view;

import android.os.Bundle;

import androidx.preference.PreferenceFragmentCompat;

import com.github.ttdyce.nhviewer.R;

public class ProxySettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.proxy_preferences);
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


    }

}
