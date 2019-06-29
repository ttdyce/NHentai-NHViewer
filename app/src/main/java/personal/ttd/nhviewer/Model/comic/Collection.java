package personal.ttd.nhviewer.Model.comic;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import org.json.JSONException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import personal.ttd.nhviewer.Model.Saver.JSONSaver;
import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Controller.fragment.CollectionFragment;
import personal.ttd.nhviewer.Controller.fragment.FavoriteFragment;
import personal.ttd.nhviewer.Controller.fragment.HistoryFragment;

public class Collection {

    /*
    static field
     */

    public static final String PARENT_DIRECTORY_NAME = "NH";
    public static final String REMOVED_DIRECTORY_NAME = "removed";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_TITLE = "title";
    public static final String COLUMN_THUMB_LINK = "thumblink";

    public static final int HISTORY_ID = 0;
    public static final int FAVARITE_ID = 1;
    public static final int CUSTOM_ID_START = 2;
    public static int NEXT_CUSTOM_ID = CUSTOM_ID_START;
    //    public static final String HISTORY_NAME = "History";
//    public static final String FAVORITE_NAME = "Favorite";
    public static HashMap<Integer, String> NAME_LIST = new HashMap<Integer, String>() {
        {
            put(HISTORY_ID, "History");
            put(FAVARITE_ID, "Favorite");

        }
    };

    public static HashMap<Integer, Fragment> FRAGMENT_LIST = new HashMap<Integer, Fragment>() {
        {
            put(HISTORY_ID, new HistoryFragment());
            put(FAVARITE_ID, new FavoriteFragment());
        }
    };

    public static void loadCollection() throws IOException, JSONException {
        JSONSaver saver = SaverMaker.getJSONSaver();

        for (Collection c :
                saver.getCollectionList()) {
            addCollection(c.id, c.name);
        }

    }

    public static boolean addCollection(int id, String name) {
        if (NAME_LIST.put(id, name) == null)//true means put successfully
        {
            addFragment(id);
            NEXT_CUSTOM_ID++;
            return true;
        }

        return false;//collection exists with the same id
    }

    private static void addFragment(int id) {
        CollectionFragment f = new CollectionFragment();
        Bundle args = new Bundle();

        args.putInt("id", id);
        f.setArguments(args);

        FRAGMENT_LIST.put(id, f);
    }

    /*
    non-static field
     */

    public int id;
    public String name;
    public ArrayList<Comic> comicList;

    public Collection(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Collection() {

    }

}
