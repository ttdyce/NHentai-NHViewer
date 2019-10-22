package com.github.ttdyce.nhviewer.presenter;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.navigation.NavController;
import androidx.recyclerview.widget.RecyclerView;

import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;
import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.model.comic.ComicCollection;
import com.github.ttdyce.nhviewer.model.room.AppDatabase;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionDao;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;
import com.github.ttdyce.nhviewer.view.ComicCollectionViewHolder;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ComicCollectionPresenter {
    private ComicCollectionView comicCollectionView;
    private AppDatabase db;
    private ComicCollectionAdapter adapter;
    private NavController navController;

    public ComicCollectionPresenter(ComicCollectionView view, NavController navController) {
        this.comicCollectionView = view;
        this.db = MainActivity.getAppDatabase();
        this.adapter = new ComicCollectionAdapter();
        this.navController = navController;

        new LoadComicCollectionTask(db, view, adapter).execute();

    }

    public ComicCollectionAdapter getAdapter() {
        return adapter;
    }

    public void onItemClick(int position) {
        String collectionName = adapter.get(position).getName();

        Bundle bundle = new Bundle();
        bundle.putString("collectionName", collectionName);

        navController.navigate(R.id.comicListFragment, bundle);
    }

    private class ComicCollectionAdapter extends RecyclerView.Adapter<ComicCollectionViewHolder> {
        private ArrayList<ComicCollection> comicCollections = new ArrayList<>();

        @NonNull
        @Override
        public ComicCollectionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return comicCollectionView.onCreateViewHolder(parent, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull final ComicCollectionViewHolder holder, final int position) {
            ComicCollection cc = comicCollections.get(position);
            final String name = cc.getName();
            final int numOfComics = cc.getComicCount();

            if (numOfComics != 0) {
                Comic latestComic = cc.getComicList().get(0);
                NHAPI nhapi = new NHAPI(comicCollectionView.getContext());
                ResponseCallback callback = new ResponseCallback() {
                    @Override
                    public void onReponse(String response) {
                        JsonObject obj = new JsonParser().parse(response).getAsJsonObject();
                        Gson gson = new Gson();
                        Comic c = gson.fromJson(obj, Comic.class);
                        final String thumbUrl = NHAPI.URLs.getThumbnail(c.getMid(), c.getImages().getThumbnail().getType());

                        if (c.getId() != -1)//id -1 is for empty comic collection
                            comicCollectionView.onBindViewHolder(holder, position, name, thumbUrl, numOfComics);
                    }
                };

                nhapi.getComic(latestComic.getId(), callback);
            }

            comicCollectionView.onBindViewHolder(holder, position, name, "", numOfComics);
        }

        @Override
        public int getItemCount() {
            return comicCollections.size();
        }

        public void add(ComicCollection cc) {
            comicCollections.add(cc);
        }

        public void clear() {
            comicCollections.clear();
        }

        public ComicCollection get(int position) {
            return comicCollections.get(position);
        }
    }

    public interface ComicCollectionView {

        ComicCollectionViewHolder onCreateViewHolder(ViewGroup parent, int viewType);

        void onBindViewHolder(ComicCollectionViewHolder holder, int position, String name, String thumbUrl, int numOfPages);

        void updateList();

        Context getContext();

    }


    private static class LoadComicCollectionTask extends AsyncTask<Void, Integer, Void> {
        private AppDatabase db;
        private ComicCollectionView view;
        private ComicCollectionAdapter adapter;

        public LoadComicCollectionTask(AppDatabase db, ComicCollectionView view, ComicCollectionAdapter adapter) {
            this.db = db;
            this.view = view;
            this.adapter = adapter;
        }

        protected Void doInBackground(Void... voids) {
            ComicCollectionDao dao = db.comicCollectionDao();
            List<ComicCollectionEntity> entities = dao.getAll();
            HashMap<String, List<Integer>> comicCollections = new HashMap<>();


            for (ComicCollectionEntity e : entities) {
                final String name = e.getName();
                int id = e.getId();

                if (comicCollections.get(name) == null)
                    comicCollections.put(name, new ArrayList<>(Collections.singletonList(id)));
                else {
                    List<Integer> ids = comicCollections.get(name);
                    ids.add(id);
                    comicCollections.put(name, ids);
                }
            }

            for (Map.Entry<String, List<Integer>> entry : comicCollections.entrySet()) {
                String name = entry.getKey();
                List<Integer> ids = entry.getValue();

                adapter.add(new ComicCollection(ids, name));
            }
            return null;
        }

        protected void onPostExecute(Void result) {
            Log.i("asyncTask", "onPostExecute: Finished task");
            view.updateList();
        }
    }
}
