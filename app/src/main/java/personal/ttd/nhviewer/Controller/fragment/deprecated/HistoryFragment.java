package personal.ttd.nhviewer.Controller.fragment.deprecated;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import personal.ttd.nhviewer.Model.api.NHTranlator;
import personal.ttd.nhviewer.Model.comic.Comic;

public class HistoryFragment extends DisplayComicFragment {
    @Override
    protected void getFreshComicToDisplay() {
        for (Comic c :
                NHTranlator.Companion.getHistoryComicList(mContext)) {
            String link = NHTranlator.Companion.getComicInfoLink(c.getId());
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
                comic.setPageTypes(types.toString());
//                comic.setSeenPage(seenPage);
                comic.setPages(NHTranlator.Companion.getPages(mid, types.toString(), totalPage));

                comicsToDisplay.add(comic);
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
        super.setComicToDisplay(jsonObjects);
    }

}

//        try {
//            arr = new JSONArray(Storage.getCollectionsJSON());
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
//                comicList.add(c);
//            }
//        } catch (IOException | JSONException e) {
//            e.printStackTrace();
//        }
//
//        Log.i("arr", "setList: arr: " + arr);
//
//
//        swipeRefreshLayout.setRefreshing(false);
//        //
//        Collections.reverse(comicList);