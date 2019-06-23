package personal.ttd.nhviewer.Controller.fragment;

import personal.ttd.nhviewer.Controller.fragment.base.ComicListFragment;
import personal.ttd.nhviewer.Model.comic.ComicMaker;

public class HistoryFragment extends ComicListFragment {
    @Override
    protected String getActionBarTitle() {
        return "History";
    }

    @Override
    protected boolean getHasPage() {
        return false;
    }

    @Override
    protected boolean getCanDelete() {
        return false;
    }

    @Override
    protected void setList(int page) {
        ComicMaker.getComicListHistory(requireContext(), listReturnCallback);
    }

}
