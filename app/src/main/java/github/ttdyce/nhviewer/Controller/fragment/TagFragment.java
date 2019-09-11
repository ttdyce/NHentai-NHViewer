package github.ttdyce.nhviewer.Controller.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioGroup;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import github.ttdyce.nhviewer.Model.tag.TagManager;
import github.ttdyce.nhviewer.R;

public class TagFragment extends Fragment {
    public static final String KEY_PREF_TAG = "tags";
    public static final String KEY_PREF_TAG_HOME_ENABLE = "tag_home_enable";
    public static final String KEY_PREF_TAG_SEARCH_ENABLE = "tag_search_enable";
    private final List<View> views = new ArrayList<>();
    private ViewPager vp;
    private TabLayout tl;
    private FloatingActionButton fab;
    private CharSequence[] tabTitles;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (views.isEmpty()) {
            views.add(inflater.inflate(R.layout.content_tag_list, container, false));
            views.add(inflater.inflate(R.layout.content_tag_list, container, false));
        }

        return inflater.inflate(R.layout.content_tag, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        vp = view.findViewById(R.id.vpTag);
        tl = view.findViewById(R.id.tlTag);
        fab = getActivity().findViewById(R.id.fabHome);

        ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Tags");

        initTabLayout();
        initViewPager();
        initList();
        initFAB();
    }

    @Override
    public void onResume() {
        super.onResume();

        fab.show();
    }

    @Override
    public void onPause() {
        super.onPause();

        fab.hide();
    }

    private void initFAB() {
        fab.setOnClickListener(v -> {
            //keyboard show/hide
            InputMethodManager inputMethodManager =
                    (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);

            // custom dialog
            final Dialog dialog = new Dialog(requireContext());
            dialog.setContentView(R.layout.dialog_add_tag);
            dialog.setTitle("Add tag");

            // set the custom dialog components - text, image and button
            EditText etTagName = dialog.findViewById(R.id.etTagName);
            RadioGroup rgTagType = dialog.findViewById(R.id.rgTagType);
            Button btnCancel = dialog.findViewById(R.id.btnCancel);
            Button btnAdd = dialog.findViewById(R.id.btnAdd);

            btnAdd.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String tagName;
                    if (rgTagType.getCheckedRadioButtonId() == R.id.rbTagFiltered)
                        tagName = "-";
                    else
                        tagName = "";

                    tagName += etTagName.getText().toString();

                    if (TagManager.addTag(tagName, PreferenceManager.getDefaultSharedPreferences(requireContext()))) {
                        Snackbar.make(getView(), String.format("\"%s\" is added", tagName), Snackbar.LENGTH_SHORT).show();
                        refreshListData();
                    } else {
                        Snackbar.make(getView(), "Failed adding, tag name is not valid", Snackbar.LENGTH_SHORT).show();
                    }

                    dialog.dismiss();
                }
            });

            btnCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });

            dialog.show();
            etTagName.performClick();
        });
    }

    private void initList() {
        ListView v0 = views.get(0).findViewById(R.id.lvTag),
                v1 = views.get(1).findViewById(R.id.lvTag);
        AdapterView.OnItemClickListener onTagClickListener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                //Yes button clicked
                                if (TagManager.removeTag(parent.getAdapter().getItem(position).toString(),
                                        PreferenceManager.getDefaultSharedPreferences(requireContext()))) {
                                    refreshListData();
                                    Snackbar.make(getView(), "Tag removed", Snackbar.LENGTH_SHORT).show();
                                }
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                //No button clicked
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
                builder.setMessage("Are you sure to remove?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

            }
        };

        v0.setOnItemClickListener(onTagClickListener);
        v1.setOnItemClickListener(onTagClickListener);

        refreshListData();
    }

    private void initViewPager() {
        vp.setAdapter(new TagPagerAdapter());
    }

    private void initTabLayout() {
        tabTitles = getResources().getStringArray(R.array.tags);
        tl.setupWithViewPager(vp);
    }

    private void refreshListData() {
        Set<String> rTags = TagManager.getTagRequired(PreferenceManager.getDefaultSharedPreferences(requireContext()));
        Set<String> fTags = TagManager.getTagFiltered(PreferenceManager.getDefaultSharedPreferences(requireContext()));
        View requiredTagList = views.get(0), filteredTagList = views.get(1);

        ((ListView) requiredTagList.findViewById(R.id.lvTag)).setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, rTags.toArray()));
        ((ListView) filteredTagList.findViewById(R.id.lvTag)).setAdapter(new ArrayAdapter<>(requireContext(), android.R.layout.simple_list_item_1, fTags.toArray()));

    }

    private class TagPagerAdapter extends PagerAdapter {

        @NonNull
        @Override
        public Object instantiateItem(@NonNull ViewGroup container, int position) {
            container.addView(views.get(position));

            return views.get(position);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabTitles[position];
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public int getCount() {
            return views.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object o) {
            return view == o;
        }
    }
}
