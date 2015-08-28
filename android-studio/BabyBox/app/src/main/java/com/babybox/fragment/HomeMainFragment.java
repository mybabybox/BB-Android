package com.babybox.fragment;

import android.app.ActionBar;
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
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.astuetz.PagerSlidingTabStrip;
import com.babybox.R;
import com.babybox.activity.GameActivity;
import com.babybox.activity.MainActivity;
import com.babybox.activity.NewPostActivity;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

public class HomeMainFragment extends TrackedFragment {

    private RelativeLayout profileLayout;
    private ImageView profileImage;
    private TextView usernameText;
    private ImageView signInImage;
    private ImageView newPostIcon;
    private View actionBarView;

    private ViewPager viewPager;
    private HomeMainPagerAdapter adapter;
    private PagerSlidingTabStrip tabs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.home_main_fragment, container, false);

        actionBarView = inflater.inflate(R.layout.home_main_actionbar, null);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getActivity().getActionBar().setCustomView(actionBarView, lp);
        getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().show();

        setHasOptionsMenu(true);

        profileLayout = (RelativeLayout) actionBarView.findViewById(R.id.profileLayout);
        profileImage = (ImageView) actionBarView.findViewById(R.id.profileImage);
        usernameText = (TextView) actionBarView.findViewById(R.id.usernameText);

        newPostIcon = (ImageView) actionBarView.findViewById(R.id.newPostIcon);
        signInImage = (ImageView) actionBarView.findViewById(R.id.signInImage);

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(mascotIcon);
            }
        }, 2000);
        */

        // profile
        ImageUtil.displayThumbnailProfileImage(UserInfoCache.getUser().getId(), profileImage);
        usernameText.setText(UserInfoCache.getUser().getDisplayName());

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null) {
                    mainActivity.pressProfileTab();
                }
            }
        });

        signInImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch game
                Intent intent = new Intent(HomeMainFragment.this.getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(HomeMainFragment.this.getActivity(), NewPostActivity.class);
                intent.putExtra("id",0L);
                intent.putExtra("flag","FromHomeActivity");
                startActivity(intent);
            }
        });

        // pager

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.homeTabs);
        viewPager = (ViewPager) view.findViewById(R.id.homePager);
        adapter = new HomeMainPagerAdapter(getChildFragmentManager());

        int pageMargin = ViewUtil.getRealDimension(0, this.getResources());
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
}

/**
 * https://guides.codepath.com/android/Sliding-Tabs-with-PagerSlidingTabStrip
 * https://android-arsenal.com/details/1/1100
 */
class HomeMainPagerAdapter extends FragmentStatePagerAdapter {

    public static final int NUM_TABS = 3;
    private static String[] TITLES;

    public HomeMainPagerAdapter(FragmentManager fm) {
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

        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        switch (position) {
            // Explore
            case 0: {
                bundle.putString("key", "home_explore");
                fragment = new HomeFeedViewFragment();
            }
            // Following
            case 1: {
                bundle.putString("key", "home_following");
                fragment = new HomeFeedViewFragment();
            }
            // Trending
            case 2: {
                bundle.putString("key", "home_trending");
                fragment = new HomeFeedViewFragment();
            }
            default: {

            }
        }

        if (fragment != null) {
            fragment.setArguments(bundle);
            fragment.setTrackedOnce();
        }
        return fragment;
    }
}

