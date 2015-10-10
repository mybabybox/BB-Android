package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    private List<ConversationVM> conversationVMs;
    private ImageView userImage, postImage;
    private TextView userText, postTitleText, lastMessageText, soldText, dateText, unreadCountText;

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this.activity = activity;
        this.conversationVMs = conversationVMs;
    }

    @Override
    public int getCount() {
        return conversationVMs.size();
    }

    @Override
    public ConversationVM getItem(int i) {
        return conversationVMs.get(i);
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

        userText = (TextView) view.findViewById(R.id.userText);
        postTitleText = (TextView) view.findViewById(R.id.postTitleText);
        lastMessageText = (TextView) view.findViewById(R.id.lastMessageText);
        soldText = (TextView) view.findViewById(R.id.soldText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        unreadCountText = (TextView) view.findViewById(R.id.unreadCountText);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        postImage = (ImageView) view.findViewById(R.id.postImage);

        ConversationVM item = conversationVMs.get(i);

        if (item.getUnread() != 0) {
            RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.conversationLayout);
            layout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        }

        unreadCountText.setText(item.getUnread()+"");
        unreadCountText.setVisibility(item.getUnread() > 0? View.VISIBLE : View.GONE);

        ImageUtil.displayThumbnailProfileImage(item.getUserId(), userImage);
        ImageUtil.displayPostImage(item.getPostImage(), postImage);

        userText.setText(item.getUserName());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);
        soldText.setVisibility(item.postSold? View.VISIBLE : View.GONE);

        Log.d(this.getClass().getSimpleName(), item.getLastMessage());

        return  view;
    }
}
