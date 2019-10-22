package com.github.ttdyce.nhviewer.model.comic;

import java.util.ArrayList;
import java.util.List;

public class ComicCollection {
    private String name;
    private List<Comic> comicList;

    public ComicCollection(String name, List<Comic> comicList) {
        this.name = name;
        this.comicList = comicList;
    }

    public ComicCollection(List<Integer> ids, String name) {
        this.name = name;
        this.comicList = new ArrayList<>();
        for (int id :ids) {
            if(id != -1)
                //there is at least a comic with id -1 in a empty collection
                comicList.add(new Comic(id));
        }
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
        return comicList.size() ;
    }
}
