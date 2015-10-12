package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.astuetz.PagerSlidingTabStrip;
import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.util.FeedFilter;
import com.babybox.util.ViewUtil;

public class ActivityMainFragment extends TrackedFragment {

    private ViewPager viewPager;
    private ActivityMainPagerAdapter adapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        // pager

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.homeTabs);
        viewPager = (ViewPager) view.findViewById(R.id.homePager);
        adapter = new ActivityMainPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(0);
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(adapter);

        tabs.setViewPager(viewPager);

        /*
        // styles declared in xml
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.actionbar_selected_text));

        int indicatorHeight = ViewUtil.getRealDimension(5, this.getResources());
        tabs.setIndicatorHeight(indicatorHeight);

        final int textSize = ViewUtil.getRealDimension(16, this.getResources());
        tabs.setTextSize(textSize);
        */

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}

/**
 * https://guides.codepath.com/android/Sliding-Tabs-with-PagerSlidingTabStrip
 * https://android-arsenal.com/details/1/1100
 */
class ActivityMainPagerAdapter extends FragmentStatePagerAdapter {

    private static String[] TITLES = new String[] {
            AppController.getInstance().getString(R.string.main_tab_explore),
            AppController.getInstance().getString(R.string.main_tab_following)
    };

    public ActivityMainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return TITLES[position];
    }

    @Override
    public int getCount() {
        return TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        switch (position) {
            // Explore
            case 0: {
                bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, FeedFilter.FeedType.HOME_EXPLORE.name());
                fragment = new HomeExploreFeedViewFragment();
                break;
            }
            // Following
            case 1: {
                bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, FeedFilter.FeedType.HOME_FOLLOWING.name());
                fragment = new FeedViewFragment();
                break;
            }
            default: {
                Log.e(this.getClass().getSimpleName(), "getItem: Unknown item position=" + position);
                break;
            }
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            fragment.setTrackedOnce();
        }
        return fragment;
    }
}