package com.babybox.fragment;

import android.app.ActionBar;
import android.app.Notification;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.babybox.app.AppController;
import com.babybox.util.DefaultValues;
import com.babybox.util.FeedFilter;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;

import org.parceler.apache.commons.collections.ListUtils;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedViewFragment extends AbstractFeedViewFragment {

    private static final String TAG = FeedViewFragment.class.getName();

    protected Callback<List<PostVMLite>> feedCallback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void initCallback() {
        feedCallback = new Callback<List<PostVMLite>>() {
            @Override
            public void success(final List<PostVMLite> posts, Response response) {
                loadFeedItemsToList(posts);
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                //setFooterText(R.string.list_loading_error);
                ViewUtil.alert(getActivity(), getActivity().getString(R.string.list_loading_error)+"\n[FeedFilter]\n"+
                        getFeedFilter().toString()+"\n[Error]\n"+
                        error.getLocalizedMessage());
                Log.e(FeedViewFragment.class.getSimpleName(), "getFeed: failure feedFilter=\n"+getFeedFilter().toString(), error);
                ViewUtil.stopSpinner(getActivity());
            }
        };
    }

    protected void loadFeed(Long offset, FeedFilter feedFilter) {
        if (feedFilter == null || feedFilter.feedType == null) {
            Log.w(this.getClass().getSimpleName(), "loadFeed: offset=" + offset + " with null key!!");
            return;
        }

        if (feedCallback == null) {
            initCallback();
        }

        Log.d(this.getClass().getSimpleName(), "loadFeed: offset=" + offset + " with key=" + feedFilter.feedType.name());
        switch (feedFilter.feedType) {
            case HOME_EXPLORE:
                AppController.getApiService().getHomeExploreFeed(
                        Long.valueOf(offset),
                        feedCallback);
                break;
            case HOME_FOLLOWING:
                AppController.getApiService().getHomeFollowingFeed(
                        Long.valueOf(offset),
                        feedCallback);
                break;
            case CATEGORY_POPULAR:
                AppController.getApiService().getCategoryPopularFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.productType.name(),
                        feedCallback);
                break;
            case CATEGORY_NEWEST:
                AppController.getApiService().getCategoryNewestFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.productType.name(),
                        feedCallback);
                break;
            case CATEGORY_PRICE_LOW_HIGH:
                AppController.getApiService().getCategoryPriceLowHighFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.productType.name(),
                        feedCallback);
                break;
            case CATEGORY_PRICE_HIGH_LOW:
                AppController.getApiService().getCategoryPriceHighLowFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedFilter.productType.name(),
                        feedCallback);
                break;
            /*
            case USER_COLLECTION:
            case USER_COLLECTIONS:
                AppController.getApiService().getUserCollectionFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedCallback);
                break;
            */
            case USER_POSTED:
                AppController.getApiService().getUserPostedFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedCallback);
                break;
            case USER_LIKED:
                AppController.getApiService().getUserLikedFeed(
                        Long.valueOf(offset),
                        feedFilter.objId,
                        feedCallback);
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "loadFeed: unknown default case with key - " + feedFilter.feedType.name());
        }
    }

    @Override
    protected void onScrollUp() {
        MainActivity.getInstance().showBottomMenuBar(true);
    }

    @Override
    protected void onScrollDown() {
        MainActivity.getInstance().showBottomMenuBar(false);
    }
}