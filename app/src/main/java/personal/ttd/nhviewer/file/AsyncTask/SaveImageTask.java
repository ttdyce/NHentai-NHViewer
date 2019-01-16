package personal.ttd.nhviewer.file.AsyncTask;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.file.Storage;

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
