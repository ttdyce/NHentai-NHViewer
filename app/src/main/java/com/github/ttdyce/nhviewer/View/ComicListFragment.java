package com.github.ttdyce.nhviewer.View;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.ttdyce.nhviewer.Presenter.ComicListPresenter;
import com.github.ttdyce.nhviewer.R;
import com.google.android.material.snackbar.Snackbar;

import java.util.Locale;

import static com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions.withCrossFade;

public class ComicListFragment extends Fragment implements ComicListPresenter.ComicListView {
    private static final String ARG_COLLECTION_NAME = "collectionName";
    private static final String ARG_QUERY = "query";

    private String collectionName;
    private String query;

    private RecyclerView rvComicList;
    private ComicListPresenter presenter;
    private ContentLoadingProgressBar pbComicList;
    private TextView tvComicListDesc;

    public ComicListFragment() {
        // Required empty public constructor
    }

    public static ComicListFragment newInstance(String param1, String param2) {
        ComicListFragment fragment = new ComicListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_COLLECTION_NAME, param1);
        args.putString(ARG_QUERY, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_comic_list, container, false);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        if (getArguments() == null)
            return;

        collectionName = getArguments().getString(ARG_COLLECTION_NAME);
        query = getArguments().getString(ARG_QUERY);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);
        presenter = new ComicListPresenter(this, collectionName, query);
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 3);
        rvComicList = view.findViewById(R.id.rvComicList);
        pbComicList = view.findViewById(R.id.pbComicList);
        tvComicListDesc = view.findViewById(R.id.tvComicListDesc);

        tvComicListDesc.setText("Loading from " + collectionName + "...");

        rvComicList.setHasFixedSize(true);
        rvComicList.setAdapter(presenter.getAdapter());
        rvComicList.setLayoutManager(layoutManager);

    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.app_bar_items, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                presenter.onSortClick();
                return true;
            case R.id.action_jumpToPage:
                presenter.onJumpToPageClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public ComicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic_list, parent, false);
        return new ComicListViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ComicListViewHolder holder, final int position, String title, String thumbUrl, int numOfPages) {
        //endless scroll
        if (position == rvComicList.getAdapter().getItemCount() - 1) {
            presenter.loadNextPage();
        }

        holder.tvTitle.setText(title);
        holder.tvNumOfPages.setText(String.format(Locale.ENGLISH,"%dp", numOfPages));
//        GlideApp.with(holder.itemView.getContext())
//                .load(thumbUrl)
//                .customFormat()
//                .transition(withCrossFade())
//                .into(holder.ivThumb);
        Glide.with(requireContext())
                .load(thumbUrl)
                .into(holder.ivThumb);

        holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onComicItemClick(position);
            }
        });
        holder.ibCollect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onCollectClick(position);
            }
        });
        holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onFavoriteClick(position);
            }
        });

    }

    @Override
    public void updateList() {
        RecyclerView.Adapter adapter = rvComicList.getAdapter();
        rvComicList.getAdapter().notifyDataSetChanged();

        boolean loading = false;
        if(adapter.getItemCount() == 0)
            loading = true;
        toggleLoadingDesc(loading);
    }

    private void toggleLoadingDesc(boolean loading){
        if(loading){
            pbComicList.show();
            tvComicListDesc.setVisibility(View.VISIBLE);
        }else{
            pbComicList.hide();
            tvComicListDesc.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public Context getContext() {
        return requireActivity();
    }

    @Override
    public void showAdded(boolean isAdded, String collectionName) {
        if(isAdded)
            Snackbar.make(requireView(), Html.fromHtml(String.format(Locale.ENGLISH, "Comic is added to <font color=\"yellow\">%s</font>", collectionName)), Snackbar.LENGTH_LONG).show();
        else
            Snackbar.make(requireView(), Html.fromHtml(String.format(Locale.ENGLISH, "Comic is already exist in <font color=\"red\">%s</font>", collectionName)), Snackbar.LENGTH_SHORT).show();
    }

}