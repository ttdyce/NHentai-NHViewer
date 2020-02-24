package com.github.ttdyce.nhviewer.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;
import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.model.comic.factory.ComicFactory;
import com.github.ttdyce.nhviewer.model.comic.factory.DBComicFactory;
import com.github.ttdyce.nhviewer.model.comic.factory.NHApiComicFactory;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCachedDao;
import com.github.ttdyce.nhviewer.model.room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.view.ComicActivity;
import com.github.ttdyce.nhviewer.view.ComicListViewHolder;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Date;

public class ComicListPresenter {
    private String collectionName, query;
    private boolean sortedPopularNow = false, hasNextPage = true;
    private int pageNow = 1;
    private boolean selectionMode = false;

    private ComicFactory comicFactory;
    private AppDatabase db;
    private ComicListView comicListView;
    private ComicListAdapter adapter;
    private ResponseCallback callback;
    private ArrayList<Comic> selectedComics = new ArrayList<>();
    private ArrayList<View> selectedSelectors = new ArrayList<>();

    public ComicListPresenter(final ComicListView comicListView, String collectionName, String query) {
        this.collectionName = collectionName;
        this.query = query;
        this.db = MainActivity.getAppDatabase();
        this.comicListView = comicListView;
        this.adapter = new ComicListAdapter();
        this.callback = new ResponseCallback() {
            @Override
            public void onReponse(String result) {
                JsonArray array = new JsonParser().parse(result).getAsJsonArray();
                Gson gson = new Gson();

                if (array.size() == 0)
                    hasNextPage = false;
                else if (array.size() != 25) {
                    hasNextPage = false;
                    for (JsonElement jsonElement : array) {
                        Comic c = gson.fromJson(jsonElement, Comic.class);
                        adapter.addComic(c);
                    }
                } else {
                    hasNextPage = true;
                    for (JsonElement jsonElement : array) {
                        Comic c = gson.fromJson(jsonElement, Comic.class);
                        adapter.addComic(c);
                    }

                }

                // TODO: 2019/10/1 hardcoded to stop loading if incoming comics[0] == current[0]
                if (pageNow > 1 &&
                        hasNextPage &&
                        array.get(0).getAsJsonObject().get("id").getAsString().equals(String.valueOf(adapter.comics.get(0).getId()))) {
                    hasNextPage = false;
                }

                comicListView.updateList(false);
            }
        };
        if (collectionName.equals("index") || collectionName.equals("result"))// TODO: 2019/10/25 improve collection name checking
            comicFactory = new NHApiComicFactory(new NHAPI(comicListView.getRequiredActivity()), query, pageNow, sortedPopularNow, callback, PreferenceManager.getDefaultSharedPreferences(comicListView.getRequiredActivity()));
        else
            comicFactory = new DBComicFactory(collectionName, db, pageNow, sortedPopularNow, callback);

        refreshComicList();
    }

    public ComicListAdapter getAdapter() {
        return adapter;
    }

    private void refreshComicList() {
        comicFactory.requestComicList();
    }

    private void onComicItemClick(int position) {
        Comic c = adapter.comics.get(position);

        if (selectionMode) {
            //toggle select comic
            if (!selectedComics.contains(c))
                selectedComics.add(c);
            else
                selectedComics.remove(c);

            //exit selection mode if selected comics is empty
            if (selectedComics.size() == 0)
                onSelectionDone();
        } else {
            //enter comic
            Context activity = comicListView.getRequiredActivity();
            Intent intent = new Intent(activity, ComicActivity.class);
            Bundle args = new Bundle();

            intent.putExtra(ComicPresenter.ARG_ID, c.getId());
            intent.putExtra(ComicPresenter.ARG_MID, c.getMid());
            intent.putExtra(ComicPresenter.ARG_TITLE, c.getTitle().toString());
            intent.putExtra(ComicPresenter.ARG_NUM_OF_PAGES, c.getNumOfPages());
            intent.putExtra(ComicPresenter.ARG_PAGE_TYPES, c.getPageTypes());

            activity.startActivity(intent, args);
        }
    }

    private void onCollectClick(int position) {
        Comic c = adapter.comics.get(position);

        // TODO: 2019/9/26 Add to Collection "Next", hardcoded
        new EditCollectionComicTask(db, comicListView, AppDatabase.COL_COLLECTION_NEXT, c).execute();
    }

    private void onFavoriteClick(int position) {
        Comic c = adapter.comics.get(position);

        new EditCollectionComicTask(db, comicListView, AppDatabase.COL_COLLECTION_FAVORITE, c).execute();
    }

    private void onSortClick() {
        //toggle sort by popular
        setPageNow(1);
        setSortedPopularNow(!sortedPopularNow);

        adapter.clear();

        comicListView.updateList(true);
        refreshComicList();
    }

    private void onJumpToPageClick() {

    }

    private void onSelectionClick() {
        //toggle selection mode
        setSelectionMode(true);

    }

    public void setSelectionMode(boolean value) {
        selectionMode = value;
        comicListView.getRequiredActivity().invalidateOptionsMenu();
    }

    private void onDeleteClick() {
        if (selectedComics.size() != 0 && !collectionName.equals("index"))
            for (Comic c : selectedComics) {
                new EditCollectionComicTask(db, comicListView, collectionName, c, EditCollectionComicTask.Action.delete, this).execute();
            }

    }

    private void onSelectionDone() {
        comicListView.onSelectionDone(selectedSelectors);

        setSelectionMode(false);
        selectedSelectors.clear();
        selectedComics.clear();
        adapter.notifyDataSetChanged();//update selector display
    }

    public void loadNextPage() {
        setPageNow(pageNow + 1);
        if (hasNextPage)
            refreshComicList();
    }

    private void setSortedPopularNow(boolean sortedPopularNow) {
        this.sortedPopularNow = sortedPopularNow;
        if (sortedPopularNow)
            comicFactory.setSortBy(NHApiComicFactory.SORT_BY_POPULAR);
        else
            comicFactory.setSortBy(NHApiComicFactory.SORT_BY_DEFAULT);
    }

    private void setPageNow(int pageNow) {
        this.pageNow = pageNow;
        comicFactory.setPage(pageNow);
    }

    public boolean inSelectionMode() {
        return selectionMode;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_sort:
                onSortClick();
                return true;
            case R.id.action_jumpToPage:
                onJumpToPageClick();
                return true;
            case R.id.action_selection:
                onSelectionClick();
                return true;
            case R.id.action_delete:
                onDeleteClick();
                onSelectionDone();
                return true;
            case R.id.action_done:
                onSelectionDone();
                return true;
        }
        return false;
    }

    public boolean cannotDelete() {
        if (collectionName.equals("index"))
            return true;
        return false;
    }


    private class ComicListAdapter extends RecyclerView.Adapter<ComicListViewHolder> {
        private ArrayList<Comic> comics = new ArrayList<>();

        @NonNull
        @Override
        public ComicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return comicListView.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull final ComicListViewHolder holder, final int position) {
            final Comic c = comics.get(position);
            String title = c.getTitle().toString();
            String thumbUrl = NHAPI.URLs.getThumbnail(c.getMid(), c.getImages().getThumbnail().getType());
            int numOfPages = c.getNumOfPages();

            //bind view
            if (c.getId() != -1)//id -1 is for empty comic collection
                comicListView.onBindViewHolder(holder, position, title, thumbUrl, numOfPages, selectedComics.contains(c), holder.cvComicItem);

            //endless scroll
            if (position == adapter.getItemCount() - 1) {
                loadNextPage();
            }

            //on click
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onComicItemClick(position);
                    comicListView.onComicItemClick(v, selectedComics.contains(c), selectedSelectors);
                }
            });
            holder.tvTitle.setOnClickListener(new View.OnClickListener() {
                //let click on title = click on thumbnail
                @Override
                public void onClick(View v) {
                    onComicItemClick(position);
                    comicListView.onComicItemClick(holder.cvComicItem, selectedComics.contains(c), selectedSelectors);
                }
            });
            holder.ibCollect.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onCollectClick(position);
                }
            });
            holder.ibFavorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onFavoriteClick(position);
                }
            });

            //long press
            holder.cvComicItem.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    setSelectionMode(!selectionMode);
                    if (inSelectionMode()) {
                        onComicItemClick(position);
                        comicListView.onComicItemClick(v, selectedComics.contains(c), selectedSelectors);

                    } else {
                        onSelectionDone();
                    }

                    return true;
                }
            });
            holder.tvTitle.setOnLongClickListener(new View.OnLongClickListener() {
                //let long press on title = long press on thumbnail
                @Override
                public boolean onLongClick(View v) {
                    setSelectionMode(!selectionMode);
                    if (inSelectionMode()) {
                        onComicItemClick(position);
                        comicListView.onComicItemClick(holder.cvComicItem, selectedComics.contains(c), selectedSelectors);

                    } else {
                        onSelectionDone();
                    }

                    return true;
                }
            });
        }

        @Override
        public int getItemCount() {
            return comics.size();
        }

        public void addComic(Comic c) {
            comics.add(c);
        }

        public void clear() {
            comics.clear();
        }
    }

    public interface ComicListView {

        ComicListViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicListViewHolder holder, int position, String title, String thumbUrl, int numOfPages, Boolean isSelected, View v);

        void updateList(Boolean isLoading);

        FragmentActivity getRequiredActivity();

        void showAdded(boolean isAdded, String collectionName);

        void showDeleted(Boolean isDone, String title, String collectionName);

        void onComicItemClick(View v, boolean isSelected, ArrayList<View> selectors);

        void onSelectionDone(ArrayList<View> selectedSelectors);

    }

    public static class EditCollectionComicTask extends AsyncTask<Void, Integer, Boolean> {

        public enum Action {insert, delete}

        private ComicListPresenter presenter;
        private AppDatabase db;
        private ComicListView view;
        private String collectionName;
        private Comic comic;
        private Action action;

        //for comic presenter
        public EditCollectionComicTask(AppDatabase db, String collectionName, Comic c) {
            this.db = db;
            this.collectionName = collectionName;
            this.comic = c;
            this.action = Action.insert;
        }

        public EditCollectionComicTask(AppDatabase db, ComicListView view, String collectionName, Comic c, Action action, ComicListPresenter presenter) {
            this.db = db;
            this.view = view;
            this.collectionName = collectionName;
            this.comic = c;
            this.action = action;
            this.presenter = presenter;
        }

        public EditCollectionComicTask(AppDatabase db, ComicListView view, String collectionName, Comic c) {
            this.db = db;
            this.view = view;
            this.collectionName = collectionName;
            this.comic = c;
            this.action = Action.insert;
        }

        protected Boolean doInBackground(Void... value) {
            ComicCachedDao cachedDao = db.comicCachedDao();
            ComicCollectionDao collectionDao = db.comicCollectionDao();
            boolean comicExist = true;
            String mid = comic.getMid(), title = comic.getTitle().toString(), pageTypesStr = TextUtils.join("", comic.getPageTypes());
            int id = comic.getId(), numOfPages = comic.getNumOfPages();
            //cache comic
            if (cachedDao.notExist(id)) {
                cachedDao.insert(ComicCachedEntity.create(id, mid, title, pageTypesStr, numOfPages));
                comicExist = false;
            }

            if (action == Action.insert) {
                //insert to collection
                if (collectionDao.notExist(collectionName, id)) {
                    collectionDao.insert(ComicCollectionEntity.create(collectionName, id, new Date()));
                    comicExist = false;
                } else if (collectionName.equals(AppDatabase.COL_COLLECTION_HISTORY)) {
                    collectionDao.update(ComicCollectionEntity.create(collectionName, id, new Date()));
                }

                return !comicExist;

            } else if (action == Action.delete) {
                //delete from collection
                boolean deleted = false;
                collectionDao.delete(ComicCollectionEntity.create(collectionName, id, new Date()));

                if (collectionDao.notExist(collectionName, id))
                    deleted = true;
                return deleted;
            }

            return false;
        }

        protected void onPostExecute(Boolean isDone) {
            if (action == Action.insert && view != null)//null if called from ComicPresenter
                view.showAdded(isDone, collectionName);
            else if (action == Action.delete) {
                view.showDeleted(isDone, comic.getTitle().toString(), collectionName);
                presenter.adapter.clear();// TODO: 2019/10/8 consider control the adding of comics, not clearing all comics
                presenter.refreshComicList();
            }
        }
    }

}
