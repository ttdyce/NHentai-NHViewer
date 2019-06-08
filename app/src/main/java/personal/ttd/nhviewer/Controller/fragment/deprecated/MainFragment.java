package personal.ttd.nhviewer.Controller.fragment.deprecated;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.Model.ListReturnCallBack;
import personal.ttd.nhviewer.Controller.InnerPageActivity;
import personal.ttd.nhviewer.Model.api.NHTranlator;
import personal.ttd.nhviewer.Model.comic.Comic;
import personal.ttd.nhviewer.Model.Saver.file.Storage;
import personal.ttd.nhviewer.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;
import static personal.ttd.nhviewer.Model.api.NHapi.userAgent;

public class MainFragment extends Fragment {

    private final String TAG = "From MainFragment";
    private final String baseUrl = "https://nhentai.net/language/chinese/";
    private final String pagePrefix = "?page=";
    public View myRoot;
    SwipeRefreshLayout mySwipeRefreshLayout;
    private int page;
    private ComicsAdapter comicsAdapter;
    private Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        // Inflate the layout for this fragment

        if (myRoot == null) {
            myRoot = inflater.inflate(R.layout.content_main, container, false);
        }
        return myRoot;

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        //((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle("Home");
        setRefreshLayout();
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

    private void setRefreshLayout() {
        mySwipeRefreshLayout = getView().findViewById(R.id.srMain);

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

        comicsAdapter.clear();
        setComics(baseUrl, page, comicsAdapter);
        comicsAdapter.notifyDataSetChanged();

    }

    private void setRecycleView() {
        page = 1;
        RecyclerView mRecyclerView = getView().findViewById(R.id.rvMain);
        ListReturnCallBack addComicsCallback = comicsResponse-> {
            comicsAdapter.addComic(comicsResponse);
            comicsAdapter.notifyDataSetChanged();
            mySwipeRefreshLayout.setRefreshing(false);
        };

        comicsAdapter = new ComicsAdapter();

        //setList(baseUrlChinese, page, comicsAdapter);

        //use api to get main page
        //comicsAdapter.addComic(NHTranlator.Companion.getMainPageComics(page));

        GridLayoutManager mLayoutManager = new GridLayoutManager(mContext, 3);

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setAdapter(comicsAdapter);

        mRecyclerView.addOnScrollListener(getEndlessScrollListener(comicsAdapter));
        registerForContextMenu(mRecyclerView);

        NHTranlator.Companion.getComicsBySite(baseUrl, String.valueOf(page), mContext, addComicsCallback);
    }

    private RecyclerView.OnScrollListener getEndlessScrollListener(final ComicsAdapter mAdapter) {
        return new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (!recyclerView.canScrollVertically(1)) {
                    Log.i(TAG, "End of list!!");
                    mySwipeRefreshLayout.setRefreshing(true);
                    setComics(baseUrl, ++page, mAdapter);
                    mAdapter.notifyDataSetChanged();
                }
            }
        };
    }

    private ArrayList<Comic> getComics(Document result) {
        ArrayList<Comic> comics;
        comics = new ArrayList<>();

        Elements galleries = result.getElementsByClass("gallery");

        int count = 1;
        for (Element gallery : galleries) {

            String title = gallery.getElementsByTag("div").get(0).text();
            String thumbLink = gallery.getElementsByTag("img").attr("data-src");
            String id = gallery.getElementsByTag("a").attr("href").split("/")[2];
            String mid = thumbLink.split("/")[thumbLink.split("/").length - 2];
            //int totalPage = gallery.getElementById("thumbnail-container").childNodeSize();

            Comic comic = new Comic();
            comic.setTitle(title);
            comic.setThumbLink(thumbLink);
            comic.setId(id);
            comic.setMid(mid);
            //comic.setTotalPage(totalPage);

            //setted comic properties
            Log.e(TAG, String.format("Finished %d gallery", count++));
            //Log.i(TAG, "setList: totalPage: " + totalPage);

            comics.add(comic);

        }
        return comics;
    }

    /*TODO use api to get main page*/
    public void setComics(String site, int page, final ComicsAdapter adapter) {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        final Document[] doc = new Document[1];

        StringRequest documentRequest = new StringRequest( //
                Request.Method.GET, //
                site + pagePrefix + page, //
                response -> {
                    //Log.i(TAG, "onResponse: " + response);
                    doc[0] = Jsoup.parse(response);

//                    adapter.addComic(setList(doc[0]));
                    adapter.addComic(getComics(doc[0]));
                    mySwipeRefreshLayout.setRefreshing(false);
                    adapter.notifyDataSetChanged();
                }, //
                error -> {
                    // Error handling
                    System.out.println("Houston we have a problem ... !");
                    error.printStackTrace();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("User-Agent", userAgent);
                return headers;
            }
        }; //

        // Add the request to the queue...
        queue.add(documentRequest);

        // ... and wait for the document.
        // NOTE: Be aware of user experience here. We don't want to freeze the app...
        queue.addRequestFinishedListener(new RequestQueue.RequestFinishedListener<Object>() {
            @Override
            public void onRequestFinished(Request<Object> request) {
                Log.i(TAG, "onRequestFinished: Finished");
            }
        });

    }

    public void addToCollection(Comic comicToCollect){
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

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        int clickedItemPosition = item.getGroupId();
        Comic c = comicsAdapter.getComicByPos(clickedItemPosition);

        switch (item.getItemId()) {
            case 1://add to collection
                addToCollection(c);

                break;

            case 2:///TODO download comic

                break;

            default:
                break;
        }

        return super.onContextItemSelected(item);
    }

    //Data adapter
    public class ComicsAdapter extends RecyclerView.Adapter<ComicsAdapter.ViewHolder> {
        private final String TAG = "ComicsAdapter";
        private ArrayList<Comic> mDataset = new ArrayList<>();

        public ComicsAdapter() {

        }

        public void addComic(ArrayList<Comic> comics) {
            mDataset.addAll(comics);
        }

        public Comic getComicByPos(int pos) {
            return mDataset.get(pos);
        }

        // Create new views (invoked by the layout manager)
        @NonNull
        @Override
        public ComicsAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                                           int viewType) {
            // create a new view
            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.comics_item, parent, false);
            return new ComicsAdapter.ViewHolder(v);
        }

        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(@NonNull ComicsAdapter.ViewHolder holder, int position) {
            // - get element from your dataset at this position
            // - replace the contents of the view with that element
            final Comic c = mDataset.get(position);

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
                    intent.setClass(getActivity(), InnerPageActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("Comic", c);
                    //intent.putExtra("comicid", c.getId());
                    getActivity().startActivity(intent);
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
        public class ViewHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
            private final Context context;
            // each data item is just a string in this case
            public CardView cvComicItem;
            public TextView tvTitle;
            public ImageView ivThumb;
            public ImageButton ibCollect;

            public ViewHolder(View v) {
                super(v);
                //param
                context = v.getContext();
                tvTitle = v.findViewById(R.id.tvTitle);
                ivThumb = v.findViewById(R.id.ivThumb);
                cvComicItem = v.findViewById(R.id.cvComicItem);
                ibCollect = v.findViewById(R.id.ibCollect);

                //
                v.setOnCreateContextMenuListener(this);
            }

            //Menu part
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,
                                            ContextMenu.ContextMenuInfo menuInfo) {

                menu.setHeaderTitle("Select The Action");//groupId, itemId, order, title
                menu.add(getAdapterPosition(), 1, 0, "Save to collection");
                menu.add(getAdapterPosition(), 2, 0, "Download");

            }


        }


    }


}

