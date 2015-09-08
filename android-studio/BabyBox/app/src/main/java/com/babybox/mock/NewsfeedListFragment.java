package com.babybox.mock;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;

import com.babybox.R;
import com.babybox.app.AppController;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewsfeedListFragment extends AbstractNewsfeedListFragment {

    private static final String TAG = NewsfeedListFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void loadNewsfeed(int offset) {
        Log.d(this.getClass().getSimpleName(), "InfiniteScrollListener offset="+offset+" with key="+getArguments().getString("key"));
        switch (getArguments().getString("key")) {
            case "userquestion":
                getUserQuestions(offset,getArguments().getLong("id"));
                break;
            case "useranswer":
                getUserAnswers(offset, getArguments().getLong("id"));
                break;
            case "question":
                getUserQuestions(offset, getArguments().getLong("id"));
                break;
            case "answer":
                getUserAnswers(offset, getArguments().getLong("id"));
                break;
            case "bookmark":
                getBookmarks(offset);
                break;
            case "feed":
                getNewsFeed(offset);
                break;
            default:
                Log.w(this.getClass().getSimpleName(), "InfiniteScrollListener unknown default case with key - "+getArguments().getString("key"));
        }
    }

    private void getNewsFeed(int offset) {
        AppController.getApi().getNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(final PostArray array, retrofit.client.Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(NewsfeedListFragment.class.getSimpleName(), "getNewsFeed: failure", error);
            }
        });
    }

    private void getUserQuestions(int offset,Long id) {
        AppController.getApi().getUserPosts(Long.valueOf(offset), id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {
            @Override
            public void success(PostArray array, Response response2) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(NewsfeedListFragment.class.getSimpleName(), "getUserQuestion: failure", error);
            }
        });
    }

    private void getUserAnswers(int offset,Long id) {
        AppController.getApi().getUserComments(Long.valueOf(offset), id, AppController.getInstance().getSessionId(), new Callback<PostArray>() {

            @Override
            public void success(PostArray array, Response response2) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(NewsfeedListFragment.class.getSimpleName(), "getUserAnswer: failure", error);
            }
        });
    }

    private void getBookmarks(int offset) {
        AppController.getApi().getBookmarkedPosts(Long.valueOf(offset), AppController.getInstance().getSessionId(), new Callback<List<CommunityPostVM>>() {
            @Override
            public void success(List<CommunityPostVM> posts, Response response) {
                loadFeedItemsToList(posts);
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                Log.e(NewsfeedListFragment.class.getSimpleName(), "getBookmark: failure", error);
            }
        });
    }
}