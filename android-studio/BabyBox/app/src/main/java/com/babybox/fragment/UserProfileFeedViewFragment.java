package com.babybox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.FeedFilter;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.lang.reflect.Field;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class UserProfileFeedViewFragment extends FeedViewFragment {

    private static final String TAG = UserProfileFeedViewFragment.class.getName();

    protected ImageView coverImage, profileImage, editCoverImage, editProfileImage, settingsIcon;
    protected TextView userNameText, followersText, followingsText, userInfoText;
    protected LinearLayout userInfoLayout;
    protected RelativeLayout settingsLayout;
    protected Button editButton, followButton, ordersButton, collectionsButton, productsButton, likesButton;

    protected FrameLayout tipsLayout;
    protected TextView tipsDescText, tipsEndText;
    protected ImageView dismissTipsButton;

    private boolean following;

    protected Long userId;
    protected FeedFilter.FeedType feedType;

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.user_profile_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        userNameText = (TextView) headerView.findViewById(R.id.userNameText);
        coverImage = (ImageView) headerView.findViewById(R.id.coverImage);
        profileImage = (ImageView) headerView.findViewById(R.id.profileImage);
        editCoverImage = (ImageView) headerView.findViewById(R.id.editCoverImage);
        editProfileImage = (ImageView) headerView.findViewById(R.id.editProfileImage);

        settingsLayout = (RelativeLayout) headerView.findViewById(R.id.settingsLayout);
        editButton = (Button) headerView.findViewById(R.id.editButton);
        settingsIcon = (ImageView) headerView.findViewById(R.id.settingsIcon);

        followersText = (TextView) headerView.findViewById(R.id.followersText);
        followingsText = (TextView) headerView.findViewById(R.id.followingsText);

        followButton = (Button) headerView.findViewById(R.id.followButton);
        ordersButton = (Button) headerView.findViewById(R.id.ordersButton);

        collectionsButton = (Button) headerView.findViewById(R.id.collectionsButton);
        productsButton = (Button) headerView.findViewById(R.id.productsButton);
        likesButton = (Button) headerView.findViewById(R.id.likesButton);

        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);

        userInfoLayout = (LinearLayout) headerView.findViewById(R.id.userInfoLayout);
        userInfoText = (TextView) headerView.findViewById(R.id.userInfoText);

        // init
        initLayout();
        initUserProfile();

        selectFeedFilter(FeedFilter.FeedType.USER_POSTED);

        return view;
    }

    /**
     * Subclass to override
     */
    protected void initLayout() {
        // hide
        editCoverImage.setVisibility(View.GONE);
        editProfileImage.setVisibility(View.GONE);
        settingsLayout.setVisibility(View.GONE);
        ordersButton.setVisibility(View.GONE);
        tipsLayout.setVisibility(View.GONE);
        userInfoLayout.setVisibility(View.GONE);

        // show
        followButton.setVisibility(View.VISIBLE);

        // actions
        productsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFeedFilter(FeedFilter.FeedType.USER_POSTED);
            }
        });

        likesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectFeedFilter(FeedFilter.FeedType.USER_LIKED);
            }
        });
    }

    /**
     * Subclass to override
     */
    protected void initUserProfile() {
        ViewUtil.showSpinner(getActivity());

        setUserId(getArguments().getLong(ViewUtil.BUNDLE_KEY_ID));

        AppController.getApiService().getUser(userId, new Callback<UserVM>() {
            @Override
            public void success(final UserVM user, retrofit.client.Response response) {
                userNameText.setText(user.getDisplayName());

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

                followersText.setText(ViewUtil.followersFormat(user.numFollowers));
                followingsText.setText(ViewUtil.followingsFormat(user.numFollowings));

                following = user.isFollowing;
                if (following) {
                    ViewUtil.selectFollowButtonStyle(followButton);
                } else {
                    ViewUtil.unselectFollowButtonStyle(followButton);
                }

                followButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (following) {
                            unFollow(user.getId());
                        } else {
                            follow(user.getId());
                        }
                    }
                });

                productsButton.setText(ViewUtil.productsTabFormat(user.numProducts));
                likesButton.setText(ViewUtil.likesTabFormat(user.numLikes));

                // admin only
                if (AppController.isUserAdmin()) {
                    userInfoText.setText(user.toString());
                    userInfoLayout.setVisibility(View.VISIBLE);
                } else {
                    userInfoLayout.setVisibility(View.GONE);
                }
                userInfoLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        userInfoLayout.setVisibility(View.GONE);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(UserProfileFeedViewFragment.class.getSimpleName(), "getUserProfile: failure", error);
                ViewUtil.stopSpinner(getActivity());
            }
        });
    }

    protected void selectFeedFilter(FeedFilter.FeedType feedType) {
        selectFeedFilter(feedType, true);
    }

    protected void selectFeedFilter(FeedFilter.FeedType feedType, boolean loadFeed) {
        if (FeedFilter.FeedType.USER_POSTED.equals(feedType)) {
            ViewUtil.selectProfileFeedButtonStyle(productsButton);
        } else {
            ViewUtil.unselectProfileFeedButtonStyle(productsButton);
        }

        if (FeedFilter.FeedType.USER_LIKED.equals(feedType)) {
            ViewUtil.selectProfileFeedButtonStyle(likesButton);
        } else {
            ViewUtil.unselectProfileFeedButtonStyle(likesButton);
        }

        this.feedType = feedType;

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    feedType,
                    userId));
        }
    }

    protected void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    protected void onRefreshView() {
        initUserProfile();
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

    public void follow(Long id){
        AppController.getApiService().followUser(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.selectFollowButtonStyle(followButton);
                following = true;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void unFollow(Long id){
        AppController.getApiService().unfollowUser(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                ViewUtil.unselectFollowButtonStyle(followButton);
                following = false;
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    public void onScrollUp() {
        LinearLayout bottomBarLayout = (LinearLayout) getActivity().findViewById(R.id.bottomBarLayout);
        if (bottomBarLayout != null) {
            bottomBarLayout.animate().translationY(bottomBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
    }

    public void onScrollDown() {
        LinearLayout bottomBarLayout = (LinearLayout) getActivity().findViewById(R.id.bottomBarLayout);
        if (bottomBarLayout != null) {
            bottomBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        }
    }
}