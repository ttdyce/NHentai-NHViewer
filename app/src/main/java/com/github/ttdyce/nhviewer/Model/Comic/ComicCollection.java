package com.github.ttdyce.nhviewer.Model.Comic;

import java.util.List;

public class ComicCollection {
    private String name;
    private List<Comic> comicList;

    public ComicCollection(String name, List<Comic> comicList) {
        this.name = name;
        this.comicList = comicList;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Comic> getComicList() {
        return comicList;
    }

    public void setComicList(List<Comic> comicList) {
        this.comicList = comicList;
    }

    public int getComicCount(){
        return comicList.size();
    }
}
