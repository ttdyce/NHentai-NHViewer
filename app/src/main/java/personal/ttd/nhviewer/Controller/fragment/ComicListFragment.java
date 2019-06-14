package personal.ttd.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.Controller.InnerPageActivity;
import personal.ttd.nhviewer.Controller.fragment.base.BaseListFragment;
import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.Comic;
import personal.ttd.nhviewer.Model.comic.ComicTool;

public abstract class ComicListFragment extends BaseListFragment {

    protected ComicListAdapter adapter = new ComicListAdapter();

    @Override
    protected abstract String getActionBarTitle();

    @Override
    protected abstract boolean getCanDelete();

    @Override
    protected abstract void setList(int page);

    @Override
    protected BaseListAdapter getAdapter() {
        return adapter;
    }


    @Override
    protected boolean getIsUsingFab() {
        return false;
    }

    public void addCollection(Comic comicSelected, int collectionid) {
        boolean added = false, collected = false;
        String collectionName = Collection.NAME_LIST.get(collectionid);

        Spanned addedText = Html.fromHtml(String.format("Added to <font color=\"#ffff00\">%s</font>", collectionName));
        Spanned errorText = Html.fromHtml("<font color=\"#d50000\">Error</font>, comic is not added");
        Spanned existText = Html.fromHtml(String.format("Already existed in <font color=\"#ffff00\">%s</font>", collectionName));

        Snackbar snackbarAdded = Snackbar.make(getView(), addedText, Snackbar.LENGTH_SHORT);
        Snackbar snackbarError = Snackbar.make(getView(), errorText, Snackbar.LENGTH_SHORT);
        Snackbar snackbarExist = Snackbar.make(getView(), existText, Snackbar.LENGTH_SHORT);
        snackbarAdded.setAction("change", v -> {
            // TODO: 6/10/2019 Allow pop up menu for changing collect location
            //Log.e(TAG, "addCollection: change clicked");
        });
        snackbarExist.setAction("uncollect", v -> uncollect(collectionid, comicSelected));

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

    private void uncollect(int collectionid, Comic comic) {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            switch (which) {
                //Yes button clicked, remove comic from collection
                case DialogInterface.BUTTON_POSITIVE:
                    try {
                        Comic removedComic = ComicTool.uncollectByComic(collectionid, comic);
                        if (removedComic != null) {
                            refreshRecyclerView(1);
                            Snackbar.make(getView(), "Comic removed", Snackbar.LENGTH_LONG)
                                    .setAction("Undo", v -> addCollection(removedComic, collectionid))
                                    .show();
                        } else{
                            Snackbar.make(getView(), "Error, comic not removed", Snackbar.LENGTH_SHORT).show();
                        }

                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    }

                    break;

                //No button clicked, do nothing
                case DialogInterface.BUTTON_NEGATIVE:
                    break;
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setMessage("Are you sure to remove this comic?");
        builder.setPositiveButton("Yes", dialogClickListener);
        builder.setNegativeButton("No", dialogClickListener);
        builder.show();

    }

    //Data adapter
    private class ComicListAdapter extends BaseListAdapter {
        ArrayList<Comic> comics = new ArrayList<>();

        @Override
        public BaseListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View v = (View) LayoutInflater.from(parent.getContext())
                    .inflate(getListItemLayout(), parent, false);
            return new ComicListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(BaseListViewHolder holder, int position) {
            super.onBindViewHolder(holder, position);

            final Comic c = comics.get(position);
            ComicListViewHolder comicListHolder = (ComicListViewHolder) holder;

            //set favorite button
            comicListHolder.ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addCollection(c, Collection.FAVARITE_ID);
                }
            });

            //set collect button
            comicListHolder.ibCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int collectionid = Integer.parseInt(sharedPref.getString(SettingFragment.KEY_PREF_DEFAULT_COLLECTION_ID, "-1"));
                    addCollection(c, collectionid);
                }
            });
        }

        @Override
        protected int getListItemLayout() {
            return R.layout.list_item_comic;
        }

        @Override
        protected ArrayList getDataList() {
            return comics;
        }

        @Override
        protected void onListItemClick(int position) {
            //open comic page by page in a activity
            Intent intent = new Intent();

            intent.setClass(requireActivity(), InnerPageActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("Comic", comics.get(position));

            requireActivity().startActivity(intent);

        }

        @Override
        protected String getThumbLink(int position) {
            Comic comic = comics.get(position);

            return comic.getThumbLink();
        }

        @Override
        protected String getTitle(int position) {
            Comic comic = comics.get(position);

            return comic.getTitle();
        }

        @Override
        public void clear() {
            comics.clear();
        }

        @Override
        public void addList(ArrayList list) {
            comics.addAll(list);
        }

        @Override
        public int getItemCount() {
            return comics.size();
        }

        public void reverse() {
            Collections.reverse(comics);
        }


        class ComicListViewHolder extends BaseListViewHolder {
            ImageButton ibFavorite;
            ImageButton ibCollect;

            ComicListViewHolder(View v) {
                super(v);
                ibFavorite = v.findViewById(R.id.ibFavorite);
                ibCollect = v.findViewById(R.id.ibCollect);
            }
        }
    }

}

