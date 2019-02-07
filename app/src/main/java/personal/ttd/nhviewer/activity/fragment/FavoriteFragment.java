package personal.ttd.nhviewer.activity.fragment;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.IOException;

import personal.ttd.nhviewer.comic.ComicMaker;
import personal.ttd.nhviewer.file.Storage;

public class FavoriteFragment extends ComicListDisplayerFragment {

    @Override
    protected void setComicList(int page) {
        hasPage = false;

        try {
            ComicMaker.getComicListFavorite(comicListReturnCallback);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //refresh comics when this fragment is visible
        if(isVisibleToUser && adapter != null)
            refreshRecycleView();
    }
}
