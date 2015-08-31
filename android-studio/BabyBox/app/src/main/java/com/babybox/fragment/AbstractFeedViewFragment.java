package com.babybox.fragment;

import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.R;
import com.babybox.adapter.FeedViewAdapter;
import com.babybox.app.TrackedFragment;
import com.babybox.listener.EndlessScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.viewmodel.PostVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedViewFragment extends TrackedFragment {

    private static final String TAG = AbstractFeedViewFragment.class.getName();

    protected RecyclerView feedView;
    protected FeedViewAdapter feedAdapter;
    protected GridLayoutManager layoutManager;

    protected List<PostVM> items;
    protected View loadingFooter;

    protected boolean hasHeader = false;

    protected View headerView;

    protected PullToRefreshView pullListView;

    abstract protected void loadFeed(int offset);

    protected View getHeaderView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.feed_view_fragment, container, false);

        loadingFooter = inflater.inflate(R.layout.list_loading_footer, null);
        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);

        items = new ArrayList<>();

        feedView = (RecyclerView) view.findViewById(R.id.feedView);
        feedView.setHasFixedSize(true);
        feedView.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        int margin = getActivity().getResources().getDimensionPixelSize(R.dimen.feed_item_margin);
                        outRect.set(margin, margin, margin, margin);
                    }
                });

        // header
        headerView = getHeaderView(inflater);
        if (headerView != null) {
            hasHeader = true;
        }

        // adapter
        feedAdapter = new FeedViewAdapter(getActivity(), items, headerView);
        feedView.setAdapter(feedAdapter);

        // layout manager
        layoutManager = new GridLayoutManager(getActivity(), 2);
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return feedAdapter.isHeader(position) ? layoutManager.getSpanCount() : 1;
            }
        });
        feedView.setLayoutManager(layoutManager);

        // endless scroll
        attachEndlessScrollListener();

        // pull refresh
        pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullListView.setRefreshing(false);
                        refreshView();
                    }
                }, DefaultValues.DEFAULT_REFRESH_DELAY);
            }
        });

        loadFeed(0);

        return view;
    }

    protected void attachEndlessScrollListener() {
        feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadFeed(page - 1);
            }
        });
    }

    protected void loadFeedItemsToList(List<PostVM> posts) {
        Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: size = "+posts.size());
        items.addAll(posts);
        feedAdapter.notifyDataSetChanged();
        showFooter(false);

        if (posts == null || posts.size() == 0) {
            setFooterText(R.string.list_loaded_all);
        } else {
            setFooterText(R.string.list_loading);
        }
    }

    protected void refreshView() {
        items.clear();
        loadFeed(0);
        attachEndlessScrollListener();
    }

    protected void setFooterText(int text) {
        //showFooter(true);
        //footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        loadingFooter.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
