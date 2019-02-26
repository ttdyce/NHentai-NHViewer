package personal.ttd.nhviewer.Saver;

import personal.ttd.nhviewer.comic.Collection;
import personal.ttd.nhviewer.comic.Comic;

public interface Saver {
    boolean addFavorite(Comic c);
    Collection getFavorite();
    boolean removeFavorite(String cid);

    boolean addCollection(int collectionid, Comic comic);
    Collection getCollection(int collectionid);
    boolean removeCollection(int collectionid, String cid);

    boolean isCollected(String cid);
}