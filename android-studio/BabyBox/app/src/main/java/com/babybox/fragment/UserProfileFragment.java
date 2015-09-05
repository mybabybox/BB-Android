package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.lang.reflect.Field;

import com.babybox.R;
import com.babybox.activity.EditProfileActivity;
import com.babybox.activity.NewsfeedActivity;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.ProfileVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import retrofit.Callback;
import retrofit.RetrofitError;

public class UserProfileFragment extends TrackedFragment {

    private static final String TAG = UserProfileFragment.class.getName();
    private ImageView coverImage, profileImage;
    private TextView userName, userInfoText, followersText, followingText;
    private LinearLayout userInfoLayout;
    private RelativeLayout settingsLayout;
    private Button editButton, followButton, ordersButton;
    private LinearLayout gameLayout;
    private FrameLayout tipsFrame;

    private boolean following;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        userName = (TextView) view.findViewById(R.id.usernameText);
        coverImage = (ImageView) view.findViewById(R.id.coverImage);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);

        tipsFrame = (FrameLayout) view.findViewById(R.id.tipsFrame);
        tipsFrame.setVisibility(View.GONE);

        settingsLayout = (RelativeLayout) view.findViewById(R.id.settingsLayout);
        settingsLayout.setVisibility(View.GONE);

        followersText = (TextView) view.findViewById(R.id.followersText);
        followingText = (TextView) view.findViewById(R.id.followingText);

        followButton = (Button) view.findViewById(R.id.followButton);
        followButton.setVisibility(View.VISIBLE);

        ordersButton = (Button) view.findViewById(R.id.ordersButton);
        ordersButton.setVisibility(View.GONE);

        editButton = (Button) view.findViewById(R.id.editButton);
        editButton.setVisibility(View.GONE);

        userInfoLayout = (LinearLayout) view.findViewById(R.id.userInfoLayout);
        userInfoLayout.setVisibility(View.GONE);
        userInfoText = (TextView) view.findViewById(R.id.userInfoText);

        gameLayout = (LinearLayout) view.findViewById(R.id.gameLayout);
        gameLayout.setVisibility(View.GONE);

        ImageView editCoverImage = (ImageView) view.findViewById(R.id.editCoverImage);
        editCoverImage.setVisibility(View.GONE);
        ImageView editProfileImage = (ImageView) view.findViewById(R.id.editProfileImage);
        editProfileImage.setVisibility(View.GONE);

        final long userId = getArguments().getLong("oid");
        getUserProfile(userId);

        return view;
    }

    private void getUserProfile(final long userId) {
        ViewUtil.showSpinner(getActivity());

        AppController.getApi().getUserProfile(userId, AppController.getInstance().getSessionId(), new Callback<ProfileVM>() {
            @Override
            public void success(ProfileVM profile, retrofit.client.Response response) {
                userName.setText(profile.getDn());

                // admin only
                if (AppController.isUserAdmin()) {
                    userInfoText.setText(profile.toString());
                    userInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    userInfoLayout.setVisibility(View.GONE);
                }

                ImageUtil.displayProfileImage(userId, profileImage, new RequestListener<String, GlideBitmapDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideBitmapDrawable resource, String model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }
                });
                /*
                ImageUtil.displayCoverImage(userId, coverImage, new RequestListener<String, GlideBitmapDrawable>() {
                    @Override
                    public boolean onException(Exception e, String model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(GlideBitmapDrawable resource, String model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                        ViewUtil.stopSpinner(getActivity());
                        return false;
                    }
                });
                */

                followersText.setText(ViewUtil.followersFormat(profile.numFollowers));
                followingText.setText(ViewUtil.followingFormat(profile.numFollowing));

                following = profile.following;
                if (following) {
                    ViewUtil.selectFollowButtonStyle(followButton);
                } else {
                    ViewUtil.unselectFollowButtonStyle(followButton);
                }

                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        following = !following;
                        if (following) {
                            ViewUtil.selectFollowButtonStyle(followButton);
                        } else {
                            ViewUtil.unselectFollowButtonStyle(followButton);
                        }
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(UserProfileFragment.class.getSimpleName(), "getUserProfile: failure", error);
                ViewUtil.stopSpinner(getActivity());
            }
        });
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}