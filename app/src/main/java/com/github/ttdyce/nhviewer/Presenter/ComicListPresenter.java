package com.github.ttdyce.nhviewer.Presenter;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.Model.Comic.Factory.ComicFactory;
import com.github.ttdyce.nhviewer.Model.Comic.Factory.DBComicFactory;
import com.github.ttdyce.nhviewer.Model.Comic.Factory.NHApiComicFactory;
import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.Model.Room.ComicCachedDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.View.ComicActivity;
import com.github.ttdyce.nhviewer.View.ComicListViewHolder;
import com.github.ttdyce.nhviewer.View.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

import java.util.ArrayList;

public class ComicListPresenter {
    private ComicFactory comicFactory;
    private DBComicFactory dbcomicFactory;
    private AppDatabase db;
    private ComicListView comicListView;
    private String collectionName, query;
    private boolean sortedPopularNow = false, hasNextPage = true;
    private int pageNow = 1;

    private ComicListAdapter adapter;
    private ResponseCallback callback;

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
                else if(array.size() < 25){
                    hasNextPage = false;
                    for (JsonElement jsonElement : array) {
                        Comic c = gson.fromJson(jsonElement, Comic.class);
                        adapter.addComic(c);
                    }
                }
                else{
                    hasNextPage = true;
                    for (JsonElement jsonElement : array) {
                        Comic c = gson.fromJson(jsonElement, Comic.class);
                        adapter.addComic(c);
                    }

                }

                comicListView.updateList(false);
            }
        };
        if (collectionName.equals("index"))
            comicFactory = new NHApiComicFactory(new NHAPI(comicListView.getContext()), query, pageNow, sortedPopularNow, callback);
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

    public void onComicItemClick(int position) {
        Context activity = comicListView.getContext();
        Intent intent = new Intent(activity, ComicActivity.class);
        Comic c = adapter.comics.get(position);
        Bundle args = new Bundle();

        intent.putExtra(ComicActivity.ARG_ID, c.getId());
        intent.putExtra(ComicActivity.ARG_MID, c.getMid());
        intent.putExtra(ComicActivity.ARG_NUM_OF_PAGES, c.getNumOfPages());
        intent.putExtra(ComicActivity.ARG_PAGE_TYPES, c.getPageTypes());

        activity.startActivity(intent, args);
    }

    public void onCollectClick(int position) {
        Comic c = adapter.comics.get(position);

        // TODO: 2019/9/26 Add to Collection "Next", hardcoded
        new AddToCollectionTask(db, comicListView, AppDatabase.COL_COLLECTION_NEXT, c).execute(c.getId());
    }

    public void onFavoriteClick(int position) {
        Comic c = adapter.comics.get(position);

        new AddToCollectionTask(db, comicListView, AppDatabase.COL_COLLECTION_FAVORITE, c).execute(c.getId());
    }

    public void onSortClick() {
        //toggle sort by popular
        setPageNow(1);
        setSortedPopularNow(!sortedPopularNow);

        adapter.clear();
        comicListView.updateList(true);

        refreshComicList();
    }

    public void onJumpToPageClick() {

    }

    public void loadNextPage() {
        setPageNow(pageNow + 1);
        if(hasNextPage)
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

    private class ComicListAdapter extends RecyclerView.Adapter<ComicListViewHolder> {
        private ArrayList<Comic> comics = new ArrayList<>();

        @NonNull
        @Override
        public ComicListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return comicListView.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicListViewHolder holder, int position) {
            Comic c = comics.get(position);
            String title = c.getTitle().toString();
            String thumbUrl = NHAPI.URLs.getThumbnail(c.getMid(), c.getImages().getThumbnail().getType());
            int numOfPages = c.getNumOfPages();

            if(c.getId() != -1)//id -1 is for empty comic collection
                comicListView.onBindViewHolder(holder, position, title, thumbUrl, numOfPages);
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

        void onBindViewHolder(ComicListViewHolder holder, int position, String title, String thumbUrl, int numOfPages);

        void updateList(Boolean isLoading);

        Context getContext();

        void showAdded(boolean isAdded, String collectionName);

    }

    private static class AddToCollectionTask extends AsyncTask<Integer, Integer, Boolean> {
        private AppDatabase db;
        private ComicListView view;
        private String collectionName;
        private Comic comic;

        public AddToCollectionTask(AppDatabase db, ComicListView view, String collectionName, Comic c) {
            this.db = db;
            this.view = view;
            this.collectionName = collectionName;
            this.comic = c;
        }

        protected Boolean doInBackground(Integer... ids) {
            ComicCachedDao cachedDao = db.comicCachedDao();
            ComicCollectionDao collectionDao = db.comicCollectionDao();
            boolean comicExist = true;
            String mid = comic.getMid(), title = comic.getTitle().toString(), pageTypesStr = TextUtils.join("", comic.getPageTypes());
            int numOfPages = comic.getNumOfPages();

            for (int id : ids) {
                //cache comic
                if (cachedDao.notExist(id)) {
                    cachedDao.insert(ComicCachedEntity.create(id, mid, title, pageTypesStr, numOfPages));
                    comicExist = false;
                }

                //insert to collection
                if (collectionDao.notExist(collectionName, id)) {
                    collectionDao.insert(ComicCollectionEntity.create(collectionName, id));
                    comicExist = false;
                }
            }

            return comicExist;
        }

        protected void onPostExecute(Boolean comicExist) {
            boolean isAdded = !comicExist;
            view.showAdded(isAdded, collectionName);
        }
    }

}
