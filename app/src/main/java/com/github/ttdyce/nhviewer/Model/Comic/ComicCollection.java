package com.github.ttdyce.nhviewer.Model.Comic;

import java.util.List;

public class ComicCollection {
    String name;
    List<Comic> comicList;

    public ComicCollection(String name, List<Comic> comicList) {
        this.name = name;
        this.comicList = comicList;
    }

    public int getComicCount(){
        return comicList.size();
    }
}
