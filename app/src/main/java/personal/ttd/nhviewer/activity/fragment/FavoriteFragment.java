package personal.ttd.nhviewer.activity.fragment;

import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;

import org.json.JSONException;

import java.io.IOException;

import personal.ttd.nhviewer.Saver.Saver;
import personal.ttd.nhviewer.Saver.SaverMaker;
import personal.ttd.nhviewer.Saver.file.Storage;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.comic.ComicMaker;

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

    @Override
    public void collectButtonOnClick(Comic c, int position){
        Saver saver = SaverMaker.getDefaultSaver();
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        if(saver.removeFavorite(String.valueOf(position))){
                            refreshRecycleView();
                            Snackbar.make(getView(), "Collection removed", Snackbar.LENGTH_SHORT).show();
                        }
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        //No button clicked
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setMessage(String.format("Are you sure to remove?")).setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();



    }
}
