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

import com.babybox.R;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ActivityVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.List;

public class ActivityListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<ActivityVM> activities;

    private RelativeLayout activityLayout;
    private ImageView userImage, postImage;
    private TextView messageText, dateText;

    public ActivityListAdapter(Activity activity, List<ActivityVM> activities) {
        this.activity = activity;
        this.activities = activities;
    }

    @Override
    public int getCount() {
        return activities.size();
    }

    @Override
    public ActivityVM getItem(int i) {
        return activities.get(i);
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
            view = inflater.inflate(R.layout.activity_list_item, null);

        activityLayout = (RelativeLayout) view.findViewById(R.id.activityLayout);
        messageText = (TextView) view.findViewById(R.id.messageText);
        dateText = (TextView) view.findViewById(R.id.dateText);
        userImage = (ImageView) view.findViewById(R.id.userImage);
        postImage = (ImageView) view.findViewById(R.id.postImage);

        final ActivityVM item = activities.get(i);

        //Log.d(this.getClass().getSimpleName(), "[" + i + "|id=" + item.id + "] " + item.getActivityType() + " actor=" + item.actorName + " target=" + item.targetName);
        if (!item.isViewed()) {
            activityLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        } else {
            activityLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.color.white));
        }

        ImageUtil.displayThumbnailProfileImage(item.getActorImage(), userImage);
        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(activity, item.getActor());
            }
        });

        setMessageText(item);

        dateText.setText(DateTimeUtil.getTimeAgo(item.getCreatedDate()));

        return view;
    }

    private void setMessageText(final ActivityVM item) {
        String message = "";

        switch (item.getActivityType()) {
            case "FIRST_POST":
                message = activity.getString(R.string.activity_first_post) + "\n" + item.getTargetName();
                break;
            case "NEW_POST":
                message = activity.getString(R.string.activity_posted) + "\n" + item.getTargetName();
                break;
            case "NEW_COMMENT":
                message = activity.getString(R.string.activity_commented) + "\n" + item.getTargetName();
                break;
            case "LIKED":
                message = activity.getString(R.string.activity_liked);
                break;
            case "FOLLOWED":
                message = activity.getString(R.string.activity_followed);
                break;
            case "SOLD":
                message = activity.getString(R.string.activity_sold);
                break;
            case "NEW_GAME_BADGE":
                message = activity.getString(R.string.activity_game_badge) + "\n\"" + item.getTargetName() + "\"";
                break;
        }

        // skip unknown activities
        if (StringUtils.isEmpty(message)) {
            return;
        }

        // link is always actor user
        ViewUtil.setClickableText(messageText, item.getActorName(), message, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getActor());
            }
        });

        switch (item.getActivityType()) {
            case "FIRST_POST":
            case "NEW_POST":
            case "NEW_COMMENT":
            case "LIKED":
            case "SOLD":
                // open product
                activityLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startProductActivity(activity, item.getTarget());
                    }
                });
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startProductActivity(activity, item.getTarget());
                    }
                });
                ImageUtil.displayPostImage(item.getTargetImage(), postImage);
                postImage.setVisibility(View.VISIBLE);
                break;
            case "FOLLOWED":
                // open actor user
                activityLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startUserProfileActivity(activity, item.getActor());
                    }
                });
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startUserProfileActivity(activity, item.getActor());
                    }
                });
                postImage.setVisibility(View.INVISIBLE);
                break;
            case "NEW_GAME_BADGE":
                // open game badges
                activityLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startGameBadgesActivity(activity, UserInfoCache.getUser().id);
                    }
                });
                messageText.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startGameBadgesActivity(activity, UserInfoCache.getUser().id);
                    }
                });
                postImage.setVisibility(View.INVISIBLE);
                break;
        }
    }
}
