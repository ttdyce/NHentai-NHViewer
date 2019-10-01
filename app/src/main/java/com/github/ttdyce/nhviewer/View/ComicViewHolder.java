package com.github.ttdyce.nhviewer.View;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.R;

public class ComicViewHolder extends RecyclerView.ViewHolder {
    public ImageView ivComicPage;
    public TextView tvComicPage;

    public ComicViewHolder(View v) {
        super(v);
        ivComicPage = v.findViewById(R.id.ivComicPage);
        tvComicPage = v.findViewById(R.id.tvComicPage);
    }
}
