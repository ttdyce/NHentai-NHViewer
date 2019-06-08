package personal.ttd.nhviewer.Model.Saver.file;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class FeedReaderDbHelper extends SQLiteOpenHelper {

    @Override
    protected void finalize() throws Throwable {
        this.close();
        super.finalize();
    }

    //tables
    private static final String CREATE_TABLE_COLLECTION =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_COLLECTION + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTIONID+ " INTEGER PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID  + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTDATE + " TEXT DEFAULT CURRENT_TIMESTAMP);";

    private static final String CREATE_TABLE_COMIC =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_COMIC + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID + " TEXT PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COMICNAME+ " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_MID  + " TEXT," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_TOTALPAGE + " TEXT);";

    private static final String CREATE_TABLE_HISTORY =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_HISTORY+ " (" +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID + " TEXT PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_TIME + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_SEENPAGE + " INTEGER);";

    private static final String CREATE_TABLE_INNERPAGE =
            "CREATE TABLE " + FeedReaderContract.FeedEntry.TABLE_INNERPAGE + " (" +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID + " TEXT PRIMARY KEY," +
                    FeedReaderContract.FeedEntry.COLUMN_NAME_FORMAT + " TEXT);";

    private static final String[] SQL_CREATE_ENTRIES = {
            CREATE_TABLE_COLLECTION ,
                    CREATE_TABLE_COMIC ,
                    CREATE_TABLE_HISTORY ,
                    CREATE_TABLE_INNERPAGE
    };

    //delete statement
    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedReaderContract.FeedEntry.TABLE_COLLECTION;

    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "NHDB.db";

    public FeedReaderDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        for (String s :
                SQL_CREATE_ENTRIES) {
            db.execSQL(s);
        }
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

}