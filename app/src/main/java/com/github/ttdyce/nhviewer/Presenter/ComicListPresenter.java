package com.github.ttdyce.nhviewer.Presenter;

import android.content.Context;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;
import com.github.ttdyce.nhviewer.Model.Comic.Comic;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class ComicListPresenter {
    private ComicListView comicListView;

    public ComicListPresenter(final ComicListView comicListView) {
        this.comicListView = comicListView;

        NHAPI nhapi = new NHAPI(comicListView.getContext());
        ResponseCallback callback = new ResponseCallback() {
            @Override
            public void onReponse(String result) {
                JsonArray array = new JsonParser().parse(result).getAsJsonArray();
                Gson gson = new Gson();
                for (JsonElement jsonElement : array) {

                    Comic c = gson.fromJson(jsonElement, Comic.class);
                    comicListView.updateText(c.getTitle().toString());
                }

            }
        };

        nhapi.getComicList("language:chinese", true, callback);
    }

    public interface ComicListView{

        void updateText(String text);
        Context getContext();

    }
}
