package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.babybox.adapter.ActivityListAdapter;
import com.babybox.adapter.SellerListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragment;
import com.babybox.listener.InfiniteScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ActivityVM;
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.SellerVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SellerMainFragment extends TrackedFragment {

    private static final String TAG = SellerMainFragment.class.getName();

    protected ListView listView;

    protected ImageView backImage;
    protected PullToRefreshView pullListView;

    protected List<SellerVM> items;
    protected SellerListAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.seller_main_fragment, container, false);

        items = new ArrayList<>();
        adapter = new SellerListAdapter(getActivity(), items);

        listView = (ListView) view.findViewById(R.id.sellerList);
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
                            getSellers(0L);
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        attachEndlessScrollListener();

        getSellers(0L);

        return view;
    }

    protected void attachEndlessScrollListener() {
        int visibleThreshold = 5;
        listView.setOnScrollListener(new InfiniteScrollListener(visibleThreshold) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getSellers(items.get(items.size() - 1).offset);
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

    protected void getSellers(long offset) {
        if (offset == 0L) {
            items.clear();
            adapter.notifyDataSetChanged();
        }

        Log.d(TAG, "getSellers() offset="+offset);

        ViewUtil.showSpinner(getActivity());
        AppController.getApiService().getRecommendedSellersFeed(offset, new Callback<List<SellerVM>>() {
            @Override
            public void success(List<SellerVM> sellers, Response response) {
                Log.d(TAG, "getSellers: success size="+sellers.size());
                items.addAll(sellers);
                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(TAG, "getSellers: failure", error);
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