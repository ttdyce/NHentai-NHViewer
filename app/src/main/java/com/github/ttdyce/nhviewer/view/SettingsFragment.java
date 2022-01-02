package com.github.ttdyce.nhviewer.view;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.github.ttdyce.nhviewer.BuildConfig;
import com.github.ttdyce.nhviewer.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.OAuthProvider;
import com.microsoft.appcenter.distribute.Distribute;

public class SettingsFragment extends PreferenceFragmentCompat {
    private final String TAG = this.getClass().getSimpleName();

    FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        refreshPreferenceXMLs();

        firebaseAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                refreshPreferenceXMLs();
            }
        });

    }

    private void refreshPreferenceXMLs() {
        setPreferenceScreen(null);

        // Load the preferences from an XML resource
        if (firebaseAuth.getCurrentUser() == null)
            addLoginButton();
        else
            addAccountButton();

        addPreferencesFromResource(R.xml.preferences_must_show);

        showVersionName();
        setVersionOnClick();
        setCheckUpdateOnClick();

        Preference proxyPreference = findPreference(MainActivity.KEY_PREF_PROXY);
        proxyPreference.setOnPreferenceClickListener(preference -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentNavHost);
            navController.navigate(R.id.proxySettingsFragment);
            return true;
        });
    }

    private void setCheckUpdateOnClick() {
        SwitchPreference checkUpdatePreference = findPreference(MainActivity.KEY_PREF_CHECK_UPDATE);
        checkUpdatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(requireContext(), R.string.remind_restart_after_setting, Toast.LENGTH_SHORT).show();
                return true;
            }
        });

    }

    private void addAccountButton() {
        addPreferencesFromResource(R.xml.preferences_account);

        Preference accountPreference = findPreference(MainActivity.KEY_PREF_ACCOUNT);
        accountPreference.setOnPreferenceClickListener(preference -> {
            NavController navController = Navigation.findNavController(getActivity(), R.id.fragmentNavHost);
            navController.navigate(R.id.accountSettingsFragment);
            return true;
        });
    }

    private void addLoginButton() {
        addPreferencesFromResource(R.xml.preferences_login);

        View root = requireActivity().findViewById(R.id.rootMain);
        View bottomNav = requireActivity().findViewById(R.id.bottomNavigation);
        Snackbar snackbar = Snackbar.make(root, "", Snackbar.LENGTH_LONG); // empty text, setText later before show
        snackbar.setAnchorView(bottomNav);

        Preference loginPreference = findPreference(MainActivity.KEY_PREF_LOGIN);
        loginPreference.setOnPreferenceClickListener(preference -> {
            OAuthProvider.Builder provider = OAuthProvider.newBuilder("github.com");

            FirebaseAuth.getInstance()
                    .startActivityForSignInWithProvider(/* activity= */ requireActivity(), provider.build())
                    .addOnSuccessListener(
                            new OnSuccessListener<AuthResult>() {
                                @Override
                                public void onSuccess(AuthResult authResult) {
                                    // User is signed in.
                                    // IdP data available in
                                    // authResult.getAdditionalUserInfo().getProfile().
                                    // The OAuth access token can also be retrieved:
                                    // authResult.getCredential().getAccessToken().
                                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
                                    String username = authResult.getAdditionalUserInfo().getUsername();
                                    MainActivity.currentUsername = username;
                                    pref.edit().putString(MainActivity.KEY_PREF_CURRENT_USERNAME, username).apply();

                                    snackbar.setText(String.format(getString(R.string.prompt_logged_in), username));
                                    snackbar.show();

                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Handle failure.
                                    snackbar.setText(R.string.prompt_login_failed);
                                    snackbar.show();
                                }
                            });

            return true;
        });
    }


    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }

    private void setVersionOnClick() {
        Preference versionPreference = findPreference(MainActivity.KEY_PREF_VERSION);
        versionPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Toast.makeText(requireContext(), "Checking latest version...", Toast.LENGTH_SHORT).show();
                Distribute.checkForUpdate();

                return true;
            }
        });
    }

    private void showVersionName() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        Log.i("SettingsFragment", "onCreate: version name=" + versionName);
        Log.i("SettingsFragment", "onCreate: version code=" + versionCode);

        Preference editTextPreference = findPreference(MainActivity.KEY_PREF_VERSION);
        editTextPreference.setSummary(versionName);
    }

    public enum Language {
        all(0), chinese(1), english(2), japanese(3), notSet(-1);

        int id;

        Language(int i) {
            id = i;
        }

        public int getInt() {
            return id;
        }

        public String toString() {
            return String.valueOf(id);
        }

    }

}
