package com.github.ttdyce.nhviewer.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.api.GitHubSponsorsAPI;
import com.google.firebase.auth.FirebaseAuth;

public class AccountSettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.account_preferences);

        setLogoutButton();
        setNHVProxyButton();
    }

    private void setNHVProxyButton() {
        Preference nhvpProxyPreference = findPreference(MainActivity.KEY_PREF_NHVP_PROXY);
        nhvpProxyPreference.setOnPreferenceClickListener(preference -> {
            SharedPreferences pref =  preference.getSharedPreferences();

            boolean enabledNHVPProxy = pref.getBoolean(MainActivity.KEY_PREF_NHVP_PROXY, false);
            if (enabledNHVPProxy)
                GitHubSponsorsAPI.getIsSponsorAsyc(getContext(), () -> {
                    if (MainActivity.isSponsor)
                        Toast.makeText(getContext(), "Thanks for sponsoring :)", Toast.LENGTH_SHORT).show();
                    else
                        Toast.makeText(getContext(), "You are not yet a sponsor!", Toast.LENGTH_SHORT).show();
                });
            else {
                pref.edit().putBoolean(MainActivity.KEY_PREF_IS_SPONSOR, false).apply();
                MainActivity.isSponsor = false;
            }

            return true;
        });
    }


    private void setLogoutButton() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        Preference logoutPreference = findPreference(MainActivity.KEY_PREF_LOGOUT);
        logoutPreference.setOnPreferenceClickListener(preference -> {
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            pref.edit().putString(MainActivity.KEY_PREF_CURRENT_USERNAME, null).apply();
            MainActivity.currentUsername = null;
            firebaseAuth.signOut();

            Navigation.findNavController(getActivity(), R.id.fragmentNavHost).popBackStack();
            return true;
        });
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


    }

}
