package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.adapter.ConversationListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.ConversationCache;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ProductConversationListActivity extends ConversationListActivity {

    private static final String TAG = ProductConversationListActivity.class.getName();

    private RelativeLayout postLayout;
    private TextView postTitleText, postPriceText;
    private ImageView postImage;

    private List<ConversationVM> conversations = new ArrayList<>();

    @Override
    protected int getContentViewResource() {
        return R.layout.product_conversation_list_activity;
    }

    @Override
    protected String getActionBarTitle() {
        return getString(R.string.pm_actionbar_title);
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        postLayout = (RelativeLayout) findViewById(R.id.postLayout);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitleText = (TextView) findViewById(R.id.postTitleText);
        postPriceText = (TextView) findViewById(R.id.postPriceText);

    }

    private void updateConversations(ConversationVM conversation) {
        boolean deleted = conversations.remove(conversation);
        if (deleted) {
            conversations.add(conversation);
        }
        ConversationCache.sortConversations();
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
                AppController.getApiService().getConversation(conversationId, new Callback<ConversationVM>() {
                    @Override
                    public void success(ConversationVM conversation, Response response) {
                        updateConversations(conversation);
                        adapter.notifyDataSetChanged();
                        ViewUtil.stopSpinner(ProductConversationListActivity.this);
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        ViewUtil.stopSpinner(ProductConversationListActivity.this);
                        Log.e(ProductConversationListActivity.class.getSimpleName(), "onActivityResult: failure", error);
                    }
                });
            }
        } else {
            // mark read for conversation
            markRead(openedConversation);
        }
    }

    @Override
    protected void getConversations() {
        ViewUtil.showSpinner(this);

        Long postId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        AppController.getApiService().getPostConversations(postId, new Callback<List<ConversationVM>>() {
            @Override
            public void success(List<ConversationVM> vms, Response response) {
                Log.d(ProductConversationListActivity.class.getSimpleName(), "getConversations: success");

                conversations = vms;
                if (conversations.size() == 0) {
                    postLayout.setVisibility(View.GONE);
                    tipText.setVisibility(View.VISIBLE);
                } else {
                    adapter = new ConversationListAdapter(ProductConversationListActivity.this, conversations, false);
                    listView.setAdapter(adapter);

                    // init post details
                    postLayout.setVisibility(View.VISIBLE);
                    ConversationVM conversation = conversations.get(0);
                    postTitleText.setText(conversation.getPostTitle());
                    postPriceText.setText(ViewUtil.priceFormat(conversation.getPostPrice()));
                    ImageUtil.displayPostImage(conversation.getPostImage(), postImage);
                }

                ViewUtil.stopSpinner(ProductConversationListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(ProductConversationListActivity.this);
                Log.e(ProductConversationListActivity.class.getSimpleName(), "getConversations: failure", error);
            }
        });
    }
}