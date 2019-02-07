package personal.ttd.nhviewer.activity.fragment;

import personal.ttd.nhviewer.comic.ComicMaker;

public class HistoryFragment extends ComicListDisplayerFragment {
    @Override
    protected void setComicList(int page) {
        hasPage = false;

        /*

        E/Volley: [16306] BasicNetwork.performRequest: Unexpected response code 503 for https://nhentai.net/g/260434

         */
        //ComicMaker.getComicListHistory(requireContext(), comicListReturnCallback);

    }
}
