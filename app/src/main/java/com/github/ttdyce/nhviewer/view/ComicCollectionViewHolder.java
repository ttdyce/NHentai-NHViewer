package com.github.ttdyce.nhviewer.view;


import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.R;

public class ComicCollectionViewHolder extends RecyclerView.ViewHolder {
    public CardView cvComicItem;
    public TextView tvTitle;
    public TextView tvNumOfComics;
    public ImageView ivThumb;

    public ComicCollectionViewHolder(View v) {
        super(v);
        tvTitle = v.findViewById(R.id.tvComicListItem);
        tvNumOfComics= v.findViewById(R.id.tvNumOfComics);
        ivThumb = v.findViewById(R.id.ivComicListItem);
        cvComicItem = v.findViewById(R.id.cvComicListItem);
    }
}