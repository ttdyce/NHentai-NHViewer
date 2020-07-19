package com.github.ttdyce.nhviewer.model.room;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface ComicBookmarkDao {
    String ORDER_BY_DEFAULT = "dateOfCreate DESC";

    @Query("SELECT * FROM ComicBookmark WHERE id = :id ORDER BY " + ORDER_BY_DEFAULT)
    List<ComicBookmarkEntity> getById(int id);

    @Query("SELECT * FROM ComicBookmark ORDER BY " + ORDER_BY_DEFAULT)
    List<ComicBookmarkEntity> getAll();

    @Query("SELECT count(*) == 0 FROM ComicBookmark Where id = :id AND page = :page")
    boolean notExist(int id, int page);

    @Insert
    void insertAll(ComicBookmarkEntity... comicBookmark);

    @Insert
    void insert(ComicBookmarkEntity comicBookmark);

    @Delete
    void delete(ComicBookmarkEntity comicBookmark);

    @Update
    void update(ComicBookmarkEntity comicBookmark);
}
