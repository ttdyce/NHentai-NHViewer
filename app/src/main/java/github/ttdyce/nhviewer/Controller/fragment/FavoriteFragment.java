package github.ttdyce.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;

import java.io.IOException;

import github.ttdyce.nhviewer.Controller.fragment.base.ComicListFragment;
import github.ttdyce.nhviewer.Model.Saver.Saver;
import github.ttdyce.nhviewer.Model.Saver.SaverMaker;
import github.ttdyce.nhviewer.Model.comic.Comic;
import github.ttdyce.nhviewer.Model.comic.ComicMaker;

public class FavoriteFragment extends ComicListFragment {
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        //refresh comics when this fragment is visible
        if(isVisibleToUser && adapter != null)
            refreshRecyclerView(1);
    }

    @Override
    protected String getActionBarTitle() {
        return "Favorite";
    }

    @Override
    protected boolean getCanDelete() {
        return true;
    }

    @Override
    protected boolean getHasPage() {
        return false;
    }

    @Override
    protected void setList(int page) {
        try {
            ComicMaker.getComicListFavorite(listReturnCallback);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onClickFavorite(Comic c){
        Saver saver = SaverMaker.getDefaultSaver();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        try {
                            if(saver.removeFavorite(c) != null){
                                refreshRecyclerView(1);
                                Snackbar.make(getView(), "Collection removed", Snackbar.LENGTH_SHORT).show();
                            }
                        } catch (IOException|JSONException e) {
                            e.printStackTrace();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage("Are you sure to remove?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();


    }
}
