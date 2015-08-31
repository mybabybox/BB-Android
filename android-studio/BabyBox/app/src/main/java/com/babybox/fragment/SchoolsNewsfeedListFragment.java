package com.babybox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.viewmodel.PostArray;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SchoolsNewsfeedListFragment extends AbstractNewsfeedListFragment {

    private static final String TAG = SchoolsNewsfeedListFragment.class.getName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        return view;
    }

    protected void loadNewsfeed(int offset) {
        Callback<PostArray> callback = new Callback<PostArray>() {
            @Override
            public void success(final PostArray array, Response response) {
                loadFeedItemsToList(array.getPosts());
            }

            @Override
            public void failure(RetrofitError error) {
                setFooterText(R.string.list_loading_error);
                error.printStackTrace();
            }
        };

        AppController.getApi().getPNNewsfeed(Long.valueOf(offset), AppController.getInstance().getSessionId(), callback);
    }
}