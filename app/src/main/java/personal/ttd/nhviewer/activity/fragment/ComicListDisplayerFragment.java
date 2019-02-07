package personal.ttd.nhviewer.activity.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.Volley.VolleyCallback;
import personal.ttd.nhviewer.activity.InnerPageActivity;
import personal.ttd.nhviewer.api.NHTranlator;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.file.Storage;
import personal.ttd.nhviewer.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public abstract class ComicListDisplayerFragment extends android.support.v4.app.Fragment {

    private final String TAG = "ComicListDisplayer";
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ComicListDisplayerFragment.ComicListDisplayerAdapter adapter;
    //provided for child class
    protected VolleyCallback comicListReturnCallback = new VolleyCallback() {
        @Override
        public void onResponse(ArrayList<Comic> comics) {
            adapter.setComics(comics);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    protected boolean hasPage = true;
    private int currentPage = 1;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_comic_list, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setSwipeRefreshLayout();
        setRecycleView();
        setComicList(currentPage);
    }

    protected abstract void setComicList(int page);

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout = getView().findViewById(R.id.srDisplayComic);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshRecycleView();
                    }
                }
        );//End setting RefreshListener
    }

    protected void refreshRecycleView() {
        currentPage = 1;

        adapter.clear();

        setComicList(currentPage);

    }

    private void setRecycleView() {
        RecyclerView rvDisplayComic = getView().findViewById(R.id.rvDisplayComic);

        adapter = new ComicListDisplayerAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(requireContext(), 3);

        rvDisplayComic.setHasFixedSize(true);
        rvDisplayComic.setLayoutManager(mLayoutManager);
        rvDisplayComic.setAdapter(adapter);

        rvDisplayComic.addOnScrollListener(getEndlessScrollListener(adapter));

    }

    private RecyclerView.OnScrollListener getEndlessScrollListener(final ComicListDisplayerAdapter adapter) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1) && hasPage) {
                    swipeRefreshLayout.setRefreshing(true);
                    setComicList(++currentPage);
                    adapter.notifyDataSetChanged();
                }
            }
        };
    }

    public void addToCollection(Comic comicToCollect) {
        if (!Storage.isCollected(comicToCollect.getId())) {
            try {
                Storage.addCollection(comicToCollect);
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            NHTranlator.Companion.addToCollection(requireActivity(), comicToCollect);
            //Storage.insertTableCollection(c.getId(), c.getTitle(), c.getThumbLink());

            if (getView() != null)
                Snackbar.make(getView(), "Successfully saved to collection", Snackbar.LENGTH_SHORT).show();
        } else {
            if (getView() != null)
                Snackbar.make(getView(), "Already existed in collection", Snackbar.LENGTH_LONG).show();
        }
    }

    public class ComicListDisplayerAdapter extends RecyclerView.Adapter<ComicListDisplayerAdapter.ViewHolder> {
        private final String TAG = "DisplayComicAdapter";
        private ArrayList<Comic> comics = new ArrayList<>();

        public void addComic(ArrayList<Comic> comics) {
            this.comics.addAll(comics);
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ComicListDisplayerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                                       int viewType) {
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comics_item, parent, false);
            return new ComicListDisplayerAdapter.ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(ComicListDisplayerAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Comic c = comics.get(position);

            holder.tvTitle.setText(c.getTitle());

            GlideApp.with(holder.itemView.getContext())
                    .load(c.getThumbLink())
                    .customFormat()
                    .transition(withCrossFade())
                    .into(holder.ivThumb);

            //set onClick listener
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    intent.setClass(requireActivity(), InnerPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Comic", c);
                    requireActivity().startActivity(intent);
                }
            });
            holder.cvComicItem.setLongClickable(true);

            //set collect button
            holder.ibCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addToCollection(c);
                }
            });
        }

        // invoked by the layout manager
        @Override
        public int getItemCount() {
            return comics.size();
        }

        public void clear() {
            comics.clear();
        }

        public void setComics(ArrayList<Comic> comics) {
            if (this.comics != null)
                this.comics.addAll(comics);
            else
                this.comics = comics;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            public CardView cvComicItem;
            public TextView tvTitle;
            public ImageView ivThumb;
            public ImageButton ibCollect;

            public ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                ivThumb = v.findViewById(R.id.ivThumb);
                cvComicItem = v.findViewById(R.id.cvComicItem);
                ibCollect = v.findViewById(R.id.ibCollect);
            }
        }


    }

}
