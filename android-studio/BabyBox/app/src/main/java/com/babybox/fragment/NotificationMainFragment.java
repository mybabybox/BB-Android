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
import com.babybox.util.FeedFilter;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

public class NotificationMainFragment extends TrackedFragment {

    private RelativeLayout profileLayout;
    private ImageView profileImage;
    private TextView userNameText;
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
        userNameText = (TextView) actionBarView.findViewById(R.id.userNameText);

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
        userNameText.setText(UserInfoCache.getUser().getDisplayName());

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
                Intent intent = new Intent(NotificationMainFragment.this.getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(NotificationMainFragment.this.getActivity(), NewPostActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
                intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromHomeActivity");
                startActivity(intent);
            }
        });

        // pager

        tabs = (PagerSlidingTabStrip) view.findViewById(R.id.homeTabs);
        viewPager = (ViewPager) view.findViewById(R.id.homePager);
        adapter = new HomeMainPagerAdapter(getChildFragmentManager());

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
class NotificationMainPagerAdapter extends FragmentStatePagerAdapter {

    private static String[] TITLES = new String[] {
            AppController.getInstance().getString(R.string.notification_tab_conversation),
            AppController.getInstance().getString(R.string.notification_tab_activity)
    };

    public NotificationMainPagerAdapter(FragmentManager fm) {
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