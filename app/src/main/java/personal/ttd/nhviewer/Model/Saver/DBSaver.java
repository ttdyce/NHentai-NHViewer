package personal.ttd.nhviewer.Model.Saver;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

import personal.ttd.nhviewer.Model.Saver.file.FeedReaderContract;
import personal.ttd.nhviewer.Model.Saver.file.FeedReaderDbHelper;
import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.Comic;

public class DBSaver implements Saver{

    @Override
    public boolean addFavorite(Comic c) {
        return false;
    }

    @Override
    public Collection getFavorite() {
        return null;
    }

    @Override
    public Comic removeFavorite(Comic cid) {
        return null;
    }

    @Override
    public boolean addHistory(Comic c) {
        return false;
    }

    @Override
    public Collection getHistory() {
        return null;
    }

    @Override
    public boolean addCollection(int collectionid, Comic comic) {
        return false;
    }

    @Override
    public Collection getCollection(int collectionid) {
        return null;
    }

    @Override
    public Comic removeCollection(int collectionid, int pos) {
        return null;
    }

    @Override
    public Comic removeCollection(int collectionid, Comic comic) {
        return null;
    }

    @Override
    public boolean addCollectionList(int collectionid, String collectionName) {
        return false;
    }

    @Override
    public String removeCollectionList(int collectionid) {
        return null;
    }

    @Override
    public boolean isFavorited(String cid) {
        return false;
    }

    @Override
    public boolean isCollected(String cid, int collectionid) {
        return false;
    }

    @Override
    public ArrayList<Collection> getCollectionAll() {
        return null;
    }

    @Override
    public void backup(Context context) {
    }

    @Override
    public boolean addCollectionList(String collectionName) {
        return false;
    }

    public int getSeenPageDB(Context context, String comicid){
        int seenPage = 0;
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

//        String sortOrder =BaseColumns._ID + " DESC";
        String selection = FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID + " = ?" ;
        String[] selectionArgs = {comicid};

        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_HISTORY,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
                selectionArgs,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                null               // The sort order
        );
        cursor.moveToNext();
        seenPage = cursor.getInt(cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SEENPAGE));
        cursor.close();

        return --seenPage;
    }
}
