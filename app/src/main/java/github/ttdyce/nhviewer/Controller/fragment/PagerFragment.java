package github.ttdyce.nhviewer.Controller.fragment;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import github.ttdyce.nhviewer.R;

public class PagerFragment extends Fragment {

    private static final int PAGER_NUM = 2;
    public View myRoot;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (myRoot == null) {
            myRoot = inflater.inflate(R.layout.fragment_pager, container, false);
        }
        return myRoot;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initPager();
    }

    private void initPager() {
        ViewPager mPager = getView().findViewById(R.id.pagerMain);
        PagerAdapter mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
        mPager.setAdapter(mPagerAdapter);

    }

    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new HomeFragment();
                case 1:
                    return new FavoriteFragment();
            }
            return new HomeFragment();

        }

        @Override
        public int getCount() {
            return PAGER_NUM;
        }


    }
}
