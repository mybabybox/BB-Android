package com.babybox.activity;

import android.util.Log;
import android.widget.BaseAdapter;

import com.babybox.R;
import com.babybox.adapter.AdminNewUserListAdapter;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVMLite;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminNewUsersActivity extends AbstractListViewActivity {

    private List<UserVMLite> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.admin_new_users);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.admin_new_users);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new AdminNewUserListAdapter(AdminNewUsersActivity.this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getUsersBySignup(offset, new Callback<List<UserVMLite>>() {
            @Override
            public void success(List<UserVMLite> users, Response response) {
                Log.d(AdminNewUsersActivity.class.getSimpleName(), "loadListItems.getUsersBySignup: offset=" + offset +
                        " size=" + (users == null ? 0 : users.size()));

                if (offset == 0 && (users == null || users.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (users != null && users.size() > 0) {
                    items.addAll(users);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(AdminNewUsersActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminNewUsersActivity.this);
                Log.e(AdminNewUsersActivity.class.getSimpleName(), "loadListItems.getUsersBySignup: failure", error);
            }
        });
    }
}
