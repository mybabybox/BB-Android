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
    private CommunityMainPagerAdapter adapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.community_main_fragment, container, false);

        //getActivity().getActionBar().hide();

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.commTabs);
        viewPager = (ViewPager) view.findViewById(R.id.commPager);
        adapter = new CommunityMainPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(0);
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(adapter);
        
        tabs.setViewPager(viewPager);

        /*
        // declared in xml
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
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}

/**
 *
 */
class CommunityMainPagerAdapter extends FragmentStatePagerAdapter {

    public CommunityMainPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return "";
    }

    @Override
    public int getCount() {
        return 1;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        MyCommunityNewsfeedFragment fragment = new MyCommunityNewsfeedFragment();
        return fragment;
    }
}
