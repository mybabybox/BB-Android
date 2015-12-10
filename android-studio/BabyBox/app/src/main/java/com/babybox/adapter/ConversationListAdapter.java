package com.babybox.adapter;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ConversationVM;

import java.util.List;

public class ConversationListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;

    private List<ConversationVM> conversations;
    private boolean showPost;

    private RelativeLayout conversationLayout;
    private LinearLayout postImageLayout, hasImageLayout;
    public ImageView userImage, postImage,flipImage;
    private TextView userText, postTitleText, lastMessageText, buyText, sellText, soldText, dateText, unreadCountText;
    private SparseBooleanArray mSelectedItemsIds;

    private Interpolator accelerator = new AccelerateInterpolator();
    private Interpolator decelerator = new DecelerateInterpolator();

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversationVMs) {
        this(activity, conversationVMs, true);
    }

    public ConversationListAdapter(Activity activity, List<ConversationVM> conversations, boolean showPost) {
        this.activity = activity;
        this.conversations = conversations;
        this.showPost = showPost;
        mSelectedItemsIds = new SparseBooleanArray();
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

        System.out.println("item loaded ::::::::::::: ");

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
        flipImage = (ImageView) view.findViewById(R.id.userImage1);

        ConversationVM item = conversations.get(i);

        Log.d(this.getClass().getSimpleName(), "[" + i + "|id=" + item.id + "] " + item.getPostTitle() + " unread=" + item.getUnread());
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
            ViewUtil.setConversationImageTag(view, item);
        } else {
            postImageLayout.setVisibility(View.GONE);
        }

        userText.setText(item.getUserName());
        postTitleText.setText(item.getPostTitle());
        dateText.setText(DateTimeUtil.getTimeAgo(item.getLastMessageDate()));
        ViewUtil.setHtmlText(item.getLastMessage(), lastMessageText, activity);

        hasImageLayout.setVisibility(item.lastMessageHasImage? View.VISIBLE : View.GONE);

        if(AppController.getInstance().getSelectedIndex() != null && i == AppController.getInstance().getSelectedIndex())
            FlipImage(userImage, flipImage);

        return  view;
    }



    public void toggleSelection(int position) {
        selectView(position, !mSelectedItemsIds.get(position));
    }

    public void removeSelection() {
        mSelectedItemsIds = new SparseBooleanArray();
        notifyDataSetChanged();
    }

    public void selectView(int position, boolean value) {
        if (value)
            mSelectedItemsIds.put(position, value);
        else
            mSelectedItemsIds.delete(position);
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItemsIds.size();
    }

    public SparseBooleanArray getSelectedIds() {
        return mSelectedItemsIds;
    }

    private void FlipImage(ImageView imageView,ImageView imageView1) {
        final ImageView visibleList;
        final ImageView invisibleList;

        if (imageView.getVisibility() == View.GONE) {
            visibleList = imageView1;
            invisibleList = imageView;
        } else {
            invisibleList = imageView1;
            visibleList = imageView;
        }
        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
        visToInvis.setDuration(150);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY",
                -90f, 0f);

        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibleList.setVisibility(View.GONE);
                invisToVis.start();
                invisibleList.setVisibility(View.VISIBLE);
            }
        });
        visToInvis.start();
    }
}
