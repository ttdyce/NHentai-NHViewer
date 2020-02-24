package com.github.ttdyce.nhviewer.model.comic.factory;

import android.content.SharedPreferences;

import com.github.ttdyce.nhviewer.model.api.NHAPI;
import com.github.ttdyce.nhviewer.model.api.ResponseCallback;

public class NHApiComicFactory implements ComicFactory {
    public static final int SORT_BY_DEFAULT = 0;
    public static final int SORT_BY_POPULAR = 1;

    private NHAPI nhapi;
    private String query;
    private int page;
    private boolean sortedPopular;
    private ResponseCallback callback;
    private final SharedPreferences pref;

    public NHApiComicFactory(NHAPI nhapi, String query, int page, boolean sortedPopular, ResponseCallback callback, SharedPreferences pref) {
        this.nhapi = nhapi;
        this.query = query;
        this.page = page;
        this.sortedPopular = sortedPopular;
        this.callback = callback;
        this.pref = pref;
    }

    @Override
    public void requestComicList() {
        nhapi.getComicList(query, page, sortedPopular, callback, pref);

    }

    @Override
    public void setPage(int page) {
        this.page = page;
    }

    @Override
    public void setSortBy(int sortBy) {
        if(sortBy == SORT_BY_DEFAULT)
            sortedPopular = false;
        else if(sortBy == SORT_BY_POPULAR)
            sortedPopular = true;
    }

    public static void getComicById(NHAPI api, int id, ResponseCallback callback){
        api.getComic(id, callback);
    }
}
