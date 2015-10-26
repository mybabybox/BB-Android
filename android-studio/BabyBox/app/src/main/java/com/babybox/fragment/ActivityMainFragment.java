package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.adapter.ActivityListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ActivityVM;
import com.yalantis.phoenix.PullToRefreshView;

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

    protected ActivityListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.activity_main_fragment, container, false);

        tipText = (TextView) view.findViewById(R.id.tipText);
        listView = (ListView) view.findViewById(R.id.activityList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                // open activity based on ActivityType
                //ViewUtil.startMessageListActivity(getActivity(), adapter.getItem(i).id, false);
            }
        });

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
                            getActivities();
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        getActivities();

        return view;
    }

    protected void markRead() {
        // no-op... activities are marked as read after getActivities() in server...
        //adapter.notifyDataSetChanged();
    }

    protected void getActivities() {
        ViewUtil.showSpinner(getActivity());
        AppController.getApiService().getActivites(0L, new Callback<List<ActivityVM>>() {
            @Override
            public void success(List<ActivityVM> activities, Response response) {
                Log.d(ActivityMainFragment.class.getSimpleName(), "getActivities: success");
                if (activities.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ActivityListAdapter(getActivity(), activities);
                    listView.setAdapter(adapter);

                    markRead();
                }

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
        for (Fragment fragment : getChildFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}