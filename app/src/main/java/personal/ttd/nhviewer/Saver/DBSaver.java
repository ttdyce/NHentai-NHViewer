package personal.ttd.nhviewer.Saver;

import personal.ttd.nhviewer.comic.Collection;
import personal.ttd.nhviewer.comic.Comic;

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
    public boolean removeFavorite(String cid) {
        return false;
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
    public boolean removeCollection(int collectionid, String cid) {
        return false;
    }

    @Override
    public boolean isCollected(String cid) {
        return false;
    }
}
