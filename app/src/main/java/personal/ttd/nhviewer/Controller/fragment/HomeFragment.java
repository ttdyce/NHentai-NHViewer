package personal.ttd.nhviewer.Controller.fragment;

import personal.ttd.nhviewer.Model.comic.ComicMaker;

public class HomeFragment extends ComicListFragment{
    public static final String SUBTITLE = "My Home";

    @Override
    protected boolean getHasPage() {
        return true;
    }

    @Override
    protected boolean getCanDelete() {
        return false;
    }

    @Override
    protected String getActionBarTitle() {
        return SUBTITLE;
    }

    @Override
    protected void setList(int page) {
        // TODO: 6/6/2019 This comic list should be configurable, using sharePreference setting
        ComicMaker.getComicListDefault(page, requireContext(),listReturnCallback, sharedPref);
    }
}
