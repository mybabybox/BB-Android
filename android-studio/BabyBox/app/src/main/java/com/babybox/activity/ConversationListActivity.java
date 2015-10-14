package com.babybox.activity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.adapter.MessageListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.BroadcastService;
import com.babybox.app.ConversationCache;
import com.babybox.app.GCMConfig;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;
import com.babybox.viewmodel.MessageVM;
import com.babybox.viewmodel.NewMessageVM;
import com.yalantis.phoenix.PullToRefreshView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationListActivity extends TrackedFragmentActivity {

    private static final String TAG = ConversationListActivity.class.getName();
    private ListView listView;
    private TextView tipText;

    private PullToRefreshView pullListView;

    private ConversationListAdapter adapter;

    private ConversationVM openedConversation = null;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.conversation_list_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.view_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        setActionBarTitle(getString(R.string.pm_actionbar_title));

        pullListView = (PullToRefreshView) findViewById(R.id.pull_to_refresh);

        tipText = (TextView) findViewById(R.id.tipText);
        listView = (ListView) findViewById(R.id.conversationList);

        getAllConversations();

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

        // pull refresh
        pullListView.setOnRefreshListener(new PullToRefreshView.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pullListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        pullListView.setRefreshing(false);
                        getAllConversations();
                    }
                }, DefaultValues.PULL_TO_REFRESH_DELAY);
            }
        });
    }

    private void markRead(ConversationVM conversation) {
        if (conversation != null) {
            conversation.unread = 0L;
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onStart() {
        super.onStart();

        /*
        if (adapter != null) {
            Log.d(ConversationListFragment.class.getSimpleName(), "onStart");
            ConversationCache.sortConversations();
            adapter.notifyDataSetChanged();
        }
        */
    }

    @Override
    public void onResume() {
        super.onResume();

        /*
        if (adapter != null) {
            Log.d(ConversationListFragment.class.getSimpleName(), "onResume");
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

    private void getAllConversations() {
        ViewUtil.showSpinner(this);
        ConversationCache.refresh(new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> conversations, Response response) {
                Log.d(ConversationListActivity.class.getSimpleName(), "getAllConversations: success");
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
                Log.e(ConversationListActivity.class.getSimpleName(), "getAllConversations: failure", error);
            }
        });
    }

    private void deleteConversation(final Long id) {
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
}