package com.babybox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.R;
import com.babybox.activity.EditProfileActivity;
import com.babybox.activity.MyProfileActionActivity;
import com.babybox.app.AppController;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.FeedFilter;
import com.babybox.util.GameConstants;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MyProfileFeedViewFragment extends UserProfileFeedViewFragment {

    private static final String TAG = MyProfileFeedViewFragment.class.getName();

    protected Boolean isPhoto = false;
    protected String selectedImagePath = null;
    protected Uri selectedImageUri = null;
    protected boolean coverImageClicked = false, profileImageClicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    protected void initLayout() {
        // hide
        followButton.setVisibility(View.GONE);
        userInfoLayout.setVisibility(View.GONE);

        // show
        editCoverImage.setVisibility(View.VISIBLE);
        editProfileImage.setVisibility(View.VISIBLE);
        settingsLayout.setVisibility(View.VISIBLE);
        ordersButton.setVisibility(View.VISIBLE);
        ViewUtil.showTips(SharedPreferencesUtil.Screen.MY_PROFILE_TIPS, tipsLayout, dismissTipsButton);

        // actions
        editCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFeedViewFragment.this.getActivity(), getString(R.string.edit_cover_image));
                isPhoto = true;
                coverImageClicked = true;
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFeedViewFragment.this.getActivity(), getString(R.string.edit_profile_image));
                isPhoto = true;
                profileImageClicked = true;
            }
        });

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFeedViewFragment.this.getActivity(), getString(R.string.edit_profile_image));
                isPhoto = true;
                profileImageClicked = true;
            }
        });

        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditProfileActivity.class);
                startActivityForResult(intent, ViewUtil.START_ACTIVITY_REQUEST_CODE);
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MyProfileActionActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ACTION_TYPE, "settings");
                startActivity(intent);
            }
        });

        ordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getActivity(), MyOrdersActivity.class);
                //startActivity(intent);
            }
        });

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

    @Override
    protected void initUserProfile() {
        ViewUtil.showSpinner(getActivity());

        UserVM user = UserInfoCache.getUser();
        setUserId(user.getId());

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

        productsButton.setText(ViewUtil.productsTabFormat(user.numProducts));
        likesButton.setText(ViewUtil.likesTabFormat(user.numLikes));
    }

    protected void uploadCoverImage(final long id){
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeCoverPhoto: Id=" + id);

        ImageUtil.clearCoverImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApiService().uploadCoverPhoto(typedFile, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayCoverImage(id, coverImage, new RequestListener<String, GlideBitmapDrawable>() {
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
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MyProfileFeedViewFragment.class.getSimpleName(), "uploadCoverPhoto: failure", error);
            }
        });
    }

    protected void uploadProfileImage(final long id) {
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeProfilePhoto: Id=" + id);

        ImageUtil.clearProfileImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApiService().uploadProfilePhoto(typedFile, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        ImageUtil.displayProfileImage(id, profileImage, new RequestListener<String, GlideBitmapDrawable>() {
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
                    }
                }, DefaultValues.DEFAULT_HANDLER_DELAY);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MyProfileFeedViewFragment.class.getSimpleName(), "uploadCoverPhoto: failure", error);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.SELECT_IMAGE_REQUEST_CODE) {
            if (data == null)
                return;

            selectedImageUri = data.getData();
            selectedImagePath = ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri);
            String path = selectedImageUri.getPath();

            Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri="+path+" selectedImagePath="+selectedImagePath);
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(selectedImagePath);
            if (bp != null) {
                if (coverImageClicked) {
                    coverImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    coverImage.setVisibility(View.VISIBLE);
                    uploadCoverImage(userId);
                    coverImageClicked = false;
                } else if (profileImageClicked) {
                    profileImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
                    profileImage.setVisibility(View.VISIBLE);
                    uploadProfileImage(userId);
                    profileImageClicked = false;
                }
            }
        }

        if(requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_VALUE_REFRESH, false);
            if (refresh) {
                initUserProfile();

                // refresh parent activity
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }
        }
    }
}