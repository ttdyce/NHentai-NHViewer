package personal.ttd.nhviewer.activity.fragment.deprecated;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import personal.ttd.nhviewer.R;
import personal.ttd.nhviewer.comic.Comic;
import personal.ttd.nhviewer.file.Storage;

public class DownloadFragment extends Fragment {

    private ListView listView;
    private Context mContext;
    private String TAG = "DownloadFragment";


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.content_download, container, false);
    }
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        setListView();
    }

    private void setListView() {
        listView = (ListView) getView().findViewById(R.id.lvDownload);
        SimpleAdapter adapter = new SimpleAdapter(mContext,getData(),R.layout.list_item,
                                  new String[]{"title","thumb"},
                                    new int[]{R.id.tvListTitle,R.id.imgListThumb});
        //settings for showing network image
        adapter.setViewBinder(new SimpleAdapter.ViewBinder() {

            @Override
            public boolean setViewValue(View view, Object data,
                                        String textRepresentation) {
                if (view instanceof ImageView) {
                    ImageView iv = (ImageView) view;

                     Glide.with(mContext)
                            .load(data.toString())
                             .into(iv);
                    return true;
                }
                return false;
            }
        });

        listView.setAdapter(adapter);
    }

    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();

        for (Comic c : Storage.getComicsFromDB(getActivity())
             ) {
            Log.i(TAG, "loaded list view data: " + c.getThumbLink());
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", c.getTitle());
            map.put("thumb", c.getThumbLink());

            list.add(map);
        }

        return list;
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        mContext = activity;
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mContext = null;
    }

}
