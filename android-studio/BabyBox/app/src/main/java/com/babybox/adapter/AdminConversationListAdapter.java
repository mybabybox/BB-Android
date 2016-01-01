package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.AdminConversationVM;

import java.util.List;

public class AdminConversationListAdapter extends BaseAdapter {
    private static final String TAG = AdminConversationListAdapter.class.getName();

    private Activity activity;
    private LayoutInflater inflater;

    private List<AdminConversationVM> conversations;

    private LinearLayout conversationLayout;
    private LinearLayout postImageLayout, hasImageLayout;
    private ImageView user1Image, user2Image, postImage;
    private TextView user1Text, user2Text, postTitleText, lastMessageText, dateText, soldText;

    public AdminConversationListAdapter(Activity activity, List<AdminConversationVM> conversations) {
        this.activity = activity;
        this.conversations = conversations;
    }

    @Override
    public int getCount() {
        return conversations.size();
    }

    @Override
    public AdminConversationVM getItem(int i) {
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
            view = inflater.inflate(R.layout.admin_conversation_list_item, null);

        conversationLayout = (LinearLayout) view.findViewById(R.id.conversationLayout);
        user1Text = (TextView) view.findViewById(R.id.user1Text);
        user2Text = (TextView) view.findViewById(R.id.user2Text);
        postTitleText = (TextView) view.findViewById(R.id.postTitleText);
        lastMessageText = (TextView) view.findViewById(R.id.lastMessageText);
        hasImageLayout = (LinearLayout) view.findViewById(R.id.hasImageLayout);
        dateText = (TextView) view.findViewById(R.id.dateText);
        user1Image = (ImageView) view.findViewById(R.id.user1Image);
        user2Image = (ImageView) view.findViewById(R.id.user2Image);
        postImageLayout = (LinearLayout) view.findViewById(R.id.postImageLayout);
        postImage = (ImageView) view.findViewById(R.id.postImage);
        soldText = (TextView) view.findViewById(R.id.soldText);

        final AdminConversationVM item = conversations.get(i);

        ImageUtil.displayThumbnailProfileImage(item.getUser1Id(), user1Image);
        ImageUtil.displayThumbnailProfileImage(item.getUser2Id(), user2Image);
        ImageUtil.displayPostImage(item.getPostImage(), postImage);
        soldText.setVisibility(item.isPostSold()? View.VISIBLE : View.INVISIBLE);

        user1Text.setText(item.getUser1Name());
        user2Text.setText(item.getUser2Name());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);

        hasImageLayout.setVisibility(item.lastMessageHasImage? View.VISIBLE : View.GONE);

        return view;
    }
}
