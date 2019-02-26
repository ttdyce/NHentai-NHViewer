package personal.ttd.nhviewer.activity.fragment;

import android.os.Bundle;

import personal.ttd.nhviewer.comic.ComicMaker;

public class SearchableFragment extends ComicListDisplayerFragment{

    @Override
    protected void setComicList(int page) {
        Bundle bundle = getArguments();
        String query = "";

        if(bundle != null)
            query = bundle.getString("query");

        ComicMaker.getComicListQuery(query, page, requireContext(), comicListReturnCallback);
    }
}
