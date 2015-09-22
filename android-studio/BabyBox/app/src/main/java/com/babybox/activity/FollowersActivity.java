package com.babybox.activity;

import android.util.Log;
import android.widget.BaseAdapter;

import com.babybox.R;
import com.babybox.adapter.CommentListAdapter;
import com.babybox.adapter.FollowerFollowingListAdapter;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.UserVM;
import com.babybox.viewmodel.UserVMLite;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FollowersActivity extends AbstractListViewActivity {

    private List<UserVMLite> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.followers);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.no_followers);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new FollowerFollowingListAdapter(FollowersActivity.this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getFollowers(offset, objId, new Callback<List<UserVMLite>>() {
            @Override
            public void success(List<UserVMLite> users, Response response) {
                Log.d(FollowersActivity.class.getSimpleName(), "loadListItems.getFollowers: offset=" + offset +
                        " size=" + (users == null ? 0 : users.size()));

                if (offset == 0 && (users == null || users.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (users != null && users.size() > 0) {
                    items.addAll(users);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(FollowersActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(FollowersActivity.this);
                Log.e(FollowersActivity.class.getSimpleName(), "loadListItems.getFollowers: failure", error);
            }
        });
    }
}
