package personal.ttd.nhviewer.Model.Saver.file;

import android.provider.BaseColumns;

public final class FeedReaderContract {
    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private FeedReaderContract() {}

    /* Inner class that defines the table contents */
    public static class FeedEntry implements BaseColumns {
        //Collection table
        public static final String TABLE_COLLECTION = "Collection";
        public static final String COLUMN_NAME_COLLECTIONID = "collectionId";
        public static final String COLUMN_NAME_COMICID = "comicId";
        public static final String COLUMN_NAME_COLLECTDATE = "collectDate";

        //Comic  table
        public static final String TABLE_COMIC = "Comic";
        //        public static final String COLUMN_NAME_COMICID = "comicId";
        public static final String COLUMN_NAME_COMICNAME = "seenPage";
        public static final String COLUMN_NAME_MID = "mid";
        public static final String COLUMN_NAME_TOTALPAGE = "totalPage";

        //History table
        public static final String TABLE_HISTORY = "History";
        public static final String COLUMN_NAME_SEENPAGE = "seenPage";
        public static final String COLUMN_NAME_UPDATE_TIME = "updateTime";
//        public static final String COLUMN_NAME_COMICID = "comicId";

        //InnerPage table
        public static final String TABLE_INNERPAGE = "InnerPage";
        public static final String COLUMN_NAME_FORMAT = "format";
//        public static final String COLUMN_NAME_COMICID = "comicId";

    }
}