package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.model.comic.Comic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class ComicCollectionUnitTest {
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

}
