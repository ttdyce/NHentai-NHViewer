package com.github.ttdyce.nhviewer.model.comic.factory;

import android.content.SharedPreferences;

import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.PopularType;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;

public class NHApiComicFactory implements ComicFactory {
    private NHAPI nhapi;
    private String query;
    private int page;
    private PopularType popularType;
    private ResponseCallback callback;
    private final SharedPreferences pref;

    public NHApiComicFactory(NHAPI nhapi, String query, int page, PopularType popularType, ResponseCallback callback, SharedPreferences pref) {
        this.nhapi = nhapi;
        this.query = query;
        this.page = page;
        this.popularType = popularType;
        this.callback = callback;
        this.pref = pref;
    }

    @Override
    public void requestComicList() {
        nhapi.getComicList(query, page, popularType, callback, pref);

    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void setSortBy(PopularType popularType) {
        this.popularType = popularType;
    }

    public static void getComicById(NHAPI api, int id, ResponseCallback callback){
        api.getComic(id, callback);
    }
}
