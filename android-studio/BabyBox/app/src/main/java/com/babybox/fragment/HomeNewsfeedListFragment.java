package com.babybox.fragment;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.babybox.R;
import com.babybox.app.LocalCommunityTabCache;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;

public class HomeNewsfeedListFragment extends NewsfeedListFragment {

    private static final String TAG = HomeNewsfeedListFragment.class.getName();

    private ViewPager viewPager;
    private MyCommunityPagerAdapter mAdapter;

    private LinearLayout dotsLayout;

    private FrameLayout tipsLayout;
    private ImageView cancelTipsButton;

    private View headerView;

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.home_newsfeed_list_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        viewPager = (ViewPager) headerView.findViewById(R.id.commsPager);
        dotsLayout = (LinearLayout) view.findViewById(R.id.dots);

        int pageMargin = ViewUtil.getRealDimension(2, this.getResources());
        viewPager.setPageMargin(pageMargin);

        // init adapter
        mAdapter = new MyCommunityPagerAdapter(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, getChildFragmentManager());
        viewPager.setAdapter(mAdapter);

        init();

        // tips
        //SharedPreferencesUtil.getInstance().saveBoolean(SharedPreferencesUtil.Screen.MY_NEWSFEED_TIPS.name(), false);
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.MY_NEWSFEED_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            cancelTipsButton = (ImageView) headerView.findViewById(R.id.cancelTipsButton);
            cancelTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.MY_NEWSFEED_TIPS);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }

        return view;
    }

    public void notifyChange() {
        mAdapter.notifyDataSetChanged();
        viewPager.invalidate();
    }

    private void init() {
        mAdapter.setCommunityTabType(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY);
        notifyChange();

        viewPager.setCurrentItem(0);

        // pager indicator
        addDots(mAdapter.getCount(), dotsLayout, viewPager);
    }
}

