package com.github.ttdyce.nhviewer.Model.Room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ComicCollectionDao {

    @Query("SELECT * FROM ComicCollection")
    List<ComicCollectionEntity> getAll();

    @Query("SELECT * FROM ComicCollection WHERE name = :name")
    ComicCollectionEntity findByName(String name);

    @Insert
    void insertAll(ComicCollectionEntity... comicCollection);

    @Insert
    void insert(ComicCollectionEntity comicCollection);

    @Delete
    void delete(ComicCollectionEntity comicCollection);

}
