package com.github.ttdyce.nhviewer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ComicCollectionDao {
    String ORDER_BY_DEFAULT = "dateCreated DESC";

    @Query("SELECT * FROM ComicCollection ORDER BY " + ORDER_BY_DEFAULT)
    List<ComicCollectionEntity> getAll();

    @Query("SELECT * FROM ComicCollection WHERE name = :name ORDER BY " + ORDER_BY_DEFAULT)
    List<ComicCollectionEntity> getAllByName(String name);

    @Query("SELECT * FROM ComicCollection WHERE name = :name ORDER BY " + ORDER_BY_DEFAULT)
    ComicCollectionEntity findByName(String name);

    @Query("SELECT count(*) == 0 FROM ComicCollection Where id = :id AND name = :collectionName")
    boolean notExist(String collectionName, int id);

    @Insert
    void insertAll(ComicCollectionEntity... comicCollection);

    @Insert
    void insert(ComicCollectionEntity comicCollection);

    @Delete
    void delete(ComicCollectionEntity comicCollection);

    @Update
    void update(ComicCollectionEntity comicCollection);
}
