package com.github.ttdyce.nhviewer.model.room;

import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "ComicBookmark", primaryKeys = {"id", "page"})
public class ComicBookmarkEntity {

    private int page;
    private int id;
    private Date dateOfCreate;

    public ComicBookmarkEntity(int page, int id) {
        this.page = page;
        this.id = id;
        this.dateOfCreate = new Date();
    }

    // Room uses this factory method to create ComicCollectionEntity objects.
    public static ComicBookmarkEntity create(int page, int id) {
        return new ComicBookmarkEntity(page, id);
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDateOfCreate() {
        return dateOfCreate;
    }

    public void setDateOfCreate(Date dateOfCreate) {
        this.dateOfCreate = dateOfCreate;
    }
}
