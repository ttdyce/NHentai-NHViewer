package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.Model.Comic.Comic;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertNotNull;

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
