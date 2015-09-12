package com.babybox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.babybox.R;
import com.babybox.app.CategoryCache;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.view.AdaptiveViewPager;
import com.babybox.viewmodel.CategoryVM;

import java.util.List;

public class HomeExploreFeedViewFragment extends FeedViewFragment {

    private static final String TAG = HomeExploreFeedViewFragment.class.getName();

    private AdaptiveViewPager viewPager;
    private HomeCategoryPagerAdapter adapter;

    private LinearLayout dotsLayout;

    private FrameLayout tipsLayout;
    private ImageView dismissTipsButton;

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.home_explore_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        viewPager = (AdaptiveViewPager) getHeaderView(inflater).findViewById(R.id.catPager);
        dotsLayout = (LinearLayout) getHeaderView(inflater).findViewById(R.id.dots);

        int pageMargin = ViewUtil.getRealDimension(0);
        viewPager.setPageMargin(pageMargin);

        // init adapter
        adapter = new HomeCategoryPagerAdapter(getChildFragmentManager());
        viewPager.setAdapter(adapter);

        // tips
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.HOME_EXPLORE_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.HOME_EXPLORE_TIPS);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }

        viewPager.setCurrentItem(0);

        // pager indicator
        addDots(adapter.getCount(), dotsLayout, viewPager);

        return view;
    }
}

class HomeCategoryPagerAdapter extends FragmentStatePagerAdapter {

    public static final int CATEGORIES_PER_PAGE = 6;

    private String title;

    public HomeCategoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title;
    }

    @Override
    public int getCount() {
        int count = (int) Math.ceil((double) CategoryCache.getCategories().size() / (double) CATEGORIES_PER_PAGE);
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(this.getClass().getSimpleName(), "getItem: item - " + position);
        switch (position) {
            default: {
                HomeCategoryPagerFragment fragment = new HomeCategoryPagerFragment();
                //fragment.setTrackedOnce();
                fragment.setCategories(getCategoriesForPage(position));
                return fragment;
            }
        }
    }

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    private List<CategoryVM> getCategoriesForPage(int position) {
        int start = position * CATEGORIES_PER_PAGE;
        int end = start + CATEGORIES_PER_PAGE;

        List<CategoryVM> categories = CategoryCache.getCategories();
        if (start >= categories.size()) {
            Log.e(this.getClass().getSimpleName(), "getCategoriesForPage: position out of bound... position="+position+" categories.size="+categories.size());
            return null;
        }

        if (end >= categories.size()) {
            end = categories.size();
        }

        return categories.subList(start, end);
    }
}

