package com.github.ttdyce.nhviewer.Model.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ComicCollectionEntity.class}, version = 2 )
public abstract class AppDatabase extends RoomDatabase {
    public static final String COL_COLLECTION_HISTORY = "History";
    public static final String COL_COLLECTION_FAVORITE = "Favorite";
    public static final String COL_COLLECTION_READLATER = "ReadLater";

    public abstract ComicCollectionDao comicCollectionDao();
}
