package com.github.ttdyce.nhviewer.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;
import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.model.comic.factory.NHApiComicFactory;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicBookmarkDao;
import com.github.ttdyce.nhviewer.model.room.ComicBookmarkEntity;
import com.github.ttdyce.nhviewer.view.ComicViewHolder;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.List;

public class ComicPresenter {
    public static final String ARG_ID = "id";
    public static final String ARG_MID = "mid";
    public static final String ARG_TITLE = "title";
    public static final String ARG_NUM_OF_PAGES = "numOfPages";
    public static final String ARG_PAGE_TYPES = "pageTypes";

    private ComicView view;
    private final AppDatabase db;
    private ComicAdapter adapter;
    private Comic comic;

    public ComicPresenter(ComicView view, int id, String mid, String title, int numOfPages, String[] types) {
        this.view = view;
        this.db = MainActivity.getAppDatabase();
        this.comic = new Comic(id, mid, new Comic.Title(title), numOfPages, types);

        String[] urls = new String[numOfPages];
        for (int i = 0; i < numOfPages; i++) {
            urls[i] = NHAPI.URLs.getPage(mid, i + 1, types[i]);
        }
        this.adapter = new ComicAdapter(urls);

        addToHistory();
    }

    private void addToHistory() {
        new ComicListPresenter.EditCollectionComicTask(db, AppDatabase.COL_COLLECTION_HISTORY, comic).execute();
        new ComicBookmarkTask(db, view.getLastVisibleItemPosition(), comic, view, ComicBookmarkTask.Action.select).execute();

    }

    public RecyclerView.Adapter getAdapter() {
        return adapter;
    }

    // process id from browser || id from comic list fragment
    public static ComicPresenter factory(final Context context, final ComicView comicView, Bundle extras, int idFromBrowser, final RecyclerView rvComic) {
        if (idFromBrowser != -1) {
            NHApiComicFactory.getComicById(new NHAPI(context), idFromBrowser, new ResponseCallback() {
                @Override
                public void onReponse(String response) {
                    JsonObject object = new JsonParser().parse(response).getAsJsonObject();
                    Gson gson = new Gson();
                    Comic c = gson.fromJson(object, Comic.class);

                    int id = c.getId();
                    String mid = c.getMid();
                    String title = c.getTitle().toString();
                    int numOfPages = c.getNumOfPages();
                    String[] pageTypes = c.getPageTypes();

                    ComicPresenter presenter = new ComicPresenter(comicView, id, mid, title, numOfPages, pageTypes);
                    rvComic.setAdapter(presenter.getAdapter());
                }
            });

        } else {
            int id = extras.getInt(ARG_ID);
            String mid = extras.getString(ARG_MID);
            String title = extras.getString(ARG_TITLE);
            int numOfPages = extras.getInt(ARG_NUM_OF_PAGES);
            String[] pageTypes = extras.getStringArray(ARG_PAGE_TYPES);
            ComicPresenter presenter = new ComicPresenter(comicView, id, mid, title, numOfPages, pageTypes);
            rvComic.setAdapter(presenter.getAdapter());

            return presenter;
        }

        return null;//non reachable
    }

    public void onStop() {
        int lastVisibleItemPosition = view.getLastVisibleItemPosition();
        if (lastVisibleItemPosition > 1)
            new ComicBookmarkTask(db, lastVisibleItemPosition, comic, view, ComicBookmarkTask.Action.insert).execute();
    }

    private class ComicAdapter extends RecyclerView.Adapter<ComicViewHolder> {
        private String[] pagesUrl;

        public ComicAdapter(String[] urls) {
            pagesUrl = urls;

        }

        @NonNull
        @Override
        public ComicViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return view.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull ComicViewHolder holder, int position) {
            String url = pagesUrl[position];

            view.onBindViewHolder(holder, position, url);
        }

        @Override
        public int getItemCount() {
            return pagesUrl.length;
        }
    }

    public interface ComicView {

        ComicViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicViewHolder holder, int position, String url);

        int getLastVisibleItemPosition();

        View getRootView();

        RecyclerView getRVComic();
    }


    public static class ComicBookmarkTask extends AsyncTask<Void, Integer, Boolean> {

        public enum Action {insert, select, delete}

        private AppDatabase db;
        private int page;
        private Comic comic;
        private ComicView view;
        private Action action;

        //default insert
        public ComicBookmarkTask(AppDatabase db, int page, Comic c, ComicView view) {
            this.db = db;
            this.page = page;
            this.comic = c;
            this.view = view;
            this.action = Action.insert;
        }

        public ComicBookmarkTask(AppDatabase db, int page, Comic c, ComicView view, Action action) {
            this(db, page, c, view);
            this.action = action;
        }

        protected Boolean doInBackground(Void... value) {
            ComicBookmarkDao bookmarkDao = db.comicBookmarkDao();
            int id = comic.getId();

            switch (action) {
                case insert:
                    if (bookmarkDao.notExist(id, page)) {
                        bookmarkDao.insert(ComicBookmarkEntity.create(page, id));
                    } else {
                        bookmarkDao.update(ComicBookmarkEntity.create(page, id));
                    }
                    return true;

                case select:
                    List<ComicBookmarkEntity> bookmarks = bookmarkDao.getById(id);
                    if (bookmarks.size() > 0) {
                        page = bookmarks.get(0).getPage();
                        return true;
                    }

                case delete:

                    break;
            }


//            ComicCachedDao cachedDao = db.comicCachedDao();
//            ComicCollectionDao collectionDao = db.comicCollectionDao();
//            boolean comicExist = true;
//            String mid = comic.getMid(), title = comic.getTitle().toString(), pageTypesStr = TextUtils.join("", comic.getPageTypes());
//            int id = comic.getId(), numOfPages = comic.getNumOfPages();
//            //cache comic
//            if (cachedDao.notExist(id)) {
//                cachedDao.insert(ComicCachedEntity.create(id, mid, title, pageTypesStr, numOfPages));
//                comicExist = false;
//            }
//
//            if (action == ComicListPresenter.EditCollectionComicTask.Action.insert) {
//                //insert to collection
//                if (collectionDao.notExist(collectionName, id)) {
//                    collectionDao.insert(ComicCollectionEntity.create(collectionName, id, new Date()));
//                    comicExist = false;
//                } else if (collectionName.equals(AppDatabase.COL_COLLECTION_HISTORY)) {
//                    collectionDao.update(ComicCollectionEntity.create(collectionName, id, new Date()));
//                }
//
//                return !comicExist;
//
//            } else if (action == ComicListPresenter.EditCollectionComicTask.Action.delete) {
//                //delete from collection
//                boolean deleted = false;
//                collectionDao.delete(ComicCollectionEntity.create(collectionName, id, new Date()));
//
//                if (collectionDao.notExist(collectionName, id))
//                    deleted = true;
//                return deleted;
//            }

            return false;
        }

        protected void onPostExecute(Boolean isDone) {
            final int seenPage = page;

            switch (action) {
                case insert:
                    // not showing anything when saving bookmark
                    Snackbar snackbarSaved = Snackbar.make(view.getRootView(), String.format("Saved seen page %s", seenPage), Snackbar.LENGTH_SHORT);
                    snackbarSaved.show();
                    break;

                case select:
                    /*
                     * check if last seen page exist.
                     * if exist, show a Snackbar with a button "Go to page"
                     * */
                    Snackbar snackbarSeen = Snackbar.make(view.getRootView(), String.format("You have seen page %s", seenPage), Snackbar.LENGTH_LONG)
                            .setAction("Go to page", new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    view.getRVComic().scrollToPosition(seenPage - 1);
                                }
                            });

                    if (seenPage > 0)
                        snackbarSeen.show();
                    break;

                case delete:

                    break;
            }
        }
    }
}
