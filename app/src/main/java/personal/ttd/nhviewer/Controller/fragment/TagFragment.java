package personal.ttd.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import personal.ttd.nhviewer.Model.tag.TagManager;
import personal.ttd.nhviewer.R;

public class TagFragment extends android.support.v4.app.Fragment {
    public static final String KEY_PREF_TAG = "tags";
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
            final EditText input = new EditText(requireContext());
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

            input.setInputType(InputType.TYPE_CLASS_TEXT);

            builder.setTitle("Tag name");
            builder.setView(input);

            builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String tagName = input.getText().toString();

                    if (TagManager.addTag(tagName, PreferenceManager.getDefaultSharedPreferences(requireContext()))) {
                        Snackbar.make(getView(), String.format("\"%s\" is added", tagName), Snackbar.LENGTH_SHORT).show();
                        refreshListData();
//                        refreshListView();
                    } else {
                        Snackbar.make(getView(), "Failed adding tag", Snackbar.LENGTH_SHORT).show();
                    }
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            });

            builder.show();
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
                                        PreferenceManager.getDefaultSharedPreferences(requireContext())) ) {
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
