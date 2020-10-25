package com.github.ttdyce.nhviewer.view;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;
import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.presenter.ComicPresenter;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;


public class SearchingFragment extends Fragment {
    private static final String TAG = "SearchingFragment";

    private ContentLoadingProgressBar pbComicList;
    private TextView tvComicListDesc;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comic_list, container, false);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        pbComicList = view.findViewById(R.id.pbComicList);
        tvComicListDesc = view.findViewById(R.id.tvComicListDesc);

        pbComicList.setVisibility(View.INVISIBLE);
        tvComicListDesc.setText(getString(R.string.enter_search_query));
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_items_searching, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) requireActivity().getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_searchview).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(requireActivity().getComponentName()));
        searchView.setIconified(false);
        searchView.requestFocusFromTouch();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                try {
                    int id = Integer.parseInt(query); // treat number query as an id
                    NHAPI nhapi = new NHAPI(requireContext());

                    // FIXME: 10/26/2020 activity is opened twice
                    nhapi.getComic(id, new ResponseCallback() {
                        @Override
                        public void onReponse(String response) {
                            JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
                            Gson gson = new Gson();
                            Comic c = gson.fromJson(obj, Comic.class);
                            //enter comic
                            Context activity = getActivity();
                            Intent intent = new Intent(activity, ComicActivity.class);
                            Bundle args = new Bundle();

                            intent.putExtra(ComicPresenter.ARG_ID, c.getId());
                            intent.putExtra(ComicPresenter.ARG_MID, c.getMid());
                            intent.putExtra(ComicPresenter.ARG_TITLE, c.getTitle().toString());
                            intent.putExtra(ComicPresenter.ARG_NUM_OF_PAGES, c.getNumOfPages());
                            intent.putExtra(ComicPresenter.ARG_PAGE_TYPES, c.getPageTypes());

                            activity.startActivity(intent, args);
                        }
                    });

                } catch (NumberFormatException _ignored) {
                    // do searching, not an id
                    Log.d(TAG, String.format("onClick: searching %s", query));
                    Bundle bundle = new Bundle();
                    bundle.putString(ComicListFragment.ARG_COLLECTION_NAME, "result");
                    bundle.putString(ComicListFragment.ARG_QUERY, query);
                    Navigation.findNavController(requireView()).navigate(R.id.comicListFragment, bundle);
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        super.onCreateOptionsMenu(menu, inflater);
    }

}
