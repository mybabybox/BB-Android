package com.babybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.app.ConversationCache;
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import com.yalantis.phoenix.PullToRefreshView;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationListActivity extends TrackedFragmentActivity {

    private static final String TAG = ConversationListActivity.class.getName();

    protected ListView listView;
    protected TextView tipText;

    protected ImageView backImage;
    protected PullToRefreshView pullListView;

    protected ConversationListAdapter adapter;

    protected ConversationVM openedConversation = null;

    protected int getContentViewResource() {
        return R.layout.conversation_list_activity;
    }

    protected String getActionBarTitle() {
        return getString(R.string.pm_actionbar_title);
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
                openedConversation = adapter.getItem(i);
                ViewUtil.startMessageListActivity(ConversationListActivity.this, openedConversation.id, false);
            }
        });

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final ConversationVM item = adapter.getItem(i);
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConversationListActivity.this);
                alertDialogBuilder.setMessage(ConversationListActivity.this.getString(R.string.post_delete_confirm));
                alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteConversation(item.getId());
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
                return true;
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

    protected void markRead(ConversationVM conversation) {
        if (conversation != null) {
            conversation.unread = 0L;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // handle gcm
        if (ViewUtil.isGcmLaunchTarget(getIntent())) {
            // no-op...
        }

        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_CONVERSATION_NOTIFS);

        /*
        if (adapter != null) {
            Log.d(ConversationListFragment.class.getSimpleName(), "onStart");
            ConversationCache.sortConversations();
            adapter.notifyDataSetChanged();
        }
        */
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE &&
                resultCode == Activity.RESULT_OK &&
                data != null && adapter != null) {

            Long conversationId = data.getLongExtra(ViewUtil.INTENT_RESULT_ID, -1L);

            Log.d(this.getClass().getSimpleName(), "onActivityResult: conversationId=" + conversationId);
            if (conversationId != -1L) {
                // new message sent for conversation
                ConversationCache.update(conversationId, new Callback<ConversationVM>() {
                    @Override
                    public void success(ConversationVM conversation, Response response) {
                        adapter.notifyDataSetChanged();
                        ViewUtil.stopSpinner(ConversationListActivity.this);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ViewUtil.stopSpinner(ConversationListActivity.this);
                        Log.e(ConversationListActivity.class.getSimpleName(), "onActivityResult: failure", error);
                    }
                });
            }
        } else {
            // mark read for conversation
            markRead(openedConversation);
        }
    }

    protected void getConversations() {
        ViewUtil.showSpinner(this);
        ConversationCache.refresh(new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversations, Response response) {
                Log.d(ConversationListActivity.class.getSimpleName(), "getConversations: success");
                if (conversations.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ConversationListAdapter(ConversationListActivity.this, ConversationCache.getConversations());
                    listView.setAdapter(adapter);
                }

                ViewUtil.stopSpinner(ConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ConversationListActivity.this);
                Log.e(ConversationListActivity.class.getSimpleName(), "getConversations: failure", error);
            }
        });
    }

    protected void deleteConversation(final Long id) {
        ViewUtil.showSpinner(this);
        ConversationCache.delete(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(ConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ConversationListActivity.this);
                Log.e(ConversationListActivity.class.getSimpleName(), "deleteConversation: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NotificationCounter.refresh();
    }
}