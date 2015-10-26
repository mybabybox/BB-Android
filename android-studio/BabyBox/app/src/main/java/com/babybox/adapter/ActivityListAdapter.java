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

import com.babybox.R;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ActivityVM;
import com.babybox.viewmodel.ConversationVM;

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

        Log.d(this.getClass().getSimpleName(), "[" + i + "|id=" + item.id + "] " + item.getActivityType() + " actor=" + item.actorName + " target=" + item.targetName);
        if (!item.isViewed()) {
            activityLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));
        } else {
            activityLayout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.color.white));
        }

        ImageUtil.displayThumbnailProfileImage(item.getActorImage(), userImage);
        ImageUtil.displayPostImage(item.getTargetImage(), postImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startUserProfileActivity(activity, item.getActor());
            }
        });

        ViewUtil.setClickableText(messageText, item.getTargetName(), item.getActorName(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getActor());
            }
        });

        dateText.setText(DateTimeUtil.getTimeAgo(item.getCreatedDate()));

        return  view;
    }
}
