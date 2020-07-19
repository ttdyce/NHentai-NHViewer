package com.github.ttdyce.nhviewer.model.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {ComicCollectionEntity.class, ComicCachedEntity.class, ComicBookmarkEntity.class}
        , version = 2)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {
    public static final String COL_COLLECTION_HISTORY = "History";
    public static final String COL_COLLECTION_FAVORITE = "Favorite";
    public static final String COL_COLLECTION_NEXT = "Next";
    public static final String DB_NAME = "Nhviewer";

    public abstract ComicCollectionDao comicCollectionDao();

    public abstract ComicCachedDao comicCachedDao();

    public abstract ComicBookmarkDao comicBookmarkDao();

    public static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // add comic bookmark table
            database.execSQL("CREATE TABLE IF NOT EXISTS `ComicBookmark` (`page` INTEGER NOT NULL, `id` INTEGER NOT NULL, `dateOfCreate` TEXT, PRIMARY KEY(`id`, `page`))");
        }
    };

}
