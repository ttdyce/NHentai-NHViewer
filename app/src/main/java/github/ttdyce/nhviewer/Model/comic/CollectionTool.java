package github.ttdyce.nhviewer.Model.comic;

import android.content.Context;

import java.io.IOException;

import github.ttdyce.nhviewer.Model.Saver.Saver;
import github.ttdyce.nhviewer.Model.Saver.SaverMaker;

public class CollectionTool {
    private static Saver saver = SaverMaker.getDefaultSaver();

    public static boolean addCollectionList(String collectionName){
        return saver.addCollectionList(collectionName) && saver.addCollection(Collection.NEXT_CUSTOM_ID, null);

    }

    public static String removeCollectionList(int id) throws IOException {
        return saver.removeCollectionList(id);
    }

    public static void backup(Context context) {
        saver.backup(context);
    }
}
