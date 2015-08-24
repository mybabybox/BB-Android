package com.babybox.fragment;

import android.app.Activity;
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
import com.babybox.util.ViewUtil;

public class CommunityMainFragment extends TrackedFragment {

    private static final String TAG = CommunityMainFragment.class.getName();
    private ViewPager viewPager;
    private CommunityMainPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_main_fragement, container, false);

        getActivity().getActionBar().hide();

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new CommunityMainPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(2, this.getResources());
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);
        
        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.actionbar_selected_text));

        int indicatorHeight = ViewUtil.getRealDimension(5, this.getResources());
        tabs.setIndicatorHeight(indicatorHeight);

        final int textSize = ViewUtil.getRealDimension(16, this.getResources());
        tabs.setTextSize(textSize);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}

/**
 * Main tabs
 */
class CommunityMainPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_TABS = 3;
    private static String[] TITLES;

    public CommunityMainPagerAdapter(FragmentManager fm) {
        super(fm);

        TITLES = new String[NUM_TABS];
        TITLES[0] = AppController.getInstance().getString(R.string.main_tab_explore);
        TITLES[1] = AppController.getInstance().getString(R.string.main_tab_following);
        TITLES[2] = AppController.getInstance().getString(R.string.main_tab_trending);
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
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        MyCommunityNewsfeedFragment fragment = new MyCommunityNewsfeedFragment();
        return fragment;

        /*
        switch (position) {
            case 0: {
                MyCommunityFragment fragment = new MyCommunityFragment();
                return fragment;
            }
            default: {
                TopicCommunityFragment fragment = new TopicCommunityFragment();
                fragment.setTrackedOnce();
                fragment.setCommunities(LocalCommunityTabCache.getCommunityCategoryMapList().get(position).communities);
                return fragment;
            }
        }
        */
    }
}
