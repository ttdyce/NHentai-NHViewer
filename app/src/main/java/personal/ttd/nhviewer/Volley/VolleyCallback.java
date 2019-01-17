package personal.ttd.nhviewer.Volley;

import java.util.ArrayList;

import personal.ttd.nhviewer.comic.Comic;

public interface VolleyCallback {
    void onResponse(ArrayList<Comic> comics);
}
