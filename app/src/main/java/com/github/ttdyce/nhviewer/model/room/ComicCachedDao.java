package com.github.ttdyce.nhviewer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ComicCachedDao {

    @Query("SELECT * FROM ComicCached")
    List<ComicCachedEntity> getAll();

    @Query("SELECT * FROM ComicCached WHERE id = :id")
    ComicCachedEntity findById(int id);

    @Query("SELECT * FROM ComicCached WHERE id In(:ids)")
    List<ComicCachedEntity> findById(List<Integer> ids);

    @Query("SELECT count(*) == 0 FROM ComicCached Where id = :id")
    boolean notExist(int id);

    @Insert
    void insertAll(ComicCachedEntity... comic);

    @Insert
    void insert(ComicCachedEntity comic);

    @Delete
    void delete(ComicCachedEntity comic);
}
