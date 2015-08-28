package com.babybox.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.viewmodel.PostVMArray;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FeedViewFragment extends AbstractFeedViewFragment {

    private static final String TAG = FeedViewFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void loadFeed(int offset) {
        Log.d(this.getClass().getSimpleName(), "EndlessScrollListener offset=" + offset + " with key=" + getArguments().getString("key"));
        switch (getArguments().getString("key")) {
            case "home_explore":
            case "home_following":
            case "home_trending":
                getFeed(offset);
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "EndlessScrollListener unknown default case with key - "+getArguments().getString("key"));
        }
    }

    private void getFeed(int offset) {
        AppController.getApiService().getHomeExploreFeed(Long.valueOf(offset), new Callback<PostVMArray>() {
            @Override
            public void success(final PostVMArray array, Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(FeedViewFragment.class.getSimpleName(), "getFeed: failure", error);
            }
        });
    }
}