package com.github.ttdyce.nhviewer.model.api;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.github.ttdyce.nhviewer.R;
import com.github.ttdyce.nhviewer.model.CookieStringRequest;
import com.github.ttdyce.nhviewer.model.proxy.NHVProxyStack;
import com.github.ttdyce.nhviewer.view.MainActivity;
import com.github.ttdyce.nhviewer.view.SettingsFragment;
import com.google.gson.JsonArray;
import com.google.gson.JsonParser;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Locale;

public class NHAPI {
    private static final String TAG = "NHAPI";
    private Context context;
    private String proxyHost;
    private int proxyPort;
    private RequestQueue requestQueue;
    private RequestQueue requestQueueProxied;

    public NHAPI(Context context, String proxyHost, int proxyPort) {
        this.context = context;
        this.proxyHost = proxyHost;
        this.proxyPort = proxyPort;
        requestQueue = Volley.newRequestQueue(context);
        requestQueueProxied = Volley.newRequestQueue(context, new NHVProxyStack(proxyHost, proxyPort));
    }

    /*
     * Return a JsonArray string containing 25 Comic object, as [ {"id": 284928,"media_id": "1483523",...}, ...]
     * */
    public void getComicList(String query, int page, PopularType popularType, final ResponseCallback callback, SharedPreferences pref) {
        String languageId = pref.getString(MainActivity.KEY_PREF_DEFAULT_LANGUAGE, SettingsFragment.Language.notSet.toString());
        boolean isSponsor = pref.getBoolean(MainActivity.KEY_PREF_IS_SPONSOR, false);

        int languageIdInt = Integer.parseInt(languageId);

        final String[] languageArray = context.getResources().getStringArray(R.array.key_languages);
        String language = languageArray[languageIdInt];

        // choose the RequestQueue.
        RequestQueue queue = MainActivity.isProxied() ? requestQueueProxied : requestQueue;
        String url = URLs.search("language:" + language + " " + query, page, popularType);
        Log.d(TAG, "getComicList: loading from url " + url);
        Log.d(TAG, "getComicList: language id = " + languageId);
        if (languageIdInt == SettingsFragment.Language.all.getInt() || languageIdInt == SettingsFragment.Language.notSet.getInt())// TODO: 2019/10/1 Function is limited if language = all
            url = URLs.getIndex(page);

        // for sponsors, debugging
        try {
            if ((isSponsor || "ttdyce".equals(MainActivity.currentUsername)) && pref.getBoolean(MainActivity.KEY_PREF_NHVP_PROXY, false))
                url = "https://hello-ttdyce.azurewebsites.net/api/NHViewerProxy?code=wfV4fHvSB1ydMDRdQzVcktxA3XieSmN5bKHUVzGTdJuQkkob2p/d2w==&url=" + URLEncoder.encode(url, "utf-8");
            Log.d(TAG, "for sponsors, debugging: loading from url " + URLEncoder.encode(url, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        while (CookieStringRequest.challengeCookies == null) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "getComicList: CookieStringRequest.challengeCookies is still null!");
            }
        }

        Log.i(TAG, "getComicList: CookieStringRequest.challengeCookies is ready (SplashScreen/RefreshCookieScreen is ok) ");
        // Request a string response from the provided URL.
        CookieStringRequest stringRequest = new CookieStringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        JsonArray result = JsonParser.parseString(response).getAsJsonObject().get("result").getAsJsonArray();
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
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(context);
        boolean isSponsor = pref.getBoolean(MainActivity.KEY_PREF_IS_SPONSOR, false);
        // Get the RequestQueue.
        RequestQueue queue = MainActivity.isProxied() ? requestQueueProxied : requestQueue;
        String url = URLs.getComic(id);

        // for sponsors, debugging
        try {
            if ((isSponsor || "ttdyce".equals(MainActivity.currentUsername)) && pref.getBoolean(MainActivity.KEY_PREF_NHVP_PROXY, false))
                url = "https://hello-ttdyce.azurewebsites.net/api/NHViewerProxy?code=wfV4fHvSB1ydMDRdQzVcktxA3XieSmN5bKHUVzGTdJuQkkob2p/d2w==&url=" + URLEncoder.encode(url, "utf-8");
            Log.d(TAG, "for sponsors, debugging: loading from url " + URLEncoder.encode(url, "utf-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        while (CookieStringRequest.challengeCookies == null) {
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Log.d(TAG, "getComic: CookieStringRequest.challengeCookies is still null!");
            }
        }

        Log.i(TAG, "getComic: CookieStringRequest.challengeCookies is ready (SplashScreen/RefreshCookieScreen is ok) ");

        // Request a string response from the provided URL.
        CookieStringRequest stringRequest = new CookieStringRequest(Request.Method.GET, url,
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

        public static String search(String query, int page, PopularType popularType) {
            if (popularType == PopularType.none)
                return searchPrefix + query + "&page=" + page;
            if (popularType == PopularType.allTime)
                return searchPrefix + query + "&page=" + page + "&sort=popular";
            if (popularType == PopularType.month)
                return searchPrefix + query + "&page=" + page + "&sort=popular-month";
            if (popularType == PopularType.week)
                return searchPrefix + query + "&page=" + page + "&sort=popular-week";
            if (popularType == PopularType.today)
                return searchPrefix + query + "&page=" + page + "&sort=popular-today";

            Log.w(TAG, "search: popular-type not found");
            return searchPrefix + query + "&page=" + page;// should be not needed
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
