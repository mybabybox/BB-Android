package com.babybox.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;

import com.babybox.R;
import com.babybox.adapter.AdminUserListAdapter;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVMLite;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminLatestLoginsActivity extends AbstractListViewActivity {
    private static final String TAG = AdminLatestLoginsActivity.class.getName();

    private List<UserVMLite> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.admin_latest_logins);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.admin_latest_logins);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);
    }

    @Override
    protected BaseAdapter getListAdapter() {
        if (items == null) {
            items = new ArrayList<>();
        }
        return new AdminUserListAdapter(this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getUsersByLogin(offset, new Callback<List<UserVMLite>>() {
            @Override
            public void success(List<UserVMLite> users, Response response) {
                Log.d(TAG, "loadListItems.getUsersByLogin: offset=" + offset +
                        " size=" + (users == null ? 0 : users.size()));

                if (offset == 0 && (users == null || users.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (users != null && users.size() > 0) {
                    items.addAll(users);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(AdminLatestLoginsActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminLatestLoginsActivity.this);
                Log.e(TAG, "loadListItems.getUsersBySignup: failure", error);
            }
        });
    }
}
