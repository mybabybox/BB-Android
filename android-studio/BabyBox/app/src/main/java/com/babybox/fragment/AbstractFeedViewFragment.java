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
import com.babybox.util.FeedFilter;
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

    protected FeedFilter feedFilter;
    protected List<PostVM> items;

    protected boolean hasHeader = false;

    protected View headerView;
    protected View footerView;

    protected PullToRefreshView pullListView;

    protected boolean reload = false;

    abstract protected void loadFeed(int offset, FeedFilter feedFilter);

    protected FeedFilter getFeedFilter() {
        return feedFilter;
    }

    protected void setFeedFilter(FeedFilter feedFilter) {
        this.feedFilter = feedFilter;
    }

    protected View getHeaderView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.feed_view_fragment, container, false);

        footerView = inflater.inflate(R.layout.list_loading_footer, null);
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
                }, DefaultValues.PULL_TO_REFRESH_DELAY);
            }
        });

        reloadFeed();

        return view;
    }

    protected void reloadFeed() {
        FeedFilter.FeedType feedType = getFeedType(getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_TYPE));
        FeedFilter.FeedProductType productType = getFeedProductType(getArguments().getString(ViewUtil.BUNDLE_KEY_FEED_PRODUCT_TYPE));
        Long objId = getArguments().getLong(ViewUtil.BUNDLE_KEY_ID, -1);
        reloadFeed(new FeedFilter(feedType, productType, objId));
    }

    protected void reloadFeed(FeedFilter feedFilter) {
        if (feedFilter.feedType != null) {
            ViewUtil.showSpinner(getActivity());
            setFeedFilter(feedFilter);
            loadFeed(0, feedFilter);
            attachEndlessScrollListener();
            reload = true;
        }
    }

    protected FeedFilter.FeedType getFeedType(String feedType) {
        if (StringUtils.isEmpty(feedType)) {
            Log.w(this.getClass().getSimpleName(), "getFeedType: null feedType!!");
            return null;
        }

        try {
            return FeedFilter.FeedType.valueOf(feedType);
        } catch (IllegalArgumentException e) {
            Log.e(this.getClass().getSimpleName(), "getFeedType: Invalid feedType="+feedType, e);
        }
        return null;
    }

    protected FeedFilter.FeedProductType getFeedProductType(String productType) {
        if (StringUtils.isEmpty(productType)) {
            Log.w(this.getClass().getSimpleName(), "getFeedProductType: null productType!!");
            return null;
        }

        try {
            return FeedFilter.FeedProductType.valueOf(productType);
        } catch (IllegalArgumentException e) {
            Log.e(this.getClass().getSimpleName(), "getFeedProductType: Invalid productType="+productType, e);
        }
        return null;
    }

    protected void attachEndlessScrollListener() {
        feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(int page) {
                loadFeed(page - 1, getFeedFilter());
            }
        });
    }

    protected void loadFeedItemsToList(List<PostVM> posts) {
        Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: size = "+posts.size());

        if (reload) {
            clearFeedItems();
            ViewUtil.stopSpinner(getActivity());
            reload = false;
        }

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
        reloadFeed(getFeedFilter());
    }

    protected void setFooterText(int text) {
        //showFooter(true);
        //footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        footerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
