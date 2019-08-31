package github.ttdyce.nhviewer.Model.Saver.file.AsyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import github.ttdyce.nhviewer.Model.comic.Comic;
import github.ttdyce.nhviewer.Model.Saver.file.Storage;

public class SaveImageTask extends AsyncTask<Integer, Integer, Integer> {
    private Bitmap bmp;
    private int pos;
    private Comic c;

    public SaveImageTask(Comic comic, int position, Bitmap resource){
        super();
        this.bmp = resource;
        this.pos = position;
        this.c = comic;
    }

    protected Integer doInBackground(Integer... urls) {
        Storage.saveImage(c, pos, bmp);
        return 0;
    }
}
