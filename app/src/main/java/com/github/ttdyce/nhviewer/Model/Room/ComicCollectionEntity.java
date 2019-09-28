package com.github.ttdyce.nhviewer.Model.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "ComicCollection", primaryKeys = {"name","id"})
public class ComicCollectionEntity {
    @NonNull
    private String name;

    @NonNull
    private int id;

    public ComicCollectionEntity(@NonNull String name, @NonNull int id) {
        this.name = name;
        this.id = id;
    }
    // Room uses this factory method to create ComicCollectionEntity objects.
    public static ComicCollectionEntity create(String name, int id) {
        return new ComicCollectionEntity(name, id);
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
}
