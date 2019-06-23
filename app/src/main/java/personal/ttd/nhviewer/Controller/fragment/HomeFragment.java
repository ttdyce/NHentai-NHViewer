package personal.ttd.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import personal.ttd.nhviewer.Controller.fragment.base.ComicListFragment;
import personal.ttd.nhviewer.Model.comic.ComicMaker;
import personal.ttd.nhviewer.R;

public class HomeFragment extends ComicListFragment {
    public static final String SUBTITLE = "My Home";
    private boolean sortByPopular = false;

    @Override
    protected boolean getHasPage() {
        return true;
    }

    @Override
    protected boolean getCanDelete() {
        return false;
    }

    @Override
    protected String getActionBarTitle() {
        return SUBTITLE;
    }

    @Override
    protected void setList(int page) {
        // TODO: 6/6/2019 This comic list should be configurable, using sharePreference setting
        ComicMaker.getComicListDefault(page, sortByPopular, requireContext(), listReturnCallback, sharedPref);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initDefaultSearchSetting();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();

        if (id == R.id.action_sort) {
            sortByPopular = !sortByPopular;
            refreshRecyclerView(1);
        }

        return true;
    }

    private void initDefaultSearchSetting() {
        String languageNotSet = "not set";
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        String storedLanguage = pref.getString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, languageNotSet);

        if (storedLanguage.equals(languageNotSet)) {
            //pop up dialog for setting default language
            String[] languageArray = getResources().getStringArray(R.array.languages);
            final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(
                    requireContext(),
                    android.R.layout.simple_list_item_1,
                    languageArray);
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            builder.setTitle("Set your default language");

            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, "All");
                    editor.apply();

                    dialog.dismiss();

                    refreshRecyclerView(1);
                }
            });
            builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    SharedPreferences.Editor editor = pref.edit();
                    editor.putString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, languageArray[which]);
                    editor.apply();

                    refreshRecyclerView(1);
                }
            });

            builder.show();
        }
    }
}
