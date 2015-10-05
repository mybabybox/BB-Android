package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVMLite;

import java.util.List;

public class FollowerFollowingListAdapter extends BaseAdapter {
    private ImageView userImage;
    private TextView userNameText;
    private Button followButton;

    private Activity activity;
    private LayoutInflater inflater;

    private List<UserVMLite> users;

    public FollowerFollowingListAdapter(Activity activity, List<UserVMLite> users) {
        this.activity = activity;
        this.users = users;
    }

    @Override
    public int getCount() {
        if (users == null)
            return 0;
        return users.size();
    }

    @Override
    public UserVMLite getItem(int location) {
        if (users == null || location > users.size()-1)
            return null;
        return users.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public synchronized View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.comment_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        followButton = (Button) convertView.findViewById(R.id.followButton);

        final UserVMLite item = users.get(position);

        userNameText.setText(item.getDisplayName());
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getId(), userImage);

        return convertView;
    }
}