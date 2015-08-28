package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.DetailActivity;
import com.babybox.adapter.FeedViewAdapter;
import com.babybox.app.TrackedFragment;
import com.babybox.listener.InfiniteScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.viewmodel.PostVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractFeedViewFragment extends TrackedFragment {

    private static final String TAG = AbstractFeedViewFragment.class.getName();

    protected ListView listView;
    protected BaseAdapter listAdapter;
    protected List<PostVM> items;
    protected View loadingFooter;
    protected TextView footerText;

    protected boolean hasHeader = false;

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

        listView = (ListView) view.findViewById(R.id.list);
        View headerView = getHeaderView(inflater);
        if (headerView != null) {
            listView.addHeaderView(headerView);
            hasHeader = true;
        }
        listView.addFooterView(loadingFooter);      // need to add footer before set adapter
        listAdapter = getAdapterByFlow("");
        listView.setAdapter(listAdapter);
        listView.setFriction(ViewConfiguration.getScrollFriction() *
                DefaultValues.LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR);

        footerText = (TextView) listView.findViewById(R.id.listLoadingFooterText);

        pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullListView.setRefreshing(false);
                        refreshList();
                    }
                }, DefaultValues.PULL_TO_REFRESH_DELAY);
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int headerViewsCount = listView.getHeaderViewsCount();
                if (position < headerViewsCount) {
                    // listview header
                    return;
                }

                Intent intent = new Intent(getActivity(), DetailActivity.class);
                PostVM post = (PostVM) listAdapter.getItem(position - headerViewsCount);
                if (post != null) {
                    intent.putExtra("postId", post.getId());
                    intent.putExtra("catId", post.getCategoryId());
                    intent.putExtra("flag","FromFeedView");
                    startActivity(intent);
                }
            }
        });

        // pass hasFooter = true to InfiniteScrollListener
        listView.setOnScrollListener(new InfiniteScrollListener(
                DefaultValues.DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD, hasHeader, true) {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                loadingFooter.setVisibility(View.VISIBLE);
                loadFeed(page - 1);
            }
        });

        loadFeed(0);

        return view;
    }

    protected BaseAdapter getAdapterByFlow(String flowName) {
        return new FeedViewAdapter(getActivity(), items);
    }

    protected void loadFeedItemsToList(final List<PostVM> posts) {
        if (items.size() == 0) {
            //Log.d(this.getClass().getSimpleName(), "loadFeedItemsToList: first batch completed");
            items.addAll(posts);
            listAdapter.notifyDataSetChanged();
            showFooter(false);
        } else {
            // NOTE: delay infinite scroll by a short interval to make UI looks smooth
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    items.addAll(posts);
                    listAdapter.notifyDataSetChanged();
                }
            }, DefaultValues.DEFAULT_INFINITE_SCROLL_DELAY);
            showFooter(true);
        }

        if (posts == null || posts.size() == 0) {
            setFooterText(R.string.list_loaded_all);
        } else {
            setFooterText(R.string.list_loading);
        }
    }

    protected void refreshList() {
        items.clear();
        loadFeed(0);
        listAdapter.notifyDataSetChanged();
    }

    protected void setFooterText(int text) {
        showFooter(true);
        footerText.setText(text);
    }

    protected void showFooter(boolean show) {
        loadingFooter.setVisibility(show ? View.VISIBLE : View.GONE);
    }
}
