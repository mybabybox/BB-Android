package com.babybox.listener;

import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;

import com.babybox.activity.MainActivity;

public abstract class InfiniteScrollListener implements OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 2;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    // Need to offset header by -1 from count
    private boolean hasHeader = false;
    // Need to offset footer by -1 from count
    private boolean hasFooter = false;
    // For tracking scroll up scroll down
    private int mLastFirstVisibleItem;

    public InfiniteScrollListener() {
    }

    public InfiniteScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public InfiniteScrollListener(int visibleThreshold, boolean hasHeader, boolean hasFooter) {
        this.visibleThreshold = visibleThreshold;
        this.hasHeader = hasHeader;
        this.hasFooter = hasFooter;
    }

    public InfiniteScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view,int firstVisibleItem,int visibleItemCount,int totalItemCount) {
        // scrolling up down
        if (mLastFirstVisibleItem < firstVisibleItem) {
            onScrollUp();
        }
        if (mLastFirstVisibleItem > firstVisibleItem) {
            onScrollDown();
        }
        mLastFirstVisibleItem = firstVisibleItem;

        // load more?
        if (totalItemCount > 0) {
            if (hasHeader)
                totalItemCount--;
            if (hasFooter)
                totalItemCount--;
        }

        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state
        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }

        // If it’s still loading, we check to see if the dataset count has
        // changed, if so we conclude it has finished loading and update the current page
        // number and total item count.
        if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

        // If it isn’t currently loading, we check to see if we have breached
        // the visibleThreshold and need to reload more data.
        // If we do need to reload some more data, we execute onLoadMore to fetch the data.
        if (!loading && (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }

    public abstract void onLoadMore(int page, int totalItemsCount);
    public abstract void onScrollUp();
    public abstract void onScrollDown();
}