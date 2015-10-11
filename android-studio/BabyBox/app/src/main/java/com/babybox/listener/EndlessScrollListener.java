package com.babybox.listener;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public abstract class EndlessScrollListener extends RecyclerView.OnScrollListener {
    public static String TAG = EndlessScrollListener.class.getSimpleName();

    private int previousTotal = 0; // The total number of items in the dataset after the last load
    private boolean loading = true; // True if we are still waiting for the last set of data to load.
    private int visibleThreshold = 5; // The minimum amount of items to have below your current scroll position before loading more.
    int firstVisibleItem, visibleItemCount, totalItemCount;

    private int current_page = 1;

    private static final int HIDE_THRESHOLD = 20;

    private int mScrolledDistance = 0;
    private boolean mControlsVisible = true;

    private LinearLayoutManager mLinearLayoutManager;

    public EndlessScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        int totalItem = totalItemCount - previousTotal;
        if (loading) {
            if (totalItemCount > previousTotal) {
                loading = false;
                previousTotal = totalItemCount;
            }
        }
        if (!loading &&
                (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
            // End has been reached

            // Do something

            // Need to check for accuracy.
            if (recyclerView.getChildAt(visibleItemCount - 1) == null) {
                return;
            }

            Long offset = 0L;
            offset = (Long) recyclerView.getChildAt(visibleItemCount - 1).getTag();
            if(offset != null) {
                if( offset == 0) {
                    current_page++;
                    onLoadMore((long)current_page);
                } else {
                    offset = offset+1;   //TODO:loadFeed(Long.valueOf(page - 1), getFeedFilter()) we are decrementing page value in loadFeed function
                    onLoadMore(offset);
                }
            }

            loading = true;
        }

        // show / hide controls trigger
        int threshold = HIDE_THRESHOLD;
        if (firstVisibleItem == 0) {
            threshold = HIDE_THRESHOLD * 10;
        }

        if (mScrolledDistance > threshold && mControlsVisible) {
            onScrollUp();
            mControlsVisible = false;
            mScrolledDistance = 0;
        } else if (mScrolledDistance < -threshold && !mControlsVisible) {
            onScrollDown();
            mControlsVisible = true;
            mScrolledDistance = 0;
        }

        /*
        if (firstVisibleItem == 0) {
            if (!mControlsVisible) {
                onScrollDown();
                mControlsVisible = true;
            }
        } else {
            if (mScrolledDistance > threshold && mControlsVisible) {
                onScrollUp();
                mControlsVisible = false;
                mScrolledDistance = 0;
            } else if (mScrolledDistance < -threshold && !mControlsVisible) {
                onScrollDown();
                mControlsVisible = true;
                mScrolledDistance = 0;
            }
        }
        */

        if ((mControlsVisible && dy>0) || (!mControlsVisible && dy<0)) {
            mScrolledDistance += dy;
        }
    }

    public abstract void onLoadMore(Long current_page);
    public abstract void onScrollUp();
    public abstract void onScrollDown();
}