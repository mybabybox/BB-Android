package com.babybox.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.BaseAdapter;

import com.babybox.R;
import com.babybox.adapter.AdminConversationListAdapter;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.AdminConversationVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminConversationListActivity extends AbstractListViewActivity {
    private static final String TAG = AdminConversationListActivity.class.getName();

    private List<AdminConversationVM> items;

    @Override
    protected String getTitleText() {
        return getString(R.string.admin_latest_conversations);
    }

    @Override
    protected String getNoItemText() {
        return getString(R.string.admin_latest_conversations);
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
        return new AdminConversationListAdapter(this, items);
    }

    @Override
    protected void loadListItems(final Long objId, final Long offset) {
        ViewUtil.showSpinner(this);

        AppController.getApiService().getLatestConversations(offset, new Callback<List<AdminConversationVM>>() {
            @Override
            public void success(List<AdminConversationVM> vms, Response response) {
                Log.d(TAG, "loadListItems.getLatestConversations: offset=" + offset +
                        " size=" + (vms == null ? 0 : vms.size()));

                if (offset == 0 && (vms == null || vms.size() == 0)) {
                    showNoItemText();
                    return;
                }

                if (vms != null && vms.size() > 0) {
                    items.addAll(vms);
                    listAdapter.notifyDataSetChanged();
                }

                ViewUtil.stopSpinner(AdminConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminConversationListActivity.this);
                Log.e(TAG, "loadListItems.getLatestConversations: failure", error);
            }
        });
    }
}