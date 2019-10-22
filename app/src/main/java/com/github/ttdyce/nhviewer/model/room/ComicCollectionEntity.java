package com.github.ttdyce.nhviewer.model.room;

import androidx.annotation.NonNull;
import androidx.room.Entity;

import java.util.Date;

@Entity(tableName = "ComicCollection", primaryKeys = {"name", "id"})
public class ComicCollectionEntity {
    @NonNull
    private String name;

    @NonNull
    private int id;

    @NonNull
    private Date dateCreated;

    public ComicCollectionEntity(@NonNull String name, @NonNull int id, @NonNull Date dateCreated) {
        this.name = name;
        this.id = id;
        this.dateCreated = dateCreated;
    }

    // Room uses this factory method to create ComicCollectionEntity objects.
    public static ComicCollectionEntity create(String name, int id, Date dateCreated) {
        return new ComicCollectionEntity(name, id, dateCreated);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public int getId() {
        return id;
    }

    public void setId(@NonNull int id) {
        this.id = id;
    }

    @NonNull
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(@NonNull Date dateCreated) {
        this.dateCreated = dateCreated;
    }
}