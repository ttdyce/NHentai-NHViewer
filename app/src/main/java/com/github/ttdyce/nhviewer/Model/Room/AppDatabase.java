package com.github.ttdyce.nhviewer.Model.Room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {ComicCollectionEntity.class, ComicCachedEntity.class}, version = 1 )
public abstract class AppDatabase extends RoomDatabase {
    public static final String COL_COLLECTION_HISTORY = "History";
    public static final String COL_COLLECTION_FAVORITE = "Favorite";
    public static final String COL_COLLECTION_NEXT = "Next";
    public static final String DB_NAME = "Nhviewer";

    public abstract ComicCollectionDao comicCollectionDao();
    public abstract ComicCachedDao comicCachedDao();
}
