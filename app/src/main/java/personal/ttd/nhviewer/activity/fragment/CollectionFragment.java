package personal.ttd.nhviewer.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.activity.InnerPageActivity;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.file.Storage;

public class CollectionFragment extends Fragment {
    //Data adapter
    public class CollectionsAdapter extends RecyclerView.Adapter<CollectionsAdapter.ViewHolder> {
        private final String TAG = "DisplayComicAdapter";
        private ArrayList<Comic> mDataset = new ArrayList<>();



        public CollectionsAdapter() {

        }

        public void addComic(ArrayList<Comic> comics) {
            mDataset.addAll(comics);
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public CollectionsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                                int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comics_item, parent, false);
            return new CollectionsAdapter.ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull CollectionsAdapter.ViewHolder holder, final int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Comic c = mDataset.get(position);

            //Log.i(TAG, "onBindViewHolder: position: " + position + c.getTitle());
            holder.tvTitle.setText(c.getTitle());
            Glide.with(holder.itemView.getContext())
                    .load(c.getThumbLink())
                    .into(holder.ivThumb);
            //set onClick listener
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
//                    intent.setClass(MainActivity.myContext, InnerPageActivity.class);
                    intent.setClass(getActivity(), InnerPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Comic", c);
                    getActivity().startActivity(intent);
                }
            });
            //set onLongClick listener
            holder.cvComicItem.setOnLongClickListener(new View.OnLongClickListener() {

                @Override
                public boolean onLongClick(final View v) {
                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which){
                                case DialogInterface.BUTTON_POSITIVE:
                                    //Yes button clicked
                                    if(Storage.removeCollection(position)){
                                        mDataset.remove(position);
                                        notifyDataSetChanged();
                                        Snackbar.make(v, "Collection removed", Snackbar.LENGTH_SHORT).show();
                                    }
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    //No button clicked
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                    builder.setMessage(String.format("Are you sure to remove %s?", mDataset.get(position).getTitle())).setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();

                    return true;

                }
            });
        }

        // Return the size of your dataset (invoked by the layout manager)
        @Override
        public int getItemCount() {
            return mDataset.size();
        }

        public void clear() {
            mDataset.clear();
        }

        // Provide a reference to the views for each data item
        // Complex data items may need more than one view per item, and
        // you provide access to all the views for a data item in a view holder
        public class ViewHolder extends RecyclerView.ViewHolder {
            private final Context context;
            // each data item is just a string in this case
            public CardView cvComicItem;
            public TextView tvTitle;
            public ImageView ivThumb;

            public ViewHolder(View v) {
                super(v);
                context = v.getContext();
                tvTitle = v.findViewById(R.id.tvTitle);
                ivThumb = v.findViewById(R.id.ivThumb);
                cvComicItem = v.findViewById(R.id.cvComicItem);
            }
        }


    }


    private static final String TAG = "CollectionFragment";
    private final int PAGELIMIT = 20;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private CollectionsAdapter mAdapter;
    private ProgressBar pbCollection;
    private ArrayList<Comic> comics;
    private int page = 1;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_collection, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //((AppCompatActivity)getActivity()).getSupportActionBar().setSubtitle("Collection");
        setProgressBar();
        setRefreshLayout();
        getCollections();//set this.comics
        setRecycleView();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            //do when show
            refreshRecycleView();
        }
    }

    private void setRefreshLayout() {
        mySwipeRefreshLayout = getView().findViewById(R.id.srCollection);

        mySwipeRefreshLayout.setRefreshing(true);
        mySwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        mySwipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        Log.i(TAG, "onRefresh called from SwipeRefreshLayout");
                        refreshRecycleView();
                    }
                }
        );//End RefreshListener
    }

    private void refreshRecycleView() {
        page = 1;

        mAdapter.clear();
        getCollections();
        mAdapter.addComic(getCollectionsByPage(page));
        mAdapter.notifyDataSetChanged();

    }

    private void setProgressBar() {
        pbCollection = getView().findViewById(R.id.pbCollection);
        pbCollection.setVisibility(View.VISIBLE);
    }


    private void setRecycleView() {
        RecyclerView mRecyclerView = getView().findViewById(R.id.rvCollection);

        mAdapter = new CollectionsAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.addComic(getCollectionsByPage(page));
        mAdapter.notifyDataSetChanged();

        mRecyclerView.addOnScrollListener(getEndlessScrollListener(mAdapter));
        Log.i("adapter count", "setRecycleView: adapter count: " + mAdapter.getItemCount());
        //mRecyclerView.addOnScrollListener(getEndlessScrollListener(mAdapter)  );

    }

    private void getCollections() {
        ArrayList<Comic> comics = new ArrayList<Comic>();
        JSONArray arr = null;

        try {
            arr = new JSONArray(Storage.getCollections());
            for (int i = 0; i < arr.length(); i++) {
                Comic c = new Comic();
                JSONObject obj;
                try {
                    obj = arr.getJSONObject(i);
                    c.setTitle(obj.getString("title"));
                    c.setThumbLink(obj.getString("thumblink"));
                    c.setId(obj.getString("id"));
                    c.setMid(c.getThumbLink().split("/")[c.getThumbLink().split("/").length-2]);
                    //Log.i(TAG, "getCollections: mid: " + c.getThumbLink().split("/")[c.getThumbLink().split("/").length-2]);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                comics.add(c);
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        Log.i("arr", "getCollections: arr: " + arr);


        mySwipeRefreshLayout.setRefreshing(false);
        //
        Collections.reverse(comics);
        this.comics = comics;
    }

    /*
    Starting index:  (p-1) * PAGELIMIT + 1;
    at page 1: we need (1-1)*20 = 0
    at page 2: we need (2-1)*20 = 20

    Ending index:   (startIndex + PAGELIMIT > this.comics.size()) ? this.comics.size() : startIndex + PAGELIMIT;
    at page 1: we need comics.size()-1, OR  1 + 20 = 21
    at page 1: we need comics.size()-1, OR  21 + 20 = 41
     */
    private ArrayList<Comic> getCollectionsByPage(int p) {
        int startIndex, endIndex;
        ArrayList<Comic> comics = new ArrayList<>();

        startIndex = (p - 1) * PAGELIMIT;
        endIndex = (startIndex + PAGELIMIT > this.comics.size()) ? this.comics.size() : startIndex + PAGELIMIT;
        //endIndex comic didn't show! Each time 0 ~ endIndex-1

        for (int i = startIndex; i < endIndex; i++) {
            comics.add(this.comics.get(i));
        }

        pbCollection.setVisibility(View.INVISIBLE);
        return comics;
    }


    private RecyclerView.OnScrollListener getEndlessScrollListener(final CollectionsAdapter mAdapter) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Log.i("OnScrollListener", "End of list!! bool to show: " + (comics.size() > page * PAGELIMIT));
                    if (comics.size() > page * PAGELIMIT) {
                        pbCollection.setVisibility(View.VISIBLE);
                        mAdapter.addComic(getCollectionsByPage(++page));
                        mAdapter.notifyDataSetChanged();
                    }
                }
            }
        };
    }

}

