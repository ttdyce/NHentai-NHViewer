package personal.ttd.nhviewer.Saver;

import android.os.Environment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.comic.Collection;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.comic.ComicMaker;

import static personal.ttd.nhviewer.comic.Collection.COLUMN_ID;
import static personal.ttd.nhviewer.comic.Collection.COLUMN_THUMB_LINK;
import static personal.ttd.nhviewer.comic.Collection.COLUMN_TITLE;

public class JSONSaver implements Saver {

    @Override
    public boolean addFavorite(Comic c) {
        return addCollection(Collection.FAVARITE_ID, c);
    }

    @Override
    public Collection getFavorite() {
        Collection collection = new Collection();

        JSONArray jsonArray = null;
        ArrayList<Comic> comics = null;

        try {
            jsonArray = getFavoriteJSONArr();
            comics = ComicMaker.getComicListByJSONArray(jsonArray);
            Collections.reverse(comics);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        collection.comicList = comics;
        return collection;
    }

    @Override
    public boolean removeFavorite(String cid) {
        try {
            return removeCollection(Integer.parseInt(cid));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean addCollection(int collectionid, Comic c) {
        JSONArray arr = null;

        try {
            if(collectionid == Collection.FAVARITE_ID)
                arr = getFavoriteJSONArr();
            //else//not implemented others collection

            FileWriter writer = new FileWriter(getFavoriteFile());
            JSONObject obj = new JSONObject();

            obj.put(COLUMN_ID, c.getId());
            obj.put(COLUMN_TITLE, c.getTitle());
            obj.put(COLUMN_THUMB_LINK, c.getThumbLink());
            arr.put(obj);

            writer.write(arr.toString());
            writer.flush();
            writer.close();

        } catch (IOException | JSONException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    @Override
    public Collection getCollection(int collectionid) {
        return null;
    }

    @Override
    public boolean removeCollection(int collectionid, String cid) {
        return false;
    }

    @Override
    public boolean isCollected(String cid) {
        JSONArray arr;

        try {
            arr = getFavoriteJSONArr();

            for (int i = 0; i < arr.length(); i++) {
                if (arr.getJSONObject(i).getString(Collection.COLUMN_ID).equals(cid))
                    return true;
            }
        } catch (JSONException | IOException e) {
            e.printStackTrace();
            return true;
        }
        return false;
    }

    private JSONArray getFavoriteJSONArr() throws IOException, JSONException {
        File file = getFavoriteFile();

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

        String result = stringBuilder.toString();
        if(result.equals(""))
            return new JSONArray();
        return new JSONArray(result);

    }

    private boolean removeCollection(int pos) throws IOException, JSONException {
        JSONArray arr;

        arr = getFavoriteJSONArr();
            //using reversed order

        JSONObject removed = (JSONObject) arr.remove(arr.length() - pos - 1);

            //Log.i(TAG, "removeCollection:  removed: " + removed.getString("title"));

        setJSONFile(arr.toString());

        return true;
    }

    private void setJSONFile(String s) {
        try {
            FileWriter writer = new FileWriter(getFavoriteFile());

            writer.write(s);

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    private File getFavoriteFile() throws IOException {
        return getFile("Favorite");
    }

    private File getFile(String name) throws IOException {
        // Get the directory for the user's public document directory.
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Collection.PARENT_DIECTORY_NAME);
        File file = new File(dir, name);
        boolean isIOSuccess = true;

        //if no collection found, create a new one.
        if (!dir.exists() || !file.exists()){
            isIOSuccess = dir.mkdirs();
            isIOSuccess = file.createNewFile();
        }

        return file ;
    }
}
