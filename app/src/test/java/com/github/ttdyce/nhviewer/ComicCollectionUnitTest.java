package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.model.comic.Comic;
import com.github.ttdyce.nhviewer.model.room.ComicCollectionEntity;

import org.junit.Test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ComicCollectionUnitTest {
    private static final String TAG = "ComicCollectionUnitTest";

    @Test
    public void comicCollection_creation() {
        String name = "Demo collection";
        List<Comic> comics = new ArrayList<>();
        comics.add(new Comic());
        comics.add(new Comic());
        comics.add(new Comic());
        comics.add(new Comic());

//        ComicCollectionEntity cc = new ComicCollectionEntity(name, comics);

//        assertNotNull(cc);

    }

    @Test
    public void comicCollectionEntity_toJson() {
        String name = "Demo collection";
        ComicCollectionEntity cc = new ComicCollectionEntity(name, 333, new Date());

        cc.toJson();
    }

}
