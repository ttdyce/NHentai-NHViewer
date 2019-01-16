package personal.ttd.nhviewer.activity.fragment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import personal.ttd.nhviewer.api.MyApi;
import personal.ttd.nhviewer.comic.Comic;

public class HistoryFragment extends DisplayComicFragment {
    @Override
    protected void getFreshComicToDisplay() {
        for (Comic c :
                MyApi.Companion.getHistory(mContext)) {
            String link = MyApi.Companion.getComicInfoLink(c.getId());
            JsonTask jsonTask = new JsonTask();

            jsonTask.execute(link);
            /*
             * jsonTask will then call setComicToDisplay(...) below
             * */
        }

    }

    /*
     * example site: https://nhentai.net/api/gallery/248981
     * */
    @Override
    protected void setComicToDisplay(ArrayList<JSONObject> jsonObjects) {
        //jsonObjects is returned by jsonTask, from specified comicInfoLink
        for (JSONObject jo :
                jsonObjects) {
            String id, mid, title;
            StringBuilder types = new StringBuilder();
            int totalPage;

            try {
                JSONArray pageJA = jo.getJSONObject("images").getJSONArray("pages");
                for (int i = 0; i < pageJA.length(); i++) {
                    types.append(pageJA.getJSONObject(i).getString("t"));
                }

                id = jo.getString("id");
                mid = jo.getString("media_id");
                title = jo.getJSONObject("title").getString("japanese");
                totalPage = jo.getInt("num_pages");
//                seenPage = jo.getInt("seenPage");

                Comic comic = new Comic();
                comic.setId(id);
                comic.setMid(mid);
                comic.setTitle(title);
                comic.setTotalPage(totalPage);
                comic.setTypes(types.toString());
//                comic.setSeenPage(seenPage);
                comic.setPages(MyApi.Companion.getPages(mid, types.toString(), totalPage));

                comicsToDisplay.add(comic);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        super.setComicToDisplay(jsonObjects);
    }

}

//        try {
//            arr = new JSONArray(Storage.getCollections());
//            for (int i = 0; i < arr.length(); i++) {
//                Comic c = new Comic();
//                JSONObject obj;
//                try {
//                    obj = arr.getJSONObject(i);
//                    c.setTitle(obj.getString("title"));
//                    c.setThumbLink(obj.getString("thumblink"));
//                    c.setId(obj.getString("id"));
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//
//                comics.add(c);
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.i("arr", "getFreshComicToDisplay: arr: " + arr);
//
//
//        mySwipeRefreshLayout.setRefreshing(false);
//        //
//        Collections.reverse(comics);