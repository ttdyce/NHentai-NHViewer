package github.ttdyce.nhviewer.Model.comic;

import java.util.ArrayList;

import github.ttdyce.nhviewer.Model.Saver.Saver;
import github.ttdyce.nhviewer.Model.Saver.SaverMaker;
import github.ttdyce.nhviewer.Model.ListReturnCallBack;

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
