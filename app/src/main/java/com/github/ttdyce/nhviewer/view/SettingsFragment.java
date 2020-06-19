package com.github.ttdyce.nhviewer.view;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.github.ttdyce.nhviewer.BuildConfig;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.firebase.Updater;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        showVersionName();
        setVersionOnClick();
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {


    }

    private void setVersionOnClick() {
        PreferenceScreen editTextPreference = findPreference(MainActivity.KEY_PREF_VERSION);
        editTextPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(requireContext(), "Checking latest version...", Toast.LENGTH_SHORT).show();

                Updater.with(requireContext()).onUpdateNeeded(new Updater.OnUpdateNeededListener() {
                    @Override
                    public void onUpdateNeeded(final String updateUrl) {

                        AlertDialog dialog = new AlertDialog.Builder(requireContext(), R.style.DialogTheme)
                                .setTitle(getString(R.string.new_version_available))
                                .setMessage(getString(R.string.new_version_desc))
                                .setPositiveButton(getString(R.string.new_version_download_github),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(updateUrl));
                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        }).setNegativeButton(getString(R.string.new_version_cancel),
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                dialog.dismiss();
                                            }
                                        }).create();
                        dialog.show();
                    }
                }).check();

                return true;
            }
        });
    }

    private void showVersionName() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("SettingsFragment", "onCreate: version name=" + versionName);
        Log.i("SettingsFragment", "onCreate: version code=" + versionCode);

        PreferenceScreen editTextPreference = findPreference(MainActivity.KEY_PREF_VERSION);
        editTextPreference.setSummary(versionName);
    }




    public enum Language{
        all(0), chinese(1), english(2), japanese(3), notSet(-1);

        int id;

        Language(int i) {
            id = i;
        }

        public int getInt() {
            return id;
        }

        public String toString(){
            return String.valueOf(id);
        }

    }

}
