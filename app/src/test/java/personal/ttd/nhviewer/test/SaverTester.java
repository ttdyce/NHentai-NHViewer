package personal.ttd.nhviewer.test;

import org.junit.Test;

import personal.ttd.nhviewer.Model.Saver.SaverMaker;
import personal.ttd.nhviewer.Model.comic.Comic;

public class SaverTester  {

    @Test
    public void getComic(){
        int x = 0;

        assert SaverMaker.getDefaultSaver().addCollection(-1, new Comic());
        assert x == 1;
    }
}
