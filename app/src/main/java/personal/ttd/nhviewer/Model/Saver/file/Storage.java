package personal.ttd.nhviewer.Model.Saver.file;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import personal.ttd.nhviewer.Model.comic.Comic;

public class Storage {

    private static final String TAG = "Form file";

    /*
     * Start of old json methods
     *
     * */
    public static void addCollection(Comic c) throws IOException, JSONException {
        JSONArray arr = getCollectionsJSON();

        if (arr.toString().isEmpty())
            arr = new JSONArray();

        FileWriter writer = new FileWriter(getCollectionsFile());

        JSONObject obj = new JSONObject();
        obj.put("id", c.getId());
        obj.put("title", c.getTitle());
        obj.put("thumblink", c.getThumbLink());


        arr.put(obj);
        writer.write(arr.toString());

        writer.flush();
        writer.close();
    }

    public static JSONArray getCollectionsJSON() throws IOException, JSONException {
        File file = getCollectionsFile();
        FileInputStream inputStream = new FileInputStream(file);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

        StringBuilder stringBuilder = new StringBuilder();
        String line = bufferedReader.readLine();

        while (line != null) {
            stringBuilder.append(line);
            Log.i("getFileContent", "getFileContent: line = " + line);

            line = bufferedReader.readLine();
        }


        bufferedReader.close();
        inputStream.close();
        inputStreamReader.close();

        return new JSONArray(stringBuilder.toString());

    }

    public static boolean removeCollection(int pos) {
        JSONArray arr;
        try {
            arr = new JSONArray(getCollectionsJSON());
            //using reversed order

            JSONObject removed = (JSONObject) arr.remove(arr.length() - pos - 1);
            Log.i(TAG, "removeCollection:  removed: " + removed.getString("title"));

            setJSONFile(arr.toString());
        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public static boolean isCollected(String id) {
        JSONArray arr;

        try {
            arr = new JSONArray(getCollectionsJSON());
            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString("id").equals(id))
                    return true;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    private static File getCollectionsFile() {
        // Get the directory for the user's public pictures directory.
        File dir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "NH");
        File file;
        file = new File(dir, "Collections");

        if (!dir.exists() || !file.exists()) {
            dir.mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

            Log.e(TAG, "Created dir");
        }


        return file;
    }

    private static void setJSONFile(String s) {
        try {
            FileWriter writer = new FileWriter(getCollectionsFile());

            writer.write(s);

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }
    /*
     * End of old json methods
     *
     * */

    public static void saveImage(Comic comic, int position, Bitmap bmp) {
        File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
                + "/NHComics/" + comic.getTitle());
        boolean success = true;
        if (!storageDir.exists()) {
            success = storageDir.mkdirs();
        }
        if (success && storageDir.exists()) {

            String filename = position + ".png";
            File imageFile = new File(storageDir, filename);
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(imageFile);
                bmp.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
                // PNG is a lossless format, the compression factor (100) is ignored
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            // Add the image to the system gallery
            Log.i(TAG, "saveImage: IMAGE SAVED");
        }


    }

    /*
     * Start of new database methods
     *
     * */
    public static void insertTableCollection(Context context, String id) {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);

        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID, id);
        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_COLLECTION, null, values);

        Log.i(TAG, "inserted: Primary key = " + newRowId + ", comic id = " + id);
        mDbHelper.close();
    }

    public static void insertTableHistory(Context context, String id, int page) {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID, id);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SEENPAGE, page);

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_HISTORY, null, values);
        /*TODO handle if history exist*/
        Log.i(TAG, "inserted: Primary key=" + newRowId);
        mDbHelper.close();
    }

    public static void insertTableInnerPage(Context context, Comic c) {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID, c.getId());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_FORMAT, c.getPageTypes());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_INNERPAGE, null, values);
        Log.i(TAG, "inserted: Primary key=" + newRowId);
        mDbHelper.close();
    }

    public static void insertTableComic(Context context, Comic c) {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID, c.getId());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICNAME, c.getTitle());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_MID, c.getMid());
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_TOTALPAGE, c.getTotalPage());

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(FeedReaderContract.FeedEntry.TABLE_COMIC, null, values);
        Log.i(TAG, "inserted: Primary key=" + newRowId);
        mDbHelper.close();
    }


    public static void updateTableHistory(@NotNull Context context, @Nullable String id, int p) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = new Date();
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getWritableDatabase();
        String whereCause = FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID + " = " + id;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_SEENPAGE, p);
        values.put(FeedReaderContract.FeedEntry.COLUMN_NAME_UPDATE_TIME, dateFormat.format(date));

        // Insert the new row, returning the primary key value of the new row
        int updateReturn = db.update(FeedReaderContract.FeedEntry.TABLE_HISTORY, values, whereCause, null);
        Log.i(TAG, "db.update = " + updateReturn);
        mDbHelper.close();
    }

    public static List<Comic> getComicsFromDB(Context context) {
        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// Define a projection that specifies which columns from the database
// you will actually use after this query.
        String[] projection = {
                FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTIONID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID,
                FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTDATE
        };

// Filter results WHERE "title" = 'My Title'
        String selection = BaseColumns._ID + " = ?";
        String[] selectionArgs = {BaseColumns._ID};

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTIONID + " DESC";


        Cursor cursor = db.query(
                FeedReaderContract.FeedEntry.TABLE_COLLECTION,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List<Comic> comics = new ArrayList<>();
        while (cursor.moveToNext()) {
            Comic c = new Comic();

            String id = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTIONID));
            String comicId = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID));
            String date = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_COLLECTDATE));

            c.setId(id);
            c.setThumbLink(comicId);
            c.setTitle(date);

            comics.add(c);
            Log.i(TAG, "Done comicList.add(c);, date = " + date);
        }
        cursor.close();

        Log.i(TAG, "Done query");


        return comics;
    }


    public static void delete(int id) {

    }


    public static void update(int id, int v) {

    }


    public static void updateDatabase(Context context) {
        try {
            JSONArray arr= getCollectionsJSON();

            JSONObject obj;

            for (int i = 0; i < arr.length(); i++) {
                obj = arr.getJSONObject(i);

                String id = obj.getString("id");
                String title = obj.getString("title");
                String thumblink = obj.getString("thumblink");

                insertTableCollection(context, id);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Comic> getAllRows(@NotNull Context context, String tableName, String sortBy) {

        FeedReaderDbHelper mDbHelper = new FeedReaderDbHelper(context);
        SQLiteDatabase db = mDbHelper.getReadableDatabase();

// How you want the results sorted in the resulting Cursor
        String sortOrder =
                sortBy + " DESC";


        Cursor cursor = db.query(
                tableName,   // The table to query
                null,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        List<Comic> comics = new ArrayList<>();
        while (cursor.moveToNext()) {
            Comic c = new Comic();

            String comicId = cursor.getString(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_COMICID));
            int seenPage = cursor.getInt(
                    cursor.getColumnIndexOrThrow(FeedReaderContract.FeedEntry.COLUMN_NAME_SEENPAGE));

            c.setId(comicId);
            c.setSeenPage(seenPage);

            comics.add(c);
            Log.i(TAG, "Done comicList.add(c);, seenPage = " + seenPage);
        }
        cursor.close();

        Log.i(TAG, "Done query");


        return comics;
    }

    /*
     * End of new database methods
     *
     * */
}
