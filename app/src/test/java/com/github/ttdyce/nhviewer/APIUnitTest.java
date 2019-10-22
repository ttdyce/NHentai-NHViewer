package com.github.ttdyce.nhviewer;

import com.github.ttdyce.nhviewer.model.api.NHAPI;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class APIUnitTest {
    private final String mid = "1438192";

    @Test
    public void get_thumbnail_url() {
        String url = NHAPI.URLs.getThumbnail(mid, "j");

        assertEquals(url, "https://t.nhentai.net/galleries/" + mid + "/thumb.jpg");
    }

}
