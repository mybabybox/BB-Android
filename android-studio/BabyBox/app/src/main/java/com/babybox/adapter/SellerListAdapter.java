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
import com.babybox.viewmodel.PostVMLite;
import com.babybox.viewmodel.SellerVM;
import com.babybox.viewmodel.UserVMLite;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SellerListAdapter extends BaseAdapter {
    private static final String TAG = SellerListAdapter.class.getName();

    private ImageView userImage, post1Image, post2Image, post3Image, post4Image;
    private LinearLayout userLayout;
    private TextView userNameText, userFollowersText, userDescText;
    private Button followButton;

    private Activity activity;
    private LayoutInflater inflater;

    private List<SellerVM> sellers;

    public SellerListAdapter(Activity activity, List<SellerVM> sellers) {
        this.activity = activity;
        this.sellers = sellers;
    }

    @Override
    public int getCount() {
        if (sellers == null)
            return 0;
        return sellers.size();
    }

    @Override
    public SellerVM getItem(int location) {
        if (sellers == null || location > sellers.size()-1)
            return null;
        return sellers.get(location);
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
            convertView = inflater.inflate(R.layout.seller_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userLayout = (LinearLayout) convertView.findViewById(R.id.userLayout);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        userFollowersText = (TextView) convertView.findViewById(R.id.userFollowersText);
        userDescText = (TextView) convertView.findViewById(R.id.userDescText);
        followButton = (Button) convertView.findViewById(R.id.followButton);

        post1Image = (ImageView) convertView.findViewById(R.id.post1Image);
        post2Image = (ImageView) convertView.findViewById(R.id.post2Image);
        post3Image = (ImageView) convertView.findViewById(R.id.post3Image);
        post4Image = (ImageView) convertView.findViewById(R.id.post4Image);

        post1Image.setVisibility(View.GONE);
        post2Image.setVisibility(View.GONE);
        post3Image.setVisibility(View.GONE);
        post4Image.setVisibility(View.GONE);

        List<ImageView> postImages = new ArrayList<>();
        postImages.add(post1Image);
        postImages.add(post2Image);
        postImages.add(post3Image);
        postImages.add(post4Image);

        final SellerVM item = sellers.get(position);

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

        if (!StringUtils.isEmpty(item.getAboutMe())) {
            userDescText.setText(item.getAboutMe());
            userDescText.setVisibility(View.VISIBLE);
        } else {
            userDescText.setVisibility(View.GONE);
        }

        // follow
        if (item.isFollowing) {
            ViewUtil.selectFollowButtonStyleLite(followButton);
        } else {
            ViewUtil.unselectFollowButtonStyleLite(followButton);
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

        // posts images
        int i = 0;
        for (PostVMLite post : item.getPosts()) {
            if (post.images != null && post.images.length > 0) {
                Log.d(TAG, item.displayName+" post image "+(i+1)+": "+post.images[0]);
                try {
                    ImageView postImage = postImages.get(i);
                    postImage.setVisibility(View.VISIBLE);
                    ImageUtil.displayPostImage(post.images[0], postImage);
                    i++;
                } catch (Exception e) {
                    break;
                }
            }
        }

        return convertView;
    }

    public void follow(final UserVMLite user){
        AppController.getApiService().followUser(user.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.selectFollowButtonStyleLite(followButton);
                user.isFollowing = true;
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "follow: failure", error);
            }
        });
    }

    public void unfollow(final UserVMLite user){
        AppController.getApiService().unfollowUser(user.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                ViewUtil.unselectFollowButtonStyleLite(followButton);
                user.isFollowing = false;
                notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(SellerListAdapter.class.getSimpleName(), "unFollow: failure", error);
            }
        });
    }
}