package personal.ttd.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.CardView;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.Model.ListReturnCallBack;
import personal.ttd.nhviewer.Controller.InnerPageActivity;
import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.Comic;
import personal.ttd.nhviewer.Model.comic.ComicTool;
import personal.ttd.nhviewer.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public abstract class ComicListDisplayerFragment extends android.support.v4.app.Fragment {

    private static final int ONE_PAGE_COMIC_COUNT = 25;
    private final String TAG = "ComicListDisplayer";
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected RecyclerView rvDisplayComic;
    protected ComicListDisplayerFragment.ComicListDisplayerAdapter adapter;
    protected SharedPreferences sharedPref;

    //provided for child class to update comics
    protected ListReturnCallBack comicListReturnCallback = new ListReturnCallBack() {
        @Override
        public void onResponse(ArrayList list) {
            if (list != null)
                adapter.setComics(list);
            adapter.notifyDataSetChanged();
            swipeRefreshLayout.setRefreshing(false);
        }
    };
    protected boolean hasPage = true;
    private int currentPage = 1;
    private boolean isSelectionMode = false;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        setSubtitle(getSubtitle());
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.content_comic_list, container, false);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_removeFromCollection:
                if (getCollectionid() != -1) {//id -1 means not allowed deletion
                    for (Comic c : adapter.selectedComics) {
                        uncollect(getCollectionid(), c);
                    }
                    toggleSelectMode();
                }
                break;

            case R.id.action_reverse:
                adapter.reverse();
                break;

            case R.id.action_jumpToPage:
                jumpToPage();
                break;

            case R.id.action_settings:

                break;
            case R.id.action_update:

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        selectMenu(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        selectMenu(menu);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        setSwipeRefreshLayout();
        setRecycleView();
        setComicList(currentPage);
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
    }

    @Override
    public void onPause() {
        super.onPause();
        resetMode();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(!isVisibleToUser)
            resetMode();
    }

    private void resetMode() {
        isSelectionMode = false;
        if(adapter != null && adapter.selectedComics != null)
            adapter.selectedComics.clear();
    }

    private void selectMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();

        if (isSelectionMode) {
            menu.clear();
            inflater.inflate(R.menu.selection_mode_comic_list, menu);
        }
    }

    private void toggleSelectMode() {
        requireActivity().invalidateOptionsMenu();
        isSelectionMode = !isSelectionMode;
    }

    private void jumpToPage() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Jump to page...");

// Set up the input
        final EditText input = new EditText(requireContext());
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

// Set up the buttons
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int page = Integer.parseInt(input.getText().toString());

                jumpToPage(page);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void jumpToPage(int page) {
        if (hasPage)
            refreshComicList(page);
        else
            rvDisplayComic.scrollToPosition((page - 1) * ONE_PAGE_COMIC_COUNT + 1);

    }

    protected abstract void setComicList(int page);

    protected abstract String getSubtitle();

    protected void setSubtitle(String subtitle) {
        if (getContext() != null)
            ((AppCompatActivity) requireContext()).getSupportActionBar().setSubtitle(subtitle);
    }

    private void setSwipeRefreshLayout() {
        swipeRefreshLayout = getView().findViewById(R.id.srBaseList);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshComicList();
                    }
                }
        );//End setting RefreshListener
    }

    protected void refreshComicList() {
        currentPage = 1;

        adapter.clear();
        setComicList(currentPage);
    }

    protected void refreshComicList(int page) {
        currentPage = page;

        adapter.clear();
        setComicList(currentPage);

    }

    private void setRecycleView() {
        rvDisplayComic = getView().findViewById(R.id.rvDisplayComic);
        adapter = new ComicListDisplayerAdapter();
        GridLayoutManager mLayoutManager = new GridLayoutManager(requireContext(), 3);
        AppBarLayout appBarLayout = (AppBarLayout) getActivity().findViewById(R.id.appBarLayout);

        rvDisplayComic.setHasFixedSize(true);
        rvDisplayComic.setLayoutManager(mLayoutManager);
        rvDisplayComic.setAdapter(adapter);

        rvDisplayComic.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                //endless scroll
                if (!recyclerView.canScrollVertically(1) && hasPage) {
                    swipeRefreshLayout.setRefreshing(true);
                    setComicList(++currentPage);
                    adapter.notifyDataSetChanged();
                }

                if (isSelectionMode && RecyclerView.SCROLL_STATE_IDLE == newState)
                    appBarLayout.setExpanded(true, true);

            }
        });

    }

    public void addFavorite(Comic comicToCollect, int position) {
        addCollection(comicToCollect, Collection.FAVARITE_ID, position);
    }

    //by position
    public void addCollection(Comic comicSelected, int collectionid, int position) {
        boolean added = false,
                collected = false;
        String collectionName = Collection.NAME_LIST.get(collectionid);
        Snackbar snackbarAdded = Snackbar.make(getView(), String.format("Added to %s", collectionName), Snackbar.LENGTH_SHORT);
        Snackbar snackbarError = Snackbar.make(getView(), "Error, comic is not added", Snackbar.LENGTH_SHORT);
        Snackbar snackbarExist = Snackbar.make(getView(), String.format("Already existed in %s", collectionName), Snackbar.LENGTH_SHORT);
        snackbarAdded.setAction("change", v -> {
            ///TODO show collection list for choosing
            Log.e(TAG, "addCollection: change clicked");
        });
        snackbarExist.setAction("uncollect", v -> {
            uncollect(collectionid, position);
        });

        collected = ComicTool.isCollected(comicSelected.getId(), collectionid);

        if (!collected) {
            added = ComicTool.collect(comicSelected, collectionid);
            if (added)
                snackbarAdded.show();
            else
                snackbarError.show();
        } else//comic collected
        {
            snackbarExist.show();
        }

    }

    //by comic
    public void addCollection(Comic comicSelected, int collectionid) {
        boolean added = false,
                collected = false;
        String collectionName = Collection.NAME_LIST.get(collectionid);
        Snackbar snackbarAdded = Snackbar.make(getView(), String.format("Added to %s", collectionName), Snackbar.LENGTH_SHORT);
        Snackbar snackbarError = Snackbar.make(getView(), "Error, comic is not added", Snackbar.LENGTH_SHORT);
        Snackbar snackbarExist = Snackbar.make(getView(), String.format("Already existed in %s", collectionName), Snackbar.LENGTH_SHORT);
        snackbarAdded.setAction("change", v -> {
            ///TODO show collection list for choosing
            Log.e(TAG, "addCollection: change clicked");
        });
        snackbarExist.setAction("uncollect", v -> {
            uncollect(collectionid, comicSelected);
        });

        collected = ComicTool.isCollected(comicSelected.getId(), collectionid);

        if (!collected) {
            added = ComicTool.collect(comicSelected, collectionid);
            if (added)
                snackbarAdded.show();
            else
                snackbarError.show();
        } else//comic collected
        {
            snackbarExist.show();
        }

    }


    protected abstract int getCollectionid();

    //by position
    private void uncollect(int collectionid, int position) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked, remove comic from collection
                    try {
                        Comic removedComic = ComicTool.uncollectByPosition(collectionid, position);
                        if (removedComic != null) {
                            refreshComicList();
                            Snackbar.make(getView(), "Comic removed", Snackbar.LENGTH_LONG)
                            .setAction("Undo", v -> addCollection(removedComic, collectionid, position))
                            .show();
                        } else
                            Snackbar.make(getView(), "Error, comic is not removed", Snackbar.LENGTH_SHORT).show();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    Toast.makeText(requireContext(), "ok, call me next time", Toast.LENGTH_SHORT).show();
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure to remove this comic?");
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        builder.show();

    }

    //by comic
    private void uncollect(int collectionid, Comic comic) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                case DialogInterface.BUTTON_POSITIVE:
                    //Yes button clicked, remove comic from collection
                    try {
                        Comic removedComic = ComicTool.uncollectByComic(collectionid, comic);
                        if (removedComic != null) {
                            refreshComicList();
                            Snackbar.make(getView(), "Comic removed", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", v -> {
                                        addCollection(removedComic, collectionid);
                                    })
                                    .show();
                        } else
                            Snackbar.make(getView(), "Error, comic not removed", Snackbar.LENGTH_SHORT).show();

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                case DialogInterface.BUTTON_NEGATIVE:
                    //No button clicked
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure to remove this comic?");
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        builder.show();

    }

    protected void favoriteButtonOnClick(Comic c, int position) {
        addFavorite(c, position);
    }

    public class ComicListDisplayerAdapter extends RecyclerView.Adapter<ComicListDisplayerAdapter.ViewHolder> {
        private final String TAG = "DisplayComicAdapter";
        private ArrayList<Comic> comics = new ArrayList<>();
        private ArrayList<Comic> selectedComics = new ArrayList<>();

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

            //select/unselect comics
            holder.cvComicItem.setCardBackgroundColor(selectedComics.contains(c) ? Color.RED : Color.WHITE);

            //set onClick listener
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (isSelectionMode) {
                        //select comic
                        if (selectedComics.contains(c))
                            unselectComic(c, position);
                        else
                            selectComic(c, position);

                        //end selection
                        if (selectedComics.isEmpty())
                            toggleSelectMode();
                    } else {
                        //open comic page by page in a activity
                        Intent intent = new Intent();
                        intent.setClass(requireActivity(), InnerPageActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.putExtra("Comic", c);

                        requireActivity().startActivity(intent);
                    }
                }
            });
            holder.cvComicItem.setLongClickable(true);
            holder.cvComicItem.setOnLongClickListener(v -> {
                toggleSelectMode();

                if (isSelectionMode)
                    selectComic(c, position);
                else
                    unselectComicAll();

                return true;
            });

            //set collect button
            holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    favoriteButtonOnClick(c, position);
                }
            });
            holder.ibCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int collectionid = Integer.parseInt(sharedPref.getString(SettingFragment.KEY_PREF_DEFAULT_COLLECTION_ID, "-1"));
                    addCollection(c, collectionid, position);
                }
            });
        }

        // invoked by the layout manager
        @Override
        public int getItemCount() {
            return comics.size();
        }

        void clear() {
            comics.clear();
        }

        public void setComics(ArrayList<Comic> comics) {
            if (this.comics != null)
                this.comics.addAll(comics);
            else
                this.comics = comics;
        }

        void reverse() {
            Collections.reverse(comics);
            notifyDataSetChanged();
        }

        void selectComic(Comic comic, int pos) {
            selectedComics.add(comic);
            notifyItemChanged(pos);
        }

        void unselectComic(Comic comic, int pos) {
            selectedComics.remove(comic);
            notifyItemChanged(pos);
        }

        void unselectComicAll() {
            notifyDataSetChanged();
            selectedComics.clear();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            CardView cvComicItem;
            TextView tvTitle;
            ImageView ivThumb;
            ImageButton ibFavorite;
            ImageButton ibCollect;

            ViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitle);
                ivThumb = v.findViewById(R.id.ivThumb);
                cvComicItem = v.findViewById(R.id.cvComicItem);
                ibFavorite = v.findViewById(R.id.ibFavorite);
                ibCollect = v.findViewById(R.id.ibCollect);
            }
        }


    }

}
