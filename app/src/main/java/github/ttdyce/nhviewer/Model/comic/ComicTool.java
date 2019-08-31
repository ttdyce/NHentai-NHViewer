package github.ttdyce.nhviewer.Model.comic;

import org.json.JSONException;

import java.io.IOException;

import github.ttdyce.nhviewer.Model.Saver.Saver;
import github.ttdyce.nhviewer.Model.Saver.SaverMaker;

public class ComicTool {
    private static Saver saver = SaverMaker.getDefaultSaver();

    public static boolean collect(Comic comicSelected, int collectionid){
        return saver.addCollection(collectionid, comicSelected);
    }

    public static Comic uncollectByPosition(int collectionid, int position) throws IOException, JSONException {
        return saver.removeCollection(collectionid, position);
    }

    public static Comic uncollectByComic(int collectionid, Comic comic) throws IOException, JSONException{
        return saver.removeCollection(collectionid, comic);
    }

    public static boolean isCollected(String cid, int collectionid){
        return saver.isCollected(cid, collectionid);

    }

}
