package github.ttdyce.nhviewer.Controller.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;

import github.ttdyce.nhviewer.Controller.fragment.base.ComicListFragment;
import github.ttdyce.nhviewer.Model.comic.ComicMaker;
import github.ttdyce.nhviewer.R;

public class SearchableFragment extends ComicListFragment {
    private boolean sortByPopular = false;

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

        ComicMaker.getComicListQuery(query, page, sortByPopular, requireContext(), listReturnCallback, sharedPref);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        super.onOptionsItemSelected(item);

        int id = item.getItemId();

        if (id == R.id.action_sort) {
            sortByPopular = !sortByPopular;
            refreshRecyclerView(1);
        }

        return true;
    }
}
