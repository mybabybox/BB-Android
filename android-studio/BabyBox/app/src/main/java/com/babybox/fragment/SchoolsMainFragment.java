package com.babybox.fragment;

import android.app.ActionBar;
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

public class SchoolsMainFragment extends TrackedFragment {

    private static final String TAG = SchoolsMainFragment.class.getName();
    private ViewPager viewPager;
    private SchoolsPagerAdapter mAdapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.schools_main_fragment, container, false);

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.tabs);
        viewPager = (ViewPager) view.findViewById(R.id.pager);
        mAdapter = new SchoolsPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(2);
        viewPager.setPageMargin(pageMargin);
        viewPager.setAdapter(mAdapter);

        tabs.setViewPager(viewPager);
        tabs.setTextColor(getResources().getColor(R.color.dark_gray));
        tabs.setIndicatorColor(getResources().getColor(R.color.pn_box_border));

        tabs.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                tabs.setIndicatorColor(getResources().getColor(R.color.pn_box_border));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        int indicatorHeight = ViewUtil.getRealDimension(5);
        tabs.setIndicatorHeight(indicatorHeight);

        int textSize = ViewUtil.getRealDimension(16);
        tabs.setTextSize(textSize);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public boolean allowBackPressed() {
        TrackedFragment fragment = mAdapter.getFragment(viewPager.getCurrentItem());
        Log.d(this.getClass().getSimpleName(), "allowBackPressed: call "+fragment.getClass().getSimpleName());

        if (fragment != null)
            return fragment.allowBackPressed();
        return super.allowBackPressed();
    }
}

class SchoolsPagerAdapter extends FragmentStatePagerAdapter {

    public static final int PN_PAGE = 0;

    private TrackedFragment pnFragment;

    private static String[] TITLES = {
            AppController.getInstance().getString(R.string.schools_tab_title_pn)
    };

    public SchoolsPagerAdapter(FragmentManager fm) {
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
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            case PN_PAGE:
            default:
                pnFragment = new SchoolsPNFragment();
                return pnFragment;
        }
    }

    public TrackedFragment getFragment(int position) {
        switch (position) {
            case PN_PAGE:
            default:
                return pnFragment;
        }
    }
}
