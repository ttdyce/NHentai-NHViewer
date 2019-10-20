package com.github.ttdyce.nhviewer.model.comic.factory;

public interface ComicFactory {
    void requestComicList();

    void setPage(int page);
    void setSortBy(int sortBy);
}
