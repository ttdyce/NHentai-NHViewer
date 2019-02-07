package personal.ttd.nhviewer.activity.fragment;

import personal.ttd.nhviewer.comic.ComicMaker;

public class HomeFragment extends ComicListDisplayerFragment{

    @Override
    protected void setComicList(int page) {
        ComicMaker.getComicListDefault(page, requireContext(), comicListReturnCallback);
    }
}
