package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.babybox.adapter.ActivityListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragment;
import com.babybox.listener.EndlessScrollListener;
import com.babybox.listener.InfiniteScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ActivityVM;
import com.babybox.viewmodel.SellerVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ActivityMainFragment extends TrackedFragment {

    private static final String TAG = ActivityMainFragment.class.getName();

    protected ListView listView;
    protected TextView tipText;

    protected ImageView backImage;
    protected PullToRefreshView pullListView;

    protected List<ActivityVM> items;
    protected ActivityListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        tipText = (TextView) view.findViewById(R.id.tipText);
        listView = (ListView) view.findViewById(R.id.activityList);

        items = new ArrayList<>();
        adapter = new ActivityListAdapter(getActivity(), items);
        listView.setAdapter(adapter);

        // pull refresh
        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        if (pullListView != null) {
            pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pullListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullListView.setRefreshing(false);
                            getActivities(0L);
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        attachEndlessScrollListener();

        getActivities(0L);

        return view;
    }

    protected void attachEndlessScrollListener() {
        listView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getActivities(page - 1);
            }

            @Override
            public void onScrollUp() {
                MainActivity.getInstance().showBottomMenuBar(false);
            }

            @Override
            public void onScrollDown() {
                MainActivity.getInstance().showBottomMenuBar(true);
            }
        });
    }

    protected void markRead() {
        // actual activities are marked as read after getActivities() in server
        NotificationCounter.resetActivitiesCount();
    }

    protected void getActivities(final long offset) {
        Log.d(TAG, "getActivities() offset="+offset);

        ViewUtil.showSpinner(getActivity());
        AppController.getApiService().getActivites(offset, new Callback<List<ActivityVM>>() {
            @Override
            public void success(List<ActivityVM> activities, Response response) {
                Log.d(ActivityMainFragment.class.getSimpleName(), "getActivities: size=" + activities.size());
                if (offset == 0L) {
                    items.clear();

                    if (activities.size() == 0) {
                        tipText.setVisibility(View.VISIBLE);
                    } else {
                        markRead();
                    }
                }

                items.addAll(activities);
                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(ActivityMainFragment.class.getSimpleName(), "getActivitiess: failure", error);
            }
        });
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