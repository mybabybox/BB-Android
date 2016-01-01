package com.babybox.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.adapter.AdminConversationListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.AdminConversationVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminConversationListActivity extends TrackedFragmentActivity {

    private static final String TAG = AdminConversationListActivity.class.getName();

    protected ListView listView;
    protected TextView tipText;

    protected ImageView backImage;
    protected PullToRefreshView pullListView;

    protected AdminConversationListAdapter adapter;

    protected int getContentViewResource() {
        return R.layout.conversation_list_activity;
    }

    protected String getActionBarTitle() {
        return getString(R.string.admin_latest_conversations);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(getContentViewResource());

        setActionBarTitle(getActionBarTitle());

        tipText = (TextView) findViewById(R.id.tipText);
        listView = (ListView) findViewById(R.id.conversationList);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AdminConversationVM conversation = adapter.getItem(i);
                ViewUtil.startAdminMessageListActivity(AdminConversationListActivity.this, conversation);
            }
        });

        backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        // pull refresh
        pullListView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);
        if (pullListView != null) {
            pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
                @Override
                public void onRefresh() {
                    pullListView.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            pullListView.setRefreshing(false);
                            getConversations();
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        getConversations();
    }

    protected void getConversations() {
        ViewUtil.showSpinner(this);
        AppController.getApiService().getLatestConversations(new Callback<List<AdminConversationVM>>() {
            @Override
            public void success(List<AdminConversationVM> conversations, Response response) {
                Log.d(AdminConversationListActivity.class.getSimpleName(), "getConversations: success");
                if (conversations.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new AdminConversationListAdapter(AdminConversationListActivity.this, conversations);
                    listView.setAdapter(adapter);
                }

                ViewUtil.stopSpinner(AdminConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminConversationListActivity.this);
                Log.e(AdminConversationListActivity.class.getSimpleName(), "getConversations: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

}