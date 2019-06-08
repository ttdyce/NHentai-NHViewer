package personal.ttd.nhviewer.Controller.fragment;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import personal.ttd.nhviewer.Controller.MainActivity;
import personal.ttd.nhviewer.Controller.fragment.base.BaseListFragment;
import personal.ttd.nhviewer.Model.comic.Collection;
import personal.ttd.nhviewer.Model.comic.CollectionMaker;
import personal.ttd.nhviewer.Model.comic.CollectionTool;
import personal.ttd.nhviewer.Model.comic.Comic;

public class CollectionListFragment extends BaseListFragment {

    protected CollectionListAdapter adapter = new CollectionListAdapter();
    protected String appbarTitle = "Collection List";

    @Override
    protected boolean getHasPage() {
        return false;
    }

    @Override
    protected boolean getCanDelete() {
        return true;
    }

    @Override
    protected void setList(int page) {
        CollectionMaker.getCollectionAll(listReturnCallback);
    }

    @Override
    protected BaseListAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected String getActionBarTitle() {
        return appbarTitle;
    }

    @Override
    protected boolean getIsFabVisible() {
        return true;
    }

    @Override
    protected void remove(Object o) {
        Collection collection = (Collection) o;
        int id = collection.id;
        String removed = "N/A";

        try {
            removed = CollectionTool.removeCollectionList(id);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Snackbar.make(getView(), String.format("Removed collection \"%s\".", removed), Snackbar.LENGTH_SHORT);

        adapter.collections.remove(collection);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(SettingFragment.KEY_PREF_DEFAULT_COLLECTION_ID, String.valueOf(Collection.FAVARITE_ID));
        editor.apply();
    }

    @Override
    protected View.OnClickListener getFabOnClickListener() {
        return v -> openCollectionAdderDialog();
    }

    private void openCollectionAdderDialog() {
        final EditText input = new EditText(requireContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        input.setInputType(InputType.TYPE_CLASS_TEXT);

        builder.setTitle("New collection name");
        builder.setView(input);

        builder.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String collectionName = input.getText().toString();

                if (CollectionTool.addCollectionList(collectionName)) {
                    Snackbar.make(getView(), String.format("\"%s\" is created", collectionName), Snackbar.LENGTH_SHORT).show();
                    refreshRecyclerView(1);
                } else
                    Snackbar.make(getView(), "Failed, no collection is added", Snackbar.LENGTH_SHORT).show();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    //Data adapter
    private class CollectionListAdapter extends BaseListAdapter {
        ArrayList<Collection> collections = new ArrayList<>();

        @Override
        protected void onListItemClick(int position) {
            FragmentManager fm = getFragmentManager();
            Fragment f = Collection.FRAGMENT_LIST.get(position);

            android.support.v4.app.FragmentTransaction transaction = fm.beginTransaction();
            transaction.replace(MainActivity.FRAME_HOME, f);
            transaction.addToBackStack(null);
            transaction.commit();

        }

        @Override
        protected ArrayList getDataList() {
            return collections;
        }

        @Override
        protected String getThumbLink(int position) {
            Collection c = collections.get(position);

            if (c.comicList == null || c.comicList.size() == 0)
                return "";

            Comic comic = c.comicList.get(0);
            return comic.getThumbLink();
        }

        @Override
        protected String getTitle(int position) {
            int collectionid = collections.get(position).id;

            return Collection.NAME_LIST.get(collectionid);
        }

        @Override
        public void clear() {
            collections.clear();
        }

        @Override
        public void addList(ArrayList list) {
            collections = list;
        }

        @Override
        public int getItemCount() {
            return collections.size();
        }

        public void reverse() {
            Collections.reverse(collections);
        }
    }

}

