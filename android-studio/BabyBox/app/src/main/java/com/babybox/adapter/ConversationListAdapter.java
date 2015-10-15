package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;

public class ConversationListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ConversationVM> conversations;
    private boolean showPost;

    private RelativeLayout conversationLayout;
    private LinearLayout postImageLayout;
    private ImageView userImage, postImage;
    private TextView userText, postTitleText, lastMessageText, soldText, dateText, unreadCountText;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this(activity, conversationVMs, true);
    }

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversations, boolean showPost) {
        this.activity = activity;
        this.conversations = conversations;
        this.showPost = showPost;
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
        soldText = (TextView) view.findViewById(R.id.soldText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        unreadCountText = (TextView) view.findViewById(R.id.unreadCountText);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        postImageLayout = (LinearLayout) view.findViewById(R.id.postImageLayout);
        postImage = (ImageView) view.findViewById(R.id.postImage);

        ConversationVM item = conversations.get(i);

        Log.d(this.getClass().getSimpleName(), "[" + i + "|Id=" + item.id + "] " + item.getPostTitle() + " unread=" + item.getUnread());
        if (item.getUnread() > 0) {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        } else {
            conversationLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.color.white));
        }

        unreadCountText.setText(item.getUnread() + "");
        unreadCountText.setVisibility(item.getUnread() > 0 ? View.VISIBLE : View.GONE);

        ImageUtil.displayThumbnailProfileImage(item.getUserId(), userImage);
        if (showPost) {
            postImageLayout.setVisibility(View.VISIBLE);
            ImageUtil.displayPostImage(item.getPostImage(), postImage);
            soldText.setVisibility(item.postSold ? View.VISIBLE : View.GONE);
        } else {
            postImageLayout.setVisibility(View.GONE);
        }

        userText.setText(item.getUserName());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);

        Log.d(this.getClass().getSimpleName(), item.getLastMessage());

        return  view;
    }
}
