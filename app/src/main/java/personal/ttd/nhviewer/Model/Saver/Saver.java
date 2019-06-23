package personal.ttd.nhviewer.Model.Saver;

import android.content.Context;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;

import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.Comic;

/*

Saver should manage any data that need to be saved.

Scope:
comic -> collection -> collectionList
collectionList {FavoriteList, HistoryList [, customList ...]}

Currently, I am using json file saver and database saver.

 */
public interface Saver {
    boolean addFavorite(Comic c);
    Collection getFavorite();
    Comic removeFavorite(Comic cid) throws IOException, JSONException;

    boolean addHistory(Comic c);
    Collection getHistory();

    boolean addCollection(int collectionid, Comic comic);
    Collection getCollection(int collectionid);
    Comic removeCollection(int collectionid, int pos) throws IOException, JSONException;
    Comic removeCollection(int collectionid, Comic comic) throws IOException, JSONException;

    boolean addCollectionList(int collectionid, String collectionName);
    String removeCollectionList(int collectionid) throws IOException;

    boolean isFavorited(String cid);
    boolean isCollected(String cid, int collectionid);

    ArrayList<Collection> getCollectionAll();

    boolean addCollectionList(String collectionName);

    void backup(Context context);
}