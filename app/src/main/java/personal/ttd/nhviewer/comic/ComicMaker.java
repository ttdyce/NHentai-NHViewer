package personal.ttd.nhviewer.comic;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.Volley.VolleyCallback;
import personal.ttd.nhviewer.api.NHTranlator;
import personal.ttd.nhviewer.file.Storage;

public class ComicMaker {

    public static void getComic(int cid, Context context, VolleyCallback comicReturnCallback) {
        NHTranlator.Companion.getComicById(String.valueOf(cid), context, comicReturnCallback);

    }

    /*

    E/Volley: [16306] BasicNetwork.performRequest: Unexpected response code 503 for https://nhentai.net/g/260434

    */
    public static void getComicListHistory(Context context, VolleyCallback comicListReturnCallback) {
        ArrayList<Comic> partialComics = (ArrayList<Comic>) NHTranlator.Companion.getHistoryComicList(context);
        ArrayList<Comic> comics = new ArrayList<>();

        for (Comic c :
                partialComics) {

            NHTranlator.Companion.getComicById(c.getId(), context, new VolleyCallback() {
                @Override
                public void onResponse(ArrayList<Comic> comics) {
                    comics.add(comics.get(0));
                }
            });

        }

        comicListReturnCallback.onResponse(comics);
    }

    public static void getComicListFavorite(VolleyCallback comicListReturnCallback) throws JSONException, IOException {
        JSONArray jsonArray = Storage.getCollectionsJSON();

        ArrayList<Comic> comics = getComicListByJSONArray(jsonArray);
        Collections.reverse(comics);

        comicListReturnCallback.onResponse(comics);
    }

    ///default language = chinese
    public static void getComicListDefault(int page, Context context, VolleyCallback comicListReturnCallback) {
        String language = "chinese";

        getComicList(language, page, context, comicListReturnCallback);
    }

    ///default language = chinese
    public static void getComicList(String language, int page, Context context, VolleyCallback comicListReturnCallback) {
        language += "/";
        String url = NHTranlator.Companion.getBaseUrlLanguage() + language.toLowerCase();

        NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
    }

    //some json related methods

    public static ArrayList<Comic> getComicListByJSONArray(JSONArray jsonArray) throws JSONException {
        ArrayList<Comic> comics = new ArrayList<>();
        JSONObject obj;

        for (int i = 0; i < jsonArray.length(); i++) {
            obj = jsonArray.getJSONObject(i);
            comics.add(getComicByJSONObject(obj));
        }

        return comics;
    }

    public static Comic getComicByJSONObject(JSONObject jsonObject) throws JSONException {
        Comic c = new Comic();

        c.setTitle(jsonObject.getString("title"));
        c.setThumbLink(jsonObject.getString("thumblink"));
        c.setId(jsonObject.getString("id"));
        c.setMid(c.getThumbLink().split("/")[c.getThumbLink().split("/").length - 2]);

        return c;
    }

}
