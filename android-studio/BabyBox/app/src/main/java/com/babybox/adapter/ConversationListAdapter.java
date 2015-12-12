package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class ConversationListAdapter extends BaseAdapter {
    private static final String TAG = ConversationListAdapter.class.getName();

    private Activity activity;
    private LayoutInflater inflater;

    private List<ConversationVM> conversations;
    private boolean showPost;

    private RelativeLayout conversationLayout;
    private LinearLayout postImageLayout, hasImageLayout;
    private ImageView userImage, postImage;
    private TextView userText, postTitleText, lastMessageText, buyText, sellText, soldText, dateText, unreadCountText;
    private RelativeLayout sellerAdminLayout;
    private Spinner orderTransactionStateSpinner;

    private ArrayAdapter<String> stateAdapter;
    private SparseBooleanArray selectedItemsIds;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this(activity, conversationVMs, true);
    }

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversations, boolean showPost) {
        this.activity = activity;
        this.conversations = conversations;
        this.showPost = showPost;
        this.selectedItemsIds = new SparseBooleanArray();
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public ConversationVM getItem(int i) {
        return conversations.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.conversation_list_item, null);

        conversationLayout = (RelativeLayout) view.findViewById(R.id.conversationLayout);
        userText = (TextView) view.findViewById(R.id.userText);
        postTitleText = (TextView) view.findViewById(R.id.postTitleText);
        lastMessageText = (TextView) view.findViewById(R.id.lastMessageText);
        hasImageLayout = (LinearLayout) view.findViewById(R.id.hasImageLayout);
        buyText = (TextView) view.findViewById(R.id.buyText);
        sellText = (TextView) view.findViewById(R.id.sellText);
        soldText = (TextView) view.findViewById(R.id.soldText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        unreadCountText = (TextView) view.findViewById(R.id.unreadCountText);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        postImageLayout = (LinearLayout) view.findViewById(R.id.postImageLayout);
        postImage = (ImageView) view.findViewById(R.id.postImage);
        sellerAdminLayout = (RelativeLayout) view.findViewById(R.id.sellerAdminLayout);
        orderTransactionStateSpinner = (Spinner) view.findViewById(R.id.orderTransactionStateSpinner);

        final ConversationVM item = conversations.get(i);

        Log.d(TAG, "[" + i + "|id=" + item.id + "] " + item.getPostTitle() + " unread=" + item.getUnread());
        if (item.getUnread() > 0) {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        } else {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.color.white));
        }

        conversationLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.unread = 0L;   // reset unread count
                ViewUtil.startMessageListActivity(activity, item.id, false);
            }
        });

        unreadCountText.setText(item.getUnread() + "");
        unreadCountText.setVisibility(item.getUnread() > 0 ? View.VISIBLE : View.GONE);

        ImageUtil.displayThumbnailProfileImage(item.getUserId(), userImage);
        if (showPost) {
            postImageLayout.setVisibility(View.VISIBLE);
            ImageUtil.displayPostImage(item.getPostImage(), postImage);
            ViewUtil.setConversationImageTag(view, item);
        } else {
            postImageLayout.setVisibility(View.GONE);
        }

        userText.setText(item.getUserName());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);

        hasImageLayout.setVisibility(item.lastMessageHasImage? View.VISIBLE : View.GONE);

        // seller admin

        if (item.postOwner && UserInfoCache.getUser().isAdmin()) {
            sellerAdminLayout.setVisibility(View.VISIBLE);

            initOrderTransactionStateSpinner(item);

            orderTransactionStateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

                    if (orderTransactionStateSpinner.getSelectedItem() != null) {
                        //String value = orderTransactionStateSpinner.getSelectedItem().toString();
                        String value = stateAdapter.getItem(i);
                        Log.d(TAG, "orderTransactionStateSpinner.onItemSelected: state="+value);
                        ViewUtil.ConversationOrderTransactionState state = ViewUtil.parseConversationOrderTransactionStateFromValue(value);
                        if (state != null) {
                            updateOrderTransactionState(item, state);
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
        } else {
            sellerAdminLayout.setVisibility(View.GONE);
        }

        return view;
    }

    private void updateOrderTransactionState(final ConversationVM conversation, final ViewUtil.ConversationOrderTransactionState state) {
        Log.d(TAG, "updateOrderTransactionState: conversation="+conversation.id+" to state="+state.name());
        AppController.getApiService().updateConversationOrderTransactionState(conversation.id, state.name(), new Callback<Response>() {
            @Override
            public void success(Response responseObj, Response response) {
                conversation.orderTransactionState = state.name();
                stateAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, "Failed to update order status", Toast.LENGTH_SHORT).show();
                Log.e(TAG, "updateOrderTransactionState: failure", error);
            }
        });
    }

    private void setOrderTransactionStateSpinner(ViewUtil.ConversationOrderTransactionState state) {
        String value = ViewUtil.getConversationOrderTransactionStateValue(state);
        int pos = ((ArrayAdapter)orderTransactionStateSpinner.getAdapter()).getPosition(value);
        orderTransactionStateSpinner.setSelection(pos);
    }

    private void initOrderTransactionStateSpinner(ConversationVM conversation) {
        if (stateAdapter == null) {
            List<String> orderTransactionStates = new ArrayList<>();
            orderTransactionStates.addAll(ViewUtil.getConversationOrderTransactionStateValues());

            stateAdapter = new ArrayAdapter<>(
                    orderTransactionStateSpinner.getContext(),
                    R.layout.spinner_item_admin,
                    orderTransactionStates);
        }

        orderTransactionStateSpinner.setAdapter(stateAdapter);

        // set previous value
        setOrderTransactionStateSpinner(ViewUtil.parseConversationOrderTransactionState(conversation.orderTransactionState));
    }

    public void toggleSelection(int position) {
        selectView(position, !selectedItemsIds.get(position));
    }

    public void removeSelection() {
        selectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value) {
            selectedItemsIds.put(position, value);
        } else {
            selectedItemsIds.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return selectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return selectedItemsIds;
    }
}
