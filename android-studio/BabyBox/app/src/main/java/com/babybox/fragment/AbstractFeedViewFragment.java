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
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVM;
import com.yalantis.phoenix.PullToRefreshView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedViewFragment extends TrackedFragment {

    private static final String TAG = AbstractFeedViewFragment.class.getName();

    protected RecyclerView feedView;
    protected FeedViewAdapter feedAdapter;
    protected GridLayoutManager layoutManager;

    protected ViewUtil.FeedType feedType;
    protected List<PostVM> items;
    protected View loadingFooter;

    protected boolean hasHeader = false;

    protected View headerView;

    protected PullToRefreshView pullListView;

    abstract protected void loadFeed(int offset, ViewUtil.FeedType feedType);

    protected ViewUtil.FeedType getFeedType() {
        return feedType;
    }

    protected void setFeedType(ViewUtil.FeedType feedType) {
        this.feedType = feedType;
    }

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
                        //int margin = getActivity().getResources().getDimensionPixelSize(R.dimen.feed_item_margin);
                        int topMargin = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_TOP_MARGIN);
                        int sideMargin = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_SIDE_MARGIN);
                        outRect.set(sideMargin, topMargin, sideMargin, topMargin);
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

        reloadFeed();

        return view;
    }

    protected void reloadFeed() {
        ViewUtil.FeedType feedType = getFeedType(getArguments().getString("key"));
        reloadFeed(feedType);
    }

    protected void reloadFeed(ViewUtil.FeedType feedType) {
        if (feedType != null) {
            clearFeedItems();
            setFeedType(feedType);
            loadFeed(0, feedType);
            attachEndlessScrollListener();
        }
    }

    protected ViewUtil.FeedType getFeedType(String feedType) {
        if (StringUtils.isEmpty(feedType)) {
            Log.w(this.getClass().getSimpleName(), "getFeedType: null feedType!!");
            return null;
        }

        try {
            return ViewUtil.FeedType.valueOf(feedType);
        } catch (IllegalArgumentException e) {
            Log.e(this.getClass().getSimpleName(), "getFeedType: Invalid feedType="+feedType, e);
        }
        return null;
    }

    protected void attachEndlessScrollListener() {
        feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadFeed(page - 1, getFeedType());
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

    protected void clearFeedItems() {
        items.clear();
        feedAdapter.notifyDataSetChanged();
    }

    protected void refreshView() {
        reloadFeed(getFeedType());
    }

    protected void setFooterText(int text) {
        //showFooter(true);
        //footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        loadingFooter.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
