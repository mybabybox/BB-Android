package com.babybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.app.ConversationCache;
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.listener.InfiniteScrollListener;
import com.babybox.util.DefaultValues;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import com.yalantis.phoenix.PullToRefreshView;

import org.parceler.apache.commons.lang.StringUtils;

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

        //listView.setDescendantFocusability(ListView.FOCUS_BLOCK_DESCENDANTS);

        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);

        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
            @Override
            public void onItemCheckedStateChanged(ActionMode actionMode, int i, long l, boolean b) {
                final int checkedCount = listView.getCheckedItemCount();
                actionMode.setTitle(checkedCount + " Selected");
                adapter.toggleSelection(i);
            }

            @Override
            public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                actionMode.getMenuInflater().inflate(R.menu.list_select_menu, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode actionMode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode actionMode, MenuItem menuItem) {
                String message = ConversationListActivity.this.getString(R.string.post_delete_confirm);
                final SparseBooleanArray selected = adapter.getSelectedIds();
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.valueAt(i)) {
                        ConversationVM item = adapter.getItem(selected.keyAt(i));
                        message += "\n  " + item.userName + ": " + ViewUtil.shortenString(item.postTitle, DefaultValues.DEFAULT_SHORT_TITLE_COUNT);
                    }
                }

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ConversationListActivity.this);
                alertDialogBuilder.setMessage(message);
                alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "listView.deleteConversation: selected.size="+selected.size());
                        for (int i = selected.size() - 1; i >= 0; i--) {
                            if (selected.valueAt(i)) {
                                ConversationVM item = adapter.getItem(selected.keyAt(i));
                                adapter.deleteConversation(item.getId());
                            }
                        }
                    }
                });
                alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        adapter.removeSelection();
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();

                actionMode.finish();
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode actionMode) {
                Log.d(TAG, "listView.onDestroyActionMode: adapter.removeSelection");
                adapter.removeSelection();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                ConversationVM conversation = adapter.getItem(i);
                conversation.unread = 0L;
                ViewUtil.startMessageListActivity(ConversationListActivity.this, conversation);
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
                            getConversations(0L);
                        }
                    }, DefaultValues.PULL_TO_REFRESH_DELAY);
                }
            });
        }

        attachEndlessScrollListener();

        getConversations(0L);
    }

    protected void attachEndlessScrollListener() {
        listView.setOnScrollListener(new InfiniteScrollListener() {
            @Override
            public void onLoadMore(int page, int totalItemsCount) {
                getConversations(page - 1);
            }

            @Override
            public void onScrollUp() {
            }

            @Override
            public void onScrollDown() {
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        // handle gcm
        if (ViewUtil.isGcmLaunchTarget(getIntent())) {
            // no-op...
        }

        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_CONVERSATION_NOTIFS);
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
            adapter.notifyDataSetChanged();
        }
    }

    protected void getConversations(final long offset) {
        if (offset == 0) {
            ConversationCache.clear();
            adapter = new ConversationListAdapter(this, ConversationCache.getConversations());
            listView.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }

        ViewUtil.showSpinner(this);
        ConversationCache.load(offset, new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> vms, Response response) {
                Log.d(ConversationListActivity.class.getSimpleName(), "getConversations: success");
                if (offset == 0 && vms.size() == 0) {
                    tipText.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
                ViewUtil.stopSpinner(ConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ConversationListActivity.this);
                Log.e(ConversationListActivity.class.getSimpleName(), "getConversations: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NotificationCounter.refresh();
    }

}