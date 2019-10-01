package com.github.ttdyce.nhviewer.Model.Comic.Factory;

public interface ComicFactory {
    void requestComicList();

    void setPage(int page);
    void setSortBy(int sortBy);
}
