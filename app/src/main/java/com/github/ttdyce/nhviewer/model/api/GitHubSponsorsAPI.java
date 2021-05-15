package com.github.ttdyce.nhviewer.model.api;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.preference.PreferenceManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.ttdyce.nhviewer.view.MainActivity;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.concurrent.atomic.AtomicInteger;

public class GitHubSponsorsAPI {
    private static final String TAG ="GitHubSponsorsAPI";
    private static boolean isSponsor;
    private static AtomicInteger queueCount;

    public static void getSponsorsPartial(int page, String baseUrl, RequestQueue requestQueue, Context context, Runnable onFinishAllResponse) {
        String url = baseUrl + page;
        StringRequest documentRequest = new StringRequest(
                Request.Method.GET,
                url,
                response -> {
                    if (response.equals("") || isSponsor) {
                        if(queueCount.decrementAndGet() == 0){
                            onFinishAllResponse.run();
                        }
                        return;
                    }
                    Document document = Jsoup.parse(response);
                    Log.v(TAG, response);

                    Elements sponsorsElements = document.getElementsByTag("div");

                    for (int i = 0; i < sponsorsElements.size(); i++) {
                        Element element = document.getElementsByTag("div").get(i);
                        String userName = element.getElementsByTag("a").attr("href").replaceAll("(/)", "");

                        Log.i(TAG, String.format("%s (compared: %s - %s) ", userName.equals(MainActivity.currentUsername), userName, MainActivity.currentUsername));
                        if (userName.equals(MainActivity.currentUsername) ) {
                            isSponsor = true;
                            break;
                        }
                    }

                    Log.i(TAG, "getSponsorsPartial: set isSponsor page: " + page);
                    PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(MainActivity.KEY_PREF_IS_SPONSOR, isSponsor).apply();
                    MainActivity.isSponsor = isSponsor;

                    Log.d(TAG, "getSponsorsPartial: queueCount = " + queueCount + " page = " + page);

                    if(queueCount.decrementAndGet() == 0){
                        onFinishAllResponse.run();
                    }

                    // TODO: 5/15/2021 handle sponsors more than 4 pages
//                    if(page >= 4) // page 4 still exist, then check page 5,6,7,... one by one
//                        getSponsorsPartial(page + 1, baseUrl, requestQueue, context, onFinishAllResponse);
                },
                error -> {
                    queueCount.set(0);
                    Log.e(TAG, "getSponsorsPartial: Houston we have a problem ... !");
                    error.printStackTrace();
                    Toast.makeText(context, "Hey! You are not yet a sponsor", Toast.LENGTH_LONG).show();
                }
        );

        queueCount.incrementAndGet();
        requestQueue.add(documentRequest);
    }

    public static void getIsSponsorAsyc(Context context) {
        getIsSponsorAsyc(context, () ->{} );
    }

    public static void getIsSponsorAsyc(Context context, Runnable onFinishAllResponse) {
        queueCount = new AtomicInteger();
        isSponsor = false;
        String baseUrl = "https://github.com/sponsors/ttdyce/sponsors_partial?page=";
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        int page;

        for (page = 1; page <= 4; ++page) {
            GitHubSponsorsAPI.getSponsorsPartial(page, baseUrl, requestQueue, context, onFinishAllResponse);

        }
    }
}
