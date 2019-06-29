package personal.ttd.nhviewer.Model.Saver;

import android.content.Context;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.Comic;
import personal.ttd.nhviewer.Model.comic.ComicMaker;

import static personal.ttd.nhviewer.Model.comic.Collection.COLUMN_ID;
import static personal.ttd.nhviewer.Model.comic.Collection.COLUMN_THUMB_LINK;
import static personal.ttd.nhviewer.Model.comic.Collection.COLUMN_TITLE;

public class JSONSaver implements Saver {
    // Get the directory for the user's public document directory. Here I use <internal storage>/document/NH/
    private final String PATH_NH_ROOT = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + Collection.PARENT_DIRECTORY_NAME + "/";
    private final String PATH_NH_REMOVED = PATH_NH_ROOT + Collection.REMOVED_DIRECTORY_NAME;
    private final File FILE_NH_ROOT = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), Collection.PARENT_DIRECTORY_NAME);
    private final File FILE_NH_REMOVED = new File(PATH_NH_REMOVED);

    @Override
    public boolean addFavorite(Comic c) {
        return addCollection(Collection.FAVARITE_ID, c);
    }

    @Override
    public Collection getFavorite() {
        return getCollection(Collection.FAVARITE_ID);
    }

    @Override
    public Comic removeFavorite(Comic comic) throws IOException, JSONException {
        return removeCollection(Collection.FAVARITE_ID, comic);
    }

    @Override
    public boolean addHistory(Comic c) {
        return addCollection(Collection.HISTORY_ID, c);
    }

    @Override
    public Collection getHistory() {
        return getCollection(Collection.HISTORY_ID);
    }

    @Override
    public boolean addCollection(int collectionid, Comic c) {
        JSONArray arr;

        try {
            if (collectionid == Collection.FAVARITE_ID)
                arr = getFavoriteJSONArr();
            else if (collectionid == Collection.HISTORY_ID)
                arr = getHistoryJSONArr();
            else
                arr = getJSONArr(Collection.NAME_LIST.get(collectionid));

            FileWriter writer = new FileWriter(getFile(Collection.NAME_LIST.get(collectionid)));
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
        } catch (NullPointerException e) {
            if (c == null) {
                Log.i("JSONSaver", "addCollection(): created collection, " + Collection.NAME_LIST.get(collectionid));

            }
        }

        return true;
    }

    @Override
    public Collection getCollection(int collectionid) {
        Collection collection = new Collection();

        JSONArray jsonArray = null;
        ArrayList<Comic> comics = null;

        try {
            jsonArray = getJSONArr(Collection.NAME_LIST.get(collectionid));
            comics = ComicMaker.getComicListByJSONArray(jsonArray);
            Collections.reverse(comics);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }

        collection.id = collectionid;
        collection.name = Collection.NAME_LIST.get(collectionid);
        collection.comicList = comics;

        return collection;
    }

    @Override
    public Comic removeCollection(int collectionid, int pos) throws IOException, JSONException {
        JSONArray arr = getJSONArr(Collection.NAME_LIST.get(collectionid));
        JSONObject removedObj = (JSONObject) arr.remove(arr.length() - pos - 1);

        setJSONFile(collectionid, arr.toString());

        String id = removedObj.getString("id");
        String title = removedObj.getString("title");
        String thumblink = removedObj.getString("thumblink");
        return new Comic(id, title, thumblink);
    }

    @Override
    public Comic removeCollection(int collectionid, Comic comic) throws IOException, JSONException {
        JSONArray arr = getJSONArr(Collection.NAME_LIST.get(collectionid));
        JSONObject removedObj = null;
        for (int i = 0; i < arr.length(); i++) {
            JSONObject obj = arr.getJSONObject(i);
            if (obj.getString("id").equals(comic.getId()))
                removedObj = (JSONObject) arr.remove(i);
        }

        setJSONFile(collectionid, arr.toString());

        String id = removedObj.getString("id");
        String title = removedObj.getString("title");
        String thumblink = removedObj.getString("thumblink");
        return new Comic(id, title, thumblink);
    }

    @Override
    public boolean addCollectionList(int collectionid, String collectionName) {
        return Collection.addCollection(collectionid, collectionName);
    }

    @Override
    public boolean addCollectionList(String collectionName) {
        return addCollectionList(Collection.NEXT_CUSTOM_ID, collectionName);
    }

    @Override
    public String removeCollectionList(int collectionid) throws IOException {
        String removedName = Collection.NAME_LIST.remove(collectionid);
        File removedFile = getFile(removedName);
        if (!FILE_NH_REMOVED.exists())
            FILE_NH_REMOVED.mkdir();

        removedFile.renameTo(new File(PATH_NH_REMOVED, removedName));

        return removedName;//true means removed
    }

    @Override
    public boolean isFavorited(String cid) {
        return isCollected(cid, Collection.FAVARITE_ID);
    }

    @Override
    public boolean isCollected(String cid, int collectionid) {
        JSONArray arr;

        try {
//            arr = getFavoriteJSONArr();
            arr = getJSONArr(Collection.NAME_LIST.get(collectionid));

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

    @Override
    public ArrayList<Collection> getCollectionAll() {
        ArrayList<Collection> collections = new ArrayList<>();

        for (int i : Collection.NAME_LIST.keySet()) {
            Collection c = getCollection(i);
            collections.add(c);
        }

        return collections;
    }


    public ArrayList<Collection> getCollectionList() throws IOException, JSONException {
        ArrayList<Collection> list = new ArrayList<>();
        int count = Collection.CUSTOM_ID_START;
        File[] collectionFiles = getFileAll();

        if (collectionFiles != null)//null when first time running
            for (File f :
                    collectionFiles) {
                Collection collection = new Collection();
                collection.id = count++;
                collection.name = f.getName();
                collection.comicList = ComicMaker.getComicListByJSONArray(getJSONArr(collection.name));

                if (!f.getName().equals(Collection.NAME_LIST.get(0))
                        && !f.getName().equals(Collection.NAME_LIST.get(1))) {
                    list.add(collection);
//                Collection.NEXT_CUSTOM_ID ++ ;
                } else
                    count--;
            }

        return list;//true means removed
    }

    //return non folder file
    private File[] getFileAll() {
        File root = FILE_NH_ROOT;
        FileFilter filter = file -> {
            if (file.isDirectory())
                return false;

            return true;
        };

        return root.listFiles(filter);
    }

    private JSONArray getFavoriteJSONArr() throws IOException, JSONException {
        return getJSONArr(Collection.NAME_LIST.get(Collection.FAVARITE_ID));
    }

    private JSONArray getHistoryJSONArr() throws IOException, JSONException {
        return getJSONArr(Collection.NAME_LIST.get(Collection.HISTORY_ID));
    }

    /*
     * read file by name, and return them in JSON Array format
     * */
    private JSONArray getJSONArr(String collectionName) throws IOException, JSONException {
        File file = getFile(collectionName);

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
        if (result.equals(""))
            return new JSONArray();
        return new JSONArray(result);

    }

    private boolean removeFavorite(int pos) throws IOException, JSONException {
        JSONArray arr;

        arr = getFavoriteJSONArr();
        //using reversed order

        JSONObject removed = (JSONObject) arr.remove(arr.length() - pos - 1);

        setJSONFile(Collection.FAVARITE_ID, arr.toString());

        return true;
    }

    private void setJSONFile(int collectionid, String s) {
        try {
            FileWriter writer = new FileWriter(getFile(Collection.NAME_LIST.get(collectionid)));

            writer.write(s);

            writer.flush();
            writer.close();

        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void backup(Context context) {
        new Thread(() -> {
            FileOutputStream fos = null;
            FileInputStream fis = null;
            DataInputStream dis = null;
            DataOutputStream dos = null;

            try {
                //zip file
                File zipFileDir = context.getCacheDir();
                File zipFile = File.createTempFile("NHCollections", "zip", zipFileDir);

                File[] srcFiles = getFileAll();
                fos = new FileOutputStream(zipFile);
                ZipOutputStream zipOut = new ZipOutputStream(fos);
                for (File fileToZip : srcFiles) {
                    fis = new FileInputStream(fileToZip);
                    ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
                    zipOut.putNextEntry(zipEntry);

                    byte[] bytes = new byte[4096];
                    int length;
                    while ((length = fis.read(bytes)) >= 0) {
                        zipOut.write(bytes, 0, length);
                    }
                    fis.close();
                }
                zipOut.close();
                fos.close();

                //send file
                fis = new FileInputStream(zipFile);
                Socket s = new Socket();
                s.connect(new InetSocketAddress("192.168.128.57", 3333), 1500);//my laptop ip
                s.setSoTimeout(5000);

                dis = new DataInputStream(s.getInputStream());
                dos = new DataOutputStream(s.getOutputStream());

                int count;
                byte[] buffer = new byte[4096];
                while ((count = fis.read(buffer)) > 0) {
                    dos.write(buffer, 0, count);
                }

                String response = dis.readUTF();

                //Toast cannot run on thread, use Activity.runOnUiThread instead
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Backup " + response, Toast.LENGTH_SHORT).show();
                    }
                });


            } catch (SocketTimeoutException e) {
                e.printStackTrace();
                //Toast cannot run on thread, use Activity.runOnUiThread instead
                ((AppCompatActivity) context).runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(context, "Failed, server not found or no response", Toast.LENGTH_SHORT).show();
                    }
                });

            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (fis != null)
                        fis.close();
                    if (dis != null)
                        dis.close();
                    if (dos != null)
                        dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();


    }

    //create file with collection name if needed, and return it
    private File getFile(String collectionName) throws IOException {
        File file = new File(FILE_NH_ROOT, collectionName);
        boolean isIOSuccess = true;

        //if no collection found, create a new one.
        if (!FILE_NH_ROOT.exists() || !file.exists()) {
            isIOSuccess = FILE_NH_ROOT.mkdirs();
            isIOSuccess = file.createNewFile();
        }

        return file;
    }
}
