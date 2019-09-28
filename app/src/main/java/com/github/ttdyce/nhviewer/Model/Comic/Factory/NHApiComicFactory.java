package com.github.ttdyce.nhviewer.Model.Comic.Factory;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;
import com.github.ttdyce.nhviewer.Model.API.ResponseCallback;

public class NHApiComicFactory implements ComicFactory {
    public static final int SORT_BY_DEFAULT = 0;
    public static final int SORT_BY_POPULAR = 1;

    private NHAPI nhapi;
    private String query;
    private int page;
    private boolean sortedPopular;
    private ResponseCallback callback;

    public NHApiComicFactory(NHAPI nhapi, String query, int page, boolean sortedPopular, ResponseCallback callback) {
        this.nhapi = nhapi;
        this.query = query;
        this.page = page;
        this.sortedPopular = sortedPopular;
        this.callback = callback;
    }

    @Override
    public void requestComicList() {
        nhapi.getComicList(query, page, sortedPopular, callback);

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
}
