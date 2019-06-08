package personal.ttd.nhviewer.Model.comic;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import personal.ttd.nhviewer.Controller.fragment.SettingFragment;
import personal.ttd.nhviewer.Model.Saver.Saver;
import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Model.ListReturnCallBack;
import personal.ttd.nhviewer.Model.api.NHTranlator;

public class ComicMaker {

    public static void getComicById(String cid, Context context, ListReturnCallBack comicReturnCallback) {
        NHTranlator.Companion.getComicById(cid, context, comicReturnCallback);

    }

    public static void getComicListHistory(Context context, ListReturnCallBack comicListReturnCallback) {
        Saver saver = SaverMaker.getDefaultSaver();
        Collection history = saver.getHistory();

        comicListReturnCallback.onResponse(history.comicList);
    }

    public static void getComicListFavorite(ListReturnCallBack comicListReturnCallback) throws JSONException, IOException {
        Saver saver = SaverMaker.getDefaultSaver();
        Collection favorite = saver.getFavorite();

        comicListReturnCallback.onResponse(favorite.comicList);
    }

    ///default language = chinese
    public static void getComicListQuery(String query, int page, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {
        String defaultLanguage = " " + pref.getString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, "");;
        String url = NHTranlator.Companion.getSearchBaseUrl() + query + defaultLanguage;

        NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
    }

    ///default language = all language
    public static void getComicListDefault(int page, Context context, ListReturnCallBack comicListReturnCallback, SharedPreferences pref) {
        String language = pref.getString(SettingFragment.KEY_PREF_DEFAULT_LANGUAGE, "");

        if(!language.equals("All") && !language.equals(""))
            getComicListByLanguage(language, page, context, comicListReturnCallback);
        else
            getComiListAll(page, context, comicListReturnCallback);
    }

    public static void getComicListByLanguage(String language, int page, Context context, ListReturnCallBack comicListReturnCallback) {
        language += "/";
        String url = NHTranlator.Companion.getBaseUrlLanguage() + language.toLowerCase();

        NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
//        DM5Translator.Companion.getComics(context, comicListReturnCallback);
    }

    public static void getComiListAll(int page, Context context, ListReturnCallBack comicListReturnCallback) {
        String url = NHTranlator.Companion.getBaseUrl();

        NHTranlator.Companion.getComicsBySite(url, String.valueOf(page), context, comicListReturnCallback);
//        DM5Translator.Companion.getComics(context, comicListReturnCallback);
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

        c.setTitle(jsonObject.getString(Collection.COLUMN_TITLE));
        c.setThumbLink(jsonObject.getString(Collection.COLUMN_THUMB_LINK));
        c.setId(jsonObject.getString(Collection.COLUMN_ID));
        //c.setMid(c.getThumbLink().split("/")[c.getThumbLink().split("/").length - 2]);
        // TODO: 6/2/2019  thumblink may not be set, error will thrown: java.lang.ArrayIndexOutOfBoundsException: length=1; index=-1

        return c;
    }

}
