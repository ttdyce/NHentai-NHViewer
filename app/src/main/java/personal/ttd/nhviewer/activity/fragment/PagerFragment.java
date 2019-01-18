package personal.ttd.nhviewer.activity.fragment;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import personal.ttd.nhviewer.R;

public class PagerFragment extends Fragment {

    private static final int NUM_PAGES = 2;
    public View myRoot;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    private Context mContext;

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
        setPager();
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

    private void setPager() {
        mPager = (ViewPager) getView().findViewById(R.id.pagerMain);
        mPagerAdapter = new ScreenSlidePagerAdapter(getChildFragmentManager());
//        mPagerAdapter = new ScreenSlidePagerAdapter(myContext.getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);

        mPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            private int enterMainCount = 0;

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);

                //swipe right to open navigation drawer
                DrawerLayout drawer = getActivity().findViewById(R.id.drawer_layout);
                if(position == 0 && positionOffset == 0){
                    enterMainCount++;
                    if(enterMainCount>1)
                        drawer.openDrawer(Gravity.LEFT);
                }
            }

            @Override
            public void onPageSelected(int position) {
                //setTitle text
                switch (position) {
                    case 0:
                        ((AppCompatActivity) mContext).getSupportActionBar().setSubtitle("Main");
                        break;
                    case 1:
                        ((AppCompatActivity) mContext).getSupportActionBar().setSubtitle("Collection");
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);

                //reset the counting, for swipe right checking
                enterMainCount = 0;
            }
        });
    }


    /**
     * A simple pager adapter that represents 5 ScreenSlidePageFragment objects, in
     * sequence.
     */
    private class ScreenSlidePagerAdapter extends FragmentPagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MainFragment();
                case 1:
                    return new CollectionFragment();

                default:
                    return new MainFragment();
            }
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

    }
}
