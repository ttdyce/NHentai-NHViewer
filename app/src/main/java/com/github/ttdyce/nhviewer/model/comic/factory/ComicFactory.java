package com.github.ttdyce.nhviewer.model.comic.factory;

import com.github.ttdyce.nhviewer.model.api.PopularType;

public interface ComicFactory {
    void requestComicList();

    void setPage(int page);
    void setSortBy(PopularType sortBy);
}
