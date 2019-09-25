package com.github.ttdyce.nhviewer.Model.Room;

import androidx.annotation.NonNull;
import androidx.room.Entity;

@Entity(tableName = "ComicCollection", primaryKeys = {"name","mid"})
public class ComicCollectionEntity {
    @NonNull
    private String name;

    @NonNull
    private String mid;

    public ComicCollectionEntity(@NonNull String name, @NonNull String mid) {
        this.name = name;
        this.mid = mid;
    }
    // Room uses this factory method to create ComicCollectionEntity objects.
    public static ComicCollectionEntity create(String name, String mid) {
        return new ComicCollectionEntity(name, mid);
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    @NonNull
    public String getMid() {
        return mid;
    }

    public void setMid(@NonNull String mid) {
        this.mid = mid;
    }
}
