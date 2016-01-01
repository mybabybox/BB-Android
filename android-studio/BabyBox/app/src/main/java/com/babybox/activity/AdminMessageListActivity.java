package com.babybox.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.adapter.AdminMessageListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.AdminConversationVM;
import com.babybox.viewmodel.MessageVM;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminMessageListActivity extends TrackedFragmentActivity {

    private TextView postTitleText, postPriceText, soldText;
    private FrameLayout mainFrameLayout;

    private ImageView backImage, user1Button, user2Button, postImage;

    private ListView listView;
    private View listHeader;
    private RelativeLayout postLayout, loadMoreLayout;

    private List<MessageVM> messages = new ArrayList<>();
    private AdminMessageListAdapter adapter;

    private AdminConversationVM conversation;
    private Long offset = 1L;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_message_list_activity);

        LayoutInflater layoutInflater = LayoutInflater.from(getApplicationContext());
        listHeader = layoutInflater.inflate(R.layout.message_list_header, null);
        loadMoreLayout = (RelativeLayout) listHeader.findViewById(R.id.loadMoreLayout);

        listView = (ListView)findViewById(R.id.messageList);

        postLayout = (RelativeLayout) findViewById(R.id.postLayout);
        postImage = (ImageView) findViewById(R.id.postImage);
        postTitleText = (TextView) findViewById(R.id.postTitleText);
        postPriceText = (TextView) findViewById(R.id.postPriceText);
        soldText =  (TextView) findViewById(R.id.soldText);

        mainFrameLayout = (FrameLayout) findViewById(R.id.mainFrameLayout);
        user1Button = (ImageView) findViewById(R.id.user1Button);
        user2Button = (ImageView) findViewById(R.id.user2Button);

        // conversation
        conversation = (AdminConversationVM) getIntent().getSerializableExtra(ViewUtil.BUNDLE_KEY_OBJECT);

        postTitleText.setText(conversation.getPostTitle());
        postPriceText.setText(ViewUtil.priceFormat(conversation.getPostPrice()));
        ImageUtil.displayPostImage(conversation.getPostImage(), postImage);
        soldText.setVisibility(conversation.isPostSold() ? View.VISIBLE : View.INVISIBLE);

        listView.addHeaderView(listHeader);
        listHeader.setVisibility(View.INVISIBLE);

        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final MessageVM item = adapter.getItem(i);
                if (ViewUtil.copyToClipboard(item.getBody())) {
                    Toast.makeText(AdminMessageListActivity.this, AdminMessageListActivity.this.getString(R.string.text_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AdminMessageListActivity.this, AdminMessageListActivity.this.getString(R.string.text_copy_failed), Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        user1Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(AdminMessageListActivity.this, conversation.getUser1Id());
            }
        });

        user2Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(AdminMessageListActivity.this, conversation.getUser2Id());
            }
        });

        postLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startProductActivity(AdminMessageListActivity.this, conversation.getPostId());
            }
        });

        loadMoreLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadMoreMessages(conversation.id, offset);
                offset++;
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        loadMessages(conversation.id);
    }

    private int parseAndAddMessages(String responseBody) {
        int count = 0;
        try {
            JSONObject obj = new JSONObject(responseBody);

            JSONArray messagesObj = obj.getJSONArray(ViewUtil.JSON_KEY_MESSAGE_KEY);
            count = messagesObj.length();
            for (int i = 0; i < count; i++) {
                JSONObject messageObj = messagesObj.getJSONObject(i);
                Log.d(AdminMessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: message["+i+"]="+messageObj.toString());
                MessageVM vm = new MessageVM(messageObj);
                messages.add(vm);
            }

            Collections.sort(messages, new Comparator<MessageVM>() {
                public int compare(MessageVM m1, MessageVM m2) {
                    return m1.getCreatedDate().compareTo(m2.getCreatedDate());
                }
            });
        } catch (JSONException e) {
            Log.e(AdminMessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: exception", e);
        }
        return count;
    }

    private void loadMessages(final Long conversationId) {
        ViewUtil.showSpinner(AdminMessageListActivity.this);
        AppController.getApiService().getMessagesForAdmin(conversationId, 0L, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                messages.clear();

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter = new AdminMessageListAdapter(AdminMessageListActivity.this, messages, conversation);
                listView.setAdapter(adapter);

                ViewUtil.stopSpinner(AdminMessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminMessageListActivity.this);
                Log.e(AdminMessageListActivity.class.getSimpleName(), "getMessages.api.getMessages: failure", error);
            }
        });
    }

    private void loadMoreMessages(Long conversationId, Long offset) {
        ViewUtil.showSpinner(AdminMessageListActivity.this);
        AppController.getApiService().getMessagesForAdmin(conversationId, offset, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                listHeader.setVisibility(View.INVISIBLE);

                String responseBody = ViewUtil.getResponseBody(responseObject);
                int count = parseAndAddMessages(responseBody);
                if (count >= DefaultValues.CONVERSATION_MESSAGE_COUNT) {
                    listHeader.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();

                // restore previous listView position
                View childView = listView.getChildAt(0);
                int top = (childView == null)? 0 : (childView.getTop() - listView.getPaddingTop());
                listView.setSelectionFromTop(count, top);

                ViewUtil.stopSpinner(AdminMessageListActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(AdminMessageListActivity.this);
                Log.e(AdminMessageListActivity.class.getSimpleName(), "loadMoreMessages.api.getMessages: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}