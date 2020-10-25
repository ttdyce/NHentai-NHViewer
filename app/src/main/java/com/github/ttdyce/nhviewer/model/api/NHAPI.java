package com.github.ttdyce.nhviewer.model.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.github.ttdyce.nhviewer.view.SettingsFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.util.Locale;

public class NHAPI {
    private static final String TAG = "NHAPI";
    private Context context;

    public NHAPI(Context context) {
        this.context = context;
    }

    public void getComicList(String query, final ResponseCallback callback, SharedPreferences pref) {
        getComicList(query, 1, false, callback, pref);
    }

    public void getComicList(String query, int page, final ResponseCallback callback, SharedPreferences pref) {
        getComicList(query, page, false, callback, pref);
    }

    public void getComicList(String query, boolean sortedPopular, final ResponseCallback callback, SharedPreferences pref) {
        getComicList(query, 1, sortedPopular, callback, pref);
    }

    /*
     * Return a JsonArray string containing 25 Comic object, as [ {"id": 284928,"media_id": "1483523",...}, ...]
     * */
    public void getComicList(String query, int page, boolean sortedPopular, final ResponseCallback callback, SharedPreferences pref) {
        String languageId = pref.getString(MainActivity.KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.notSet.toString());
        int languageIdInt = Integer.parseInt(languageId);

        final String[] languageArray = context.getResources().getStringArray(R.array.key_languages);
        String language = languageArray[languageIdInt];

        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = URLs.search("language:" + language + " " + query, page, sortedPopular);
        Log.d(TAG, "getComicList: loading from url " + url);
        Log.d(TAG, "getComicList: language id = " + languageId);
        if(languageIdInt  == SettingsFragment.Language.all.getInt() || languageIdInt == SettingsFragment.Language.notSet.getInt())// TODO: 2019/10/1 Function is limited if language = all
            url = URLs.getIndex(page);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonArray result = new JsonParser().parse(response).getAsJsonObject().get("result").getAsJsonArray();
                        callback.onReponse(result.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);

    }

    public void getComic(int id, final ResponseCallback callback) {
        Log.d(TAG, "nhapi: getting comic");
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = URLs.getComic(id);

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "onResponse: got comic");

                        callback.onReponse(response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                callback.onErrorResponse(error);
            }
        });

        // Add the request to the RequestQueue.
        queue.add(stringRequest);
    }

    //https://nhentai.net/api/galleries/search?query=language:chinese&page=1&sort=popular
    //https://nhentai.net/api/gallery/284987
    public static class URLs {
        private static String searchPrefix = "https://nhentai.net/api/galleries/search?query=";
        private static String getComicPrefix = "https://nhentai.net/api/gallery/";
        private static String[] types = {"jpg", "png"};

        public static String search(String query, int page, boolean sortedPopular) {
            if (sortedPopular)
                return searchPrefix + query + "&page=" + page + "&sort=popular";
            else
                return searchPrefix + query + "&page=" + page;
        }

        public static String getComic(int id) {
            return getComicPrefix + id;
        }

        public static String getThumbnail(String mid, String type) {
            for (String t : types) {
                if (t.charAt(0) == type.charAt(0))
                    return String.format(Locale.ENGLISH, "https://t.nhentai.net/galleries/%s/thumb.%s", mid, t);
            }

            return "";//should be not needed
        }

        public static String getPage(String mid, int page, String type) {
            for (String t : types) {
                if (t.charAt(0) == type.charAt(0))
                    return String.format(Locale.ENGLISH, "https://i.nhentai.net/galleries/%s/%d.%s", mid, page, t);
            }

            return "";//should be not needed
        }

        public static String getIndex(int page) {
            return "https://nhentai.net/api/galleries/all?page=" + page;
        }
    }
}
