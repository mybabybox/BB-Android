package com.babybox.fragment;

import android.app.Activity;
import android.content.Intent;
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
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragment;
import com.babybox.listener.EndlessScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.util.FeedFilter;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVMLite;
import com.yalantis.phoenix.PullToRefreshView;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedViewFragment extends TrackedFragment {

    private static final String TAG = AbstractFeedViewFragment.class.getName();

    private static final int TOP_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_TOP_MARGIN);
    private static final int BOTTOM_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_BOTTOM_MARGIN);
    private static final int SIDE_MARGIN = ViewUtil.getRealDimension(DefaultValues.FEEDVIEW_ITEM_SIDE_MARGIN);
    private static final int LEFT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);
    private static final int RIGHT_SIDE_MARGIN = (SIDE_MARGIN * 2) + ViewUtil.getRealDimension(2);

    protected RecyclerView feedView;
    protected FeedViewAdapter feedAdapter;
    protected GridLayoutManager layoutManager;

    protected FeedFilter feedFilter;
    protected List<PostVMLite> items;

    protected View headerView;
    protected View footerView;
    protected View noItemView;

    protected PullToRefreshView pullListView;

    protected boolean reload = false;

    public enum ItemChangedState {
        ITEM_UPDATED,
        ITEM_ADDED,
        ITEM_REMOVED
    }

    abstract protected void loadFeed(Long offset, FeedFilter feedFilter);

    abstract protected void onScrollUp();

    abstract protected void onScrollDown();

    public boolean showSeller() {
        return false;
    }

    public boolean hasHeader() {
        return headerView != null;
    }

    public RecyclerView getFeedView() {
        return feedView;
    }

    protected FeedFilter getFeedFilter() {
        return feedFilter;
    }

    protected void setFeedFilter(FeedFilter feedFilter) {
        Log.d(this.getClass().getSimpleName(), "setFeedFilter: feedFilter\n" + feedFilter.toString());
        this.feedFilter = feedFilter;
    }

    protected View getHeaderView(LayoutInflater inflater) {
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.feed_view_fragment, container, false);

        pullListView = (PullToRefreshView) view.findViewById(R.id.pull_to_refresh);
        footerView = inflater.inflate(R.layout.list_loading_footer, null);

        items = new ArrayList<>();

        feedView = (RecyclerView) view.findViewById(R.id.feedView);
        //feedView.setHasFixedSize(true);
        feedView.addItemDecoration(
                new RecyclerView.ItemDecoration() {
                    @Override
                    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                        //int pos = parent.getChildAdapterPosition(view);
                        //int margin = getActivity().getResources().getDimensionPixelSize(R.dimen.feed_item_margin);

                        ViewUtil.FeedItemPosition feedItemPosition =
                                ViewUtil.getFeedItemPosition(AbstractFeedViewFragment.this, view);
                        if (feedItemPosition == ViewUtil.FeedItemPosition.HEADER) {
                            outRect.set(0, 0, 0, 0);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.LEFT_COLUMN) {
                            outRect.set(LEFT_SIDE_MARGIN, TOP_MARGIN, SIDE_MARGIN, BOTTOM_MARGIN);
                        } else if (feedItemPosition == ViewUtil.FeedItemPosition.RIGHT_COLUMN) {
                            outRect.set(SIDE_MARGIN, TOP_MARGIN, RIGHT_SIDE_MARGIN, BOTTOM_MARGIN);
                        }
                    }
                });

        // header
        headerView = getHeaderView(inflater);
        if (headerView != null) {
            noItemView = headerView.findViewById(R.id.noItemView);
        }

        // adapter
        feedAdapter = new FeedViewAdapter(getActivity(), items, headerView, showSeller());
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
                        reloadFeed(getFeedFilter());
                        onRefreshView();
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
        // reload already fired
        if (this.feedFilter != null &&
                this.feedFilter.equals(feedFilter) &&
                reload) {
            Log.w(this.getClass().getSimpleName(), "reloadFeed: reload already fired for filter\n"+feedFilter.toString());
            return;
        }

        if (feedFilter.feedType != null) {
            ViewUtil.showSpinner(getActivity());
            setFeedFilter(feedFilter);
            loadFeed(0L, feedFilter);
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
            return FeedFilter.FeedProductType.ALL;
        }

        try {
            return FeedFilter.FeedProductType.valueOf(productType);
        } catch (IllegalArgumentException e) {
            Log.e(this.getClass().getSimpleName(), "getFeedProductType: Invalid productType="+productType, e);
        }
        return FeedFilter.FeedProductType.ALL;
    }

    protected void attachEndlessScrollListener() {
        feedView.setOnScrollListener(new EndlessScrollListener(layoutManager) {
            @Override
            public void onLoadMore(Long page) {
                loadFeed(Long.valueOf(page - 1), getFeedFilter());
            }

            @Override
            public void onScrollUp() {
                AbstractFeedViewFragment.this.onScrollUp();
            }

            @Override
            public void onScrollDown() {
                AbstractFeedViewFragment.this.onScrollDown();
            }
        });
    }

    protected void loadFeedItemsToList(List<PostVMLite> posts) {
        Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: size=" + posts.size()+"\n"+getFeedFilter().toString());

        if (reload) {
            clearFeedItems();
            ViewUtil.stopSpinner(getActivity());
            reload = false;
        }

        items.addAll(posts);
        feedAdapter.notifyDataSetChanged();

        if (noItemView != null) {
            if (feedAdapter.isEmpty()) {
                noItemView.setVisibility(View.VISIBLE);
            } else {
                noItemView.setVisibility(View.GONE);
            }
        }

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

    protected void onRefreshView() {
        NotificationCounter.refresh();
    }

    protected void setFooterText(int text) {
        //showFooter(true);
        //footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        footerView.setVisibility(show ? View.VISIBLE : View.GONE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getSimpleName(), "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK &&
                data != null && feedAdapter != null) {

            // changed state
            ItemChangedState itemChangedState = null;
            try {
                itemChangedState = Enum.valueOf(ItemChangedState.class, data.getStringExtra(ViewUtil.INTENT_RESULT_ITEM_CHANGED_STATE));
            } catch (Exception e) {
            }

            // item
            PostVMLite feedPost = null;
            Serializable obj = data.getSerializableExtra(ViewUtil.INTENT_RESULT_OBJECT);
            if (obj != null) {
                try {
                    feedPost = (PostVMLite) obj;
                } catch (ClassCastException e) {
                }
            }

            if (itemChangedState == ItemChangedState.ITEM_UPDATED) {
                int position = feedAdapter.getClickedPosition();
                if (position == -1 || feedAdapter.isEmpty()) {
                    return;
                }

                PostVMLite item = feedAdapter.getItem(position);
                if (feedPost != null) {
                    Log.d(this.getClass().getSimpleName(), "onActivityResult: feedAdapter ITEM_UPDATED=" + position + " post=" + feedPost.id);

                    item.title = feedPost.title;
                    item.price = feedPost.price;
                    item.sold = feedPost.sold;
                    item.isLiked = feedPost.isLiked;
                    item.numLikes = feedPost.numLikes;
                    feedAdapter.notifyItemChanged(position);
                }
            }

            // TODO: handle add / remove item
            /*
            else if (itemChangedState == ItemChangedState.ITEM_ADDED) {
                Log.d(this.getClass().getSimpleName(), "onResume: feedAdapter ITEM_ADDED="+position);
                feedAdapter.notifyItemInserted(position);
            } else if (itemChangedState == ItemChangedState.ITEM_REMOVED) {
                Log.d(this.getClass().getSimpleName(), "onResume: feedAdapter ITEM_REMOVED="+position);
                feedAdapter.notifyItemRemoved(position);
            }
            */
        }
    }
}
