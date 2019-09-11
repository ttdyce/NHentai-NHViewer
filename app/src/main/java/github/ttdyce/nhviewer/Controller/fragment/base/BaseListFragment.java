package github.ttdyce.nhviewer.Controller.fragment.base;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import androidx.annotation.Nullable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import github.ttdyce.nhviewer.Model.ListReturnCallBack;
import github.ttdyce.nhviewer.R;
import github.ttdyce.nhviewer.glide.GlideApp;

import static com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions.withCrossFade;

public abstract class BaseListFragment extends Fragment {

    private static final int ONE_PAGE_COMIC_COUNT = 25;
    private final String TAG = "BaseListFragment";
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected BaseListAdapter adapter;
    protected SharedPreferences sharedPref;
    protected RecyclerView rvDisplayComic;
    protected FloatingActionButton fab;
    private int currentPage = 1;
    private boolean isSelectionMode = false;
    private boolean isLastPage = false;
    //provided for child class to update list
    protected ListReturnCallBack listReturnCallback = new ListReturnCallBack() {
        @Override
        public void onResponse(ArrayList list) {
            int originalSize = adapter.getDataList().size();

            if (list != null)
                if (list.size() != 0) {
                    adapter.addList(list);

                    if (originalSize != 0)
                        adapter.notifyItemRangeInserted(originalSize, list.size());
                    else
                        adapter.notifyDataSetChanged();
                } else {
                    setIsLastPage(true);
                    adapter.notifyItemRemoved(originalSize);//for removing last favorite comic, it may need checking (originalSize == 0)
                }

            swipeRefreshLayout.setRefreshing(false);
            if (((AppCompatActivity) getActivity()).getSupportActionBar() != null)
                ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getActionBarTitle());
        }
    };

    protected int getLayoutID() {
        return R.layout.content_base_list;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(getLayoutID(), container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext());
        rvDisplayComic = view.findViewById(R.id.rvBaseList);

        setSwipeRefreshLayout(view);
        setRecycleView();
        setList(currentPage);
        setFab();
    }

    @Override
    public void onPause() {
        super.onPause();
        resetMode();

        if (getIsUsingFab())
            fab.hide();

    }

    @Override
    public void onResume() {
        super.onResume();

        if (getIsUsingFab())
            fab.show();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if (isVisibleToUser && getActivity() != null) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setSubtitle(getActionBarTitle());
            setFab();
        }

        if (!isVisibleToUser)
            resetMode();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_removeFromCollection:
                if (getCanDelete()) //id -1(default) means not allowed deletion
                    for (Object o : adapter.selectedObject)
                        remove(o);

                adapter.unselectAll();
                adapter.notifyDataSetChanged();
                toggleSelectMode();
                break;

            case R.id.action_reverse:
                adapter.reverse();
                break;

            case R.id.action_jumpToPage:
                jumpToPage();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    protected abstract boolean getCanDelete();

    //o : type of getDataset<T>()
    protected void remove(Object o) {/*Override if canDelete return true*/}

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        selectMenu(menu);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        selectMenu(menu);
    }

    protected void setFab() {
        if (!getIsUsingFab())
            return;

        fab = getActivity().findViewById(R.id.fabHome);
        fab.show();
        fab.setOnClickListener(getFabOnClickListener());

    }

    protected abstract boolean getHasPage();

    protected boolean getIsLastPage() {
        return isLastPage;
    }

    protected void setIsLastPage(boolean value) {
        isLastPage = value;
    }

    protected abstract boolean getIsUsingFab();

    protected View.OnClickListener getFabOnClickListener() {
        return null;
    }

    protected abstract String getActionBarTitle();

    protected abstract void setList(int page);

    private void resetMode() {
        isSelectionMode = false;
        if (adapter != null && adapter.selectedObject != null)
            adapter.selectedObject.clear();
    }

    private void selectMenu(Menu menu) {
        MenuInflater inflater = requireActivity().getMenuInflater();

        if (isSelectionMode) {
            menu.clear();
            inflater.inflate(R.menu.selection_mode_comic_list, menu);
        }
    }

    private void toggleSelectMode() {
        requireActivity().invalidateOptionsMenu();
        isSelectionMode = !isSelectionMode;
    }

    private void jumpToPage() {
        final EditText input = new EditText(requireContext());
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());

        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        builder.setTitle("Jump to page...");
        builder.setView(input);
        builder.setPositiveButton("Go", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                int page = Integer.parseInt(input.getText().toString());

                jumpToPage(page);
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

    private void jumpToPage(int page) {
        if (getHasPage())
            refreshRecyclerView(page);
        else
            rvDisplayComic.scrollToPosition((page - 1) * ONE_PAGE_COMIC_COUNT + 1);

    }

    private void setSwipeRefreshLayout(View v) {
        swipeRefreshLayout = v.findViewById(R.id.srBaseList);

        swipeRefreshLayout.setRefreshing(true);
        swipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.colorAccent));
        swipeRefreshLayout.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        refreshRecyclerView(1);
                    }
                }
        );
    }

    protected void refreshRecyclerView(int page) {
        currentPage = page;

        adapter.clear();
        setList(currentPage);
    }

    private void setRecycleView() {
        adapter = getAdapter();
        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 3);

        rvDisplayComic.setHasFixedSize(true);
        rvDisplayComic.setAdapter(adapter);
        rvDisplayComic.setLayoutManager(layoutManager);
    }

    protected abstract BaseListAdapter getAdapter();

    public abstract class BaseListAdapter extends RecyclerView.Adapter<BaseListAdapter.BaseListViewHolder> {
        private final String TAG = "BaseListAdapter";
        private ArrayList selectedObject = new ArrayList<>();//store type of getDataset<T>()

        @Override
        public BaseListViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(getListItemLayout(), parent, false);
            return new BaseListViewHolder(v);
        }

        protected int getListItemLayout() {
            return R.layout.list_item_base;
        }

        @Override
        public int getItemCount() {
            return 0;
        }

        @Override
        public void onBindViewHolder(BaseListViewHolder holder, int position) {

            //endless scrolling list
            if (position == getDataList().size() - 1 && getHasPage() && !getIsLastPage()) {
                swipeRefreshLayout.setRefreshing(true);
                setList(++currentPage);
            }

            Object o = getDataList().get(position);

            holder.tvTitle.setText(getTitle(position));

            GlideApp.with(holder.itemView.getContext())
                    .load(getThumbLink(position))
                    .customFormat()
                    .transition(withCrossFade())
                    .into(holder.ivThumb);

            //select/unselect comics
            holder.cvComicItem.setCardBackgroundColor(selectedObject.contains(getDataList().get(position)) ? Color.RED : Color.WHITE);

            //set onClick listener
            holder.cvComicItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (isSelectionMode) {
                        //select comic
                        if (selectedObject.contains(o))
                            unselect(o, position);
                        else
                            select(o, position);

                        //end selection
                        if (selectedObject.isEmpty())
                            toggleSelectMode();
                    } else {
                        onListItemClick(position);
                    }
                }
            });

            //set onLongClick listener
            holder.cvComicItem.setLongClickable(true);
            holder.cvComicItem.setOnLongClickListener(v -> {
                toggleSelectMode();

                if (isSelectionMode)
                    select(o, position);
                else
                    unselectAll();

                return true;
            });
        }

        void select(Object o, int pos) {
            selectedObject.add(o);
            notifyItemChanged(pos);
        }

        void unselect(Object o, int pos) {
            selectedObject.remove(o);
            notifyItemChanged(pos);
        }

        void unselectAll() {
            selectedObject.clear();
            notifyDataSetChanged();
        }

        protected abstract ArrayList getDataList();

        protected abstract void onListItemClick(int position);

        protected abstract String getThumbLink(int position);

        protected abstract String getTitle(int position);

        public abstract void clear();

        public abstract void addList(ArrayList list);

        public abstract void reverse();

        protected class BaseListViewHolder extends RecyclerView.ViewHolder {
            CardView cvComicItem;
            TextView tvTitle;
            ImageView ivThumb;

            protected BaseListViewHolder(View v) {
                super(v);
                tvTitle = v.findViewById(R.id.tvTitleBaseList);
                ivThumb = v.findViewById(R.id.ivThumbBaseList);
                cvComicItem = v.findViewById(R.id.cvBaseListItem);
            }
        }


    }

}
