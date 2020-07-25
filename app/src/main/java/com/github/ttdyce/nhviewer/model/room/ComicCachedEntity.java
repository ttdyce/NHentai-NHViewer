package com.github.ttdyce.nhviewer.model.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import com.google.gson.Gson;

import java.io.Serializable;

@Entity(tableName = "ComicCached", primaryKeys = {"id"})
public class ComicCachedEntity implements Serializable {
    private static final long serialVersionUID = 3001880502226771220L;
    @NonNull
    private int id;
    @NonNull
    private String mid;
    @NonNull
    private String title;
    @NonNull
    private String pageTypes;
    @NonNull
    private int numOfPages;

    public ComicCachedEntity(int id, @NonNull String mid, @NonNull String title, @NonNull String pageTypes, int numOfPages) {
        this.id = id;
        this.mid = mid;
        this.title = title;
        this.pageTypes = pageTypes;
        this.numOfPages = numOfPages;
    }

    // Room uses this factory method to create ComicCollectionEntity objects.
    public static ComicCachedEntity create(int id, String mid, String title, String pageTypes, int numOfPages) {
        return new ComicCachedEntity(id, mid, title, pageTypes, numOfPages);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @NonNull
    public String getMid() {
        return mid;
    }

    public void setMid(@NonNull String mid) {
        this.mid = mid;
    }

    @NonNull
    public String getTitle() {
        return title;
    }

    public void setTitle(@NonNull String title) {
        this.title = title;
    }

    @NonNull
    public String getPageTypes() {
        return pageTypes;
    }

    public void setPageTypes(@NonNull String pageTypes) {
        this.pageTypes = pageTypes;
    }

    public int getNumOfPages() {
        return numOfPages;
    }

    public void setNumOfPages(int numOfPages) {
        this.numOfPages = numOfPages;
    }

    public String toJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
