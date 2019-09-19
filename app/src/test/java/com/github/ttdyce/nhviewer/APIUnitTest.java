package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.Model.API.NHAPI;

import org.junit.Test;

import static org.junit.Assert.*;

public class APIUnitTest {
    private final String mid = "1438192";

    @Test
    public void get_thumbnail_url() {
        String url = NHAPI.URLs.getGetThumbnail(mid, "j");

        assertEquals(url, "https://t.nhentai.net/galleries/" + mid + "/thumb.jpg");
    }

}
