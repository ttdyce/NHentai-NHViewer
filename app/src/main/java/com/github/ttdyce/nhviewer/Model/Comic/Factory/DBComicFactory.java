package com.github.ttdyce.nhviewer.Model.Comic.Factory;

import android.os.AsyncTask;

import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.github.ttdyce.nhviewer.Model.Room.AppDatabase;
import com.github.ttdyce.nhviewer.Model.Room.ComicCachedDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCachedEntity;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.Model.Room.ComicCollectionEntity;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class DBComicFactory implements ComicFactory {
    public static final int SORT_BY_DEFAULT = 0;
    public static final int SORT_BY_COLLECTION_DATE = 1;
    public static final int SORT_BY_COLLECTION_DATE_DESC = -1;

    private String collectionName;
    private AppDatabase db;
    private int page;
    private boolean sortedPopular;
    private ResponseCallback callback;

    public DBComicFactory(String collectionName, AppDatabase db, int page, boolean sortedPopular, ResponseCallback callback) {
        this.collectionName = collectionName;
        this.db = db;
        this.page = page;
        this.sortedPopular = sortedPopular;
        this.callback = callback;
    }

    @Override
    public void requestComicList() {
        new DisplayCollectionTask(db, collectionName, callback).execute();

        // TODO: 2019/9/28 replaced by asyncTask
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                ComicCachedDao cachedDao = db.comicCachedDao();
//                ComicCollectionDao collectionDao = db.comicCollectionDao();
//                List<Integer> ids = new ArrayList<>();
//                List<Comic> comics = new ArrayList<>();
//
//                for (ComicCollectionEntity entity:collectionDao.getAllByName(collectionName))
//                    ids.add(entity.getId());
//
//                for (ComicCachedEntity entity: cachedDao.findById(ids)) {
//                    int id = entity.getId(), numOfPages = entity.getNumOfPages();
//                    String mid = entity.getMid();
//                    Comic.Title title = new Comic.Title(entity.getTitle());
//                    String[] pageTypes = entity.getPageTypes().split("(?!^)");
//                    comics.add(new Comic(id, mid, title, numOfPages, pageTypes));
//                }
//                //create api-like json object
//                Gson gson = new Gson();
//                JsonObject jsonObject = new JsonObject();
//                jsonObject.add("results", new JsonParser().parse(gson.toJson(comics)).getAsJsonArray());
//
//                callback.onReponse(gson.toJson(comics));
//
//            }
//        }).start();

    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void setSortBy(int sortBy) {
        // TODO: 2019/9/28 Not yet supported sorting in Collection
    }

    private static class DisplayCollectionTask extends AsyncTask<Void, Void, String>{

        private AppDatabase db;
        private String collectionName;
        private ResponseCallback callback;

        public DisplayCollectionTask(AppDatabase db, String collectionName, ResponseCallback callback) {
            this.db = db;
            this.collectionName = collectionName;
            this.callback = callback;
        }

        @Override
        protected String doInBackground(Void... voids) {

            ComicCachedDao cachedDao = db.comicCachedDao();
            ComicCollectionDao collectionDao = db.comicCollectionDao();
            List<Integer> ids = new ArrayList<>();
            List<Comic> comics = new ArrayList<>();

            for (ComicCollectionEntity entity:collectionDao.getAllByName(collectionName))
                ids.add(entity.getId());

            for (ComicCachedEntity entity: cachedDao.findById(ids)) {
                int id = entity.getId(), numOfPages = entity.getNumOfPages();
                String mid = entity.getMid();
                Comic.Title title = new Comic.Title(entity.getTitle());
                String[] pageTypes = entity.getPageTypes().split("(?!^)");
                comics.add(new Comic(id, mid, title, numOfPages, pageTypes));
            }
            //create api-like json object
            Gson gson = new Gson();

            return gson.toJson(comics);
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            callback.onReponse(result);
        }
    }
}
