package personal.ttd.nhviewer.Model.comic;

import java.util.ArrayList;

import personal.ttd.nhviewer.Model.Saver.Saver;
import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Model.ListReturnCallBack;

public class CollectionMaker {

    public static void getCollectionAll(ListReturnCallBack listReturnCallback) {
        Saver saver = SaverMaker.getDefaultSaver();
        ArrayList<Collection> collections = saver.getCollectionAll();

        listReturnCallback.onResponse(collections);
    }

    public static Collection getCollection(int collectionid) {
        int id = collectionid;
        Saver saver = SaverMaker.getDefaultSaver();

        return saver.getCollection(id);
    }
}
