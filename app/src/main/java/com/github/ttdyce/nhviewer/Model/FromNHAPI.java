package com.github.ttdyce.nhviewer.Model;

import com.android.volley.toolbox.Volley;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

public class FromNHAPI {
    //https://nhentai.net/api/galleries/search?query=language:chinese&page=1&sort=popular
    public static class URLs {
        private static String searchPrefix = "https://nhentai.net/api/galleries/search?query=language:chinese&page=1&sort=popular";
    }

    public List<Comic> getComic(String query, int page, boolean isSortedPopular, Runnable onResponse){
        onResponse.run();

        return null;
    }
}
