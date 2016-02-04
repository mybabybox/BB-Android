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
import com.babybox.app.TrackedFragment;
import com.babybox.util.FeedFilter;
import com.babybox.util.ViewUtil;

public class SellerMainFragment extends TrackedFragment {

    private ViewPager viewPager;
    private SellerMainPagerAdapter adapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.seller_main_fragment, container, false);

        // pager

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.sellerTabs);
        viewPager = (ViewPager) view.findViewById(R.id.sellerPager);
        adapter = new SellerMainPagerAdapter(getChildFragmentManager());

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
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}

/**
 * https://guides.codepath.com/android/Sliding-Tabs-with-PagerSlidingTabStrip
 * https://android-arsenal.com/details/1/1100
 */
class SellerMainPagerAdapter extends FragmentStatePagerAdapter {

    public SellerMainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return ViewUtil.SELLER_MAIN_TITLES[position];
    }

    @Override
    public int getCount() {
        return ViewUtil.SELLER_MAIN_TITLES.length;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item position=" + position);

        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        switch (position) {
            // Seller
            case 0: {
                fragment = new SellerFeedFragment();
                break;
            }
            // Following
            case 1: {
                bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, FeedFilter.FeedType.HOME_FOLLOWING.name());
                fragment = new SellerFollowingFeedViewFragment();
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

