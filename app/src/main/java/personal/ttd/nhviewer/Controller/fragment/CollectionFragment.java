package personal.ttd.nhviewer.Controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;

import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.CollectionMaker;

public class CollectionFragment extends ComicListDisplayerFragment {
    private int collectionid;

    @Override
    public void setArguments(@Nullable Bundle args) {
        super.setArguments(args);
        collectionid = args.getInt("id");
    }

    @Override
    protected void setComicList(int page) {
        hasPage = false;

        Collection c = CollectionMaker.getCollection(collectionid);
        comicListReturnCallback.onResponse(c.comicList);

    }

    @Override
    protected String getSubtitle() {
        return Collection.NAME_LIST.get(collectionid);
    }

    @Override
    protected int getCollectionid(){
        return collectionid;
    }
}
