package com.github.ttdyce.nhviewer.view;

import android.content.SharedPreferences;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.presenter.ComicCollectionPresenter;

import java.util.Locale;

import jp.wasabeef.glide.transformations.SupportRSBlurTransformation;


public class ComicCollectionFragment extends Fragment implements ComicCollectionPresenter.ComicCollectionView {
    private RecyclerView rvComicList;
    private ComicCollectionPresenter presenter;
    private ContentLoadingProgressBar pbComicList;
    private TextView tvComicListDesc;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        presenter = new ComicCollectionPresenter(this, Navigation.findNavController(view));
        GridLayoutManager layoutManager = new GridLayoutManager(requireActivity(), 3);
        rvComicList = view.findViewById(R.id.rvComicList);
        pbComicList = view.findViewById(R.id.pbComicList);
        tvComicListDesc = view.findViewById(R.id.tvComicListDesc);

        tvComicListDesc.setText("Loading collection list...");

        rvComicList.setHasFixedSize(true);
        rvComicList.setAdapter(presenter.getAdapter());
        rvComicList.setLayoutManager(layoutManager);
    }

    @Override
    public ComicCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_comic_collection_list, parent, false);
        return new ComicCollectionViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ComicCollectionViewHolder holder, final int position, String name, String thumbUrl, int numOfPages) {
        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(requireContext());

        holder.tvNumOfComics.setText(String.format(Locale.ENGLISH, "%d collected", numOfPages));
        holder.tvTitle.setText(name);

        //determine blur image or not
        if (pref.getBoolean(MainActivity.KEY_PREF_DEMO_MODE, false))
            Glide.with(requireContext())
                    .load(thumbUrl)
                    .placeholder(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.secondaryColor)))
                    .apply(RequestOptions.bitmapTransform(new SupportRSBlurTransformation(16, 5)))
                    .into(holder.ivThumb);
        else
            Glide.with(requireContext())
                    .load(thumbUrl)
                    .placeholder(new ColorDrawable(ContextCompat.getColor(requireContext(), R.color.secondaryColor)))
                    .into(holder.ivThumb);

        holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                presenter.onItemClick(position);
            }
        });
    }

    @Override
    public void updateList() {
        RecyclerView.Adapter adapter = rvComicList.getAdapter();
        if (adapter.getItemCount() != 0)
            adapter.notifyDataSetChanged();

        toggleLoadingDesc(false);

    }

    private void toggleLoadingDesc(boolean loading) {
        if (loading) {
            pbComicList.show();
            tvComicListDesc.setVisibility(View.VISIBLE);
        } else {
            pbComicList.hide();
            tvComicListDesc.setVisibility(View.INVISIBLE);
        }
    }
}
