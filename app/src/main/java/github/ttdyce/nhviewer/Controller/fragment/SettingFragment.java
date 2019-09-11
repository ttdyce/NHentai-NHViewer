package github.ttdyce.nhviewer.Controller.fragment;


import android.content.SharedPreferences;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

import java.util.ArrayList;
import java.util.Map;

import github.ttdyce.nhviewer.Model.comic.Collection;
import github.ttdyce.nhviewer.R;

public class SettingFragment extends PreferenceFragmentCompat {
    public static final String KEY_PREF_DEFAULT_LANGUAGE = "default_language";
    public static final String KEY_PREF_DEFAULT_COLLECTION_ID = "default_collection";

    private SharedPreferences sharedPref;
    private mOnSharedPreferenceChangeListener mOnSharedPreferenceChangeListener = new mOnSharedPreferenceChangeListener();

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preferences);

        initDefinedCollectionList();
        initLanguageList();
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Setting");
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(mOnSharedPreferenceChangeListener);
    }

    private void initDefinedCollectionList() {
        ArrayList<Collection> collectionList = getDefinedCollectionList();

        ListPreference collectionListPref = (ListPreference) findPreference(KEY_PREF_DEFAULT_COLLECTION_ID);
        int collectionid = Integer.parseInt(sharedPref.getString(KEY_PREF_DEFAULT_COLLECTION_ID, "-1"));

        if (collectionListPref == null)
            return;

        CharSequence[] entries = new String[collectionList.size()];
        CharSequence[] entryValues = new String[collectionList.size()];

        for (int i = 0; i < collectionList.size(); i++) {
            Collection c = collectionList.get(i);
            entries[i] = c.name;
            entryValues[i] = String.valueOf(c.id);
        }

        collectionListPref.setEntries(entries);
        collectionListPref.setEntryValues(entryValues);
        collectionListPref.setSummary(Collection.NAME_LIST.get(collectionid));

    }

    //the returned collection list only have set collection id and collection name
    private ArrayList<Collection> getDefinedCollectionList() {
        ArrayList<Collection> collectionList = new ArrayList<>();

        for (Map.Entry<Integer, String> entry :
                Collection.NAME_LIST.entrySet()) {
            int id = entry.getKey();
            String name = entry.getValue();

            collectionList.add(new Collection(id, name));
        }

        return collectionList;
    }

    private void initLanguageList() {
        ListPreference languageListPref = (ListPreference) findPreference(KEY_PREF_DEFAULT_LANGUAGE);
        String defaultLanguage = sharedPref.getString(KEY_PREF_DEFAULT_LANGUAGE, "not set");

        if (languageListPref == null)
            return;

        languageListPref.setSummary(defaultLanguage);
        if (defaultLanguage.equals(""))
            languageListPref.setSummary("All");

    }


    private class mOnSharedPreferenceChangeListener implements SharedPreferences.OnSharedPreferenceChangeListener {

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Preference connectionPref = findPreference(key);
            if (key.equals(KEY_PREF_DEFAULT_COLLECTION_ID)) {
                //update preference summary
                int selectedIndex = Integer.parseInt(sharedPreferences.getString(key, "-1"));
                connectionPref.setSummary(Collection.NAME_LIST.get(selectedIndex));

            } else if (key.equals(KEY_PREF_DEFAULT_LANGUAGE)) {
                String selectedLanguage = sharedPreferences.getString(key, "not set");

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, selectedLanguage);
                editor.apply();

                connectionPref.setSummary(selectedLanguage);
            }
        }
    }
}
