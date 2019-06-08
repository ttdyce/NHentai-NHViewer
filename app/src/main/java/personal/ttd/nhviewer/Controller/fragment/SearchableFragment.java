package personal.ttd.nhviewer.Controller.fragment;

import android.os.Bundle;

import personal.ttd.nhviewer.Model.comic.ComicMaker;

public class SearchableFragment extends ComicListFragment{
    @Override
    protected String getActionBarTitle() {
        return "Search result";
    }

    @Override
    protected boolean getCanDelete() {
        return false;
    }

    @Override
    protected boolean getHasPage() {
        return true;
    }

    @Override
    protected void setList(int page) {
        Bundle bundle = getArguments();
        String query = "";

        if(bundle != null)
            query = bundle.getString("query");

        ComicMaker.getComicListQuery(query, page, requireContext(), listReturnCallback, sharedPref);
    }

}
