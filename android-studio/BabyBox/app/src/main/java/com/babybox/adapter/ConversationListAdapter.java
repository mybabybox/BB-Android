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
    private ImageView userPicture;
    private TextView senderText,firstMessageText,dateText;

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

        ConversationVM item = conversationVMs.get(i);
        RelativeLayout layout = (RelativeLayout) view.findViewById(R.id.conversationLayout);

        if (item.getUnread() != 0) {
            layout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        }
        senderText = (TextView) view.findViewById(R.id.senderText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        firstMessageText = (TextView) view.findViewById(R.id.firstMessageText);
        userPicture = (ImageView) view.findViewById(R.id.userPicture);

        ImageUtil.displayThumbnailProfileImage(item.getUserId(), userPicture);

        senderText.setText(item.getUserName());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), firstMessageText, activity);

        Log.d(this.getClass().getSimpleName(), item.getLastMessage());

        return  view;
    }

}
