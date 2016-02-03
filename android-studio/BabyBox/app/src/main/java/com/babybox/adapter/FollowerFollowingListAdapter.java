package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVMLite;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class FollowerFollowingListAdapter extends BaseAdapter {
    private static final String TAG = FollowerFollowingListAdapter.class.getName();

    private ImageView userImage;
    private LinearLayout userLayout;
    private TextView userNameText, userFollowersText;
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
            convertView = inflater.inflate(R.layout.follower_following_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userLayout = (LinearLayout) convertView.findViewById(R.id.userLayout);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        userFollowersText = (TextView) convertView.findViewById(R.id.userFollowersText);
        followButton = (Button) convertView.findViewById(R.id.followButton);

        final UserVMLite item = users.get(position);

        if (UserInfoCache.getUser().id.equals(item.id)) {
            followButton.setVisibility(View.GONE);
        } else {
            followButton.setVisibility(View.VISIBLE);
        }

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getId(), userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        userNameText.setText(item.getDisplayName());
        userFollowersText.setText(ViewUtil.formatUserFollowers(item.getNumFollowers()));

        // follow
        if (item.isFollowing) {
            ViewUtil.selectFollowButtonStyle(followButton);
        } else {
            ViewUtil.unselectFollowButtonStyle(followButton);
        }

        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (item.isFollowing) {
                    unfollow(item);
                } else {
                    follow(item);
                }
            }
        });

        return convertView;
    }

    public void follow(final UserVMLite user){
        AppController.getApiService().followUser(user.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.selectFollowButtonStyle(followButton);
                user.isFollowing = true;
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(FollowerFollowingListAdapter.class.getSimpleName(), "follow: failure", error);
            }
        });
    }

    public void unfollow(final UserVMLite user){
        AppController.getApiService().unfollowUser(user.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.unselectFollowButtonStyle(followButton);
                user.isFollowing = false;
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(FollowerFollowingListAdapter.class.getSimpleName(), "unFollow: failure", error);
            }
        });
    }
}