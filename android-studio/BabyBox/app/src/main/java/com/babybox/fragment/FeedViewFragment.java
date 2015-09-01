package com.babybox.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil.FeedType;
import com.babybox.viewmodel.PostVMArray;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedViewFragment extends AbstractFeedViewFragment {

    private static final String TAG = FeedViewFragment.class.getName();

    protected Callback<PostVMArray> feedCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void initCallback() {
        feedCallback = new Callback<PostVMArray>() {
            @Override
            public void success(final PostVMArray array, Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(FeedViewFragment.class.getSimpleName(), "getFeed: failure", error);
            }
        };
    }

    protected void loadFeed(int offset, FeedType feedType) {
        if (feedType == null) {
            Log.w(this.getClass().getSimpleName(), "loadFeed: offset=" + offset + " with null key!!");
            return;
        }

        if (feedCallback == null) {
            initCallback();
        }

        Log.d(this.getClass().getSimpleName(), "loadFeed: offset=" + offset + " with key="+feedType.name());
        switch (feedType) {
            case HOME_EXPLORE:
                getHomeExploreFeed(offset);
                break;
            case HOME_TRENDING:
                getHomeTrendingFeed(offset);
                break;
            case HOME_FOLLOWING:
                getHomeFollowingFeed(offset);
                break;
            case CATEGORY_POPULAR:
                getCategoryPopularFeed(offset);
                break;
            case CATEGORY_NEWEST:
                getCategoryNewestFeed(offset);
                break;
            case CATEGORY_PRICE_LOW_HIGH:
                getCategoryPriceLowHighFeed(offset);
                break;
            case CATEGORY_PRICE_HIGH_LOW:
                getCategoryPriceHighLowFeed(offset);
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "loadFeed: unknown default case with key - "+feedType.name());
        }
    }

    private void getHomeExploreFeed(int offset) {
        AppController.getApiService().getHomeExploreFeed(Long.valueOf(offset), feedCallback);
    }

    private void getHomeTrendingFeed(int offset) {
        AppController.getApiService().getHomeTrendingFeed(Long.valueOf(offset), feedCallback);
    }

    private void getHomeFollowingFeed(int offset) {
        AppController.getApiService().getHomeFollowingFeed(Long.valueOf(offset), feedCallback);
    }

    private void getCategoryPopularFeed(int offset) {
        AppController.getApiService().getCategoryPopularFeed(Long.valueOf(offset), feedCallback);
    }

    private void getCategoryNewestFeed(int offset) {
        AppController.getApiService().getCategoryNewestFeed(Long.valueOf(offset), feedCallback);
    }

    private void getCategoryPriceLowHighFeed(int offset) {
        AppController.getApiService().getCategoryPriceLowHighFeed(Long.valueOf(offset), feedCallback);
    }

    private void getCategoryPriceHighLowFeed(int offset) {
        AppController.getApiService().getCategoryPriceHighLowFeed(Long.valueOf(offset), feedCallback);
    }
}