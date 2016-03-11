package com.babybox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.activity.EditProfileActivity;
import com.babybox.activity.SettingsActivity;
import com.babybox.app.AppController;
import com.babybox.app.NotificationCounter;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.FeedFilter;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.File;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MyProfileFeedViewFragment extends UserProfileFeedViewFragment {

    private static final String TAG = MyProfileFeedViewFragment.class.getName();

    protected Boolean isPhoto = false;
    protected Uri selectedImageUri = null;
    protected boolean coverImageClicked = false, profileImageClicked = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        NotificationCounter.refresh();
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
        editButton.setVisibility(View.VISIBLE);
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
                ViewUtil.startEditProfileActivity(getActivity());
            }
        });

        settingsIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startSettingsActivity(getActivity());
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

    private void setUserProfile(UserVM user) {
        ViewUtil.showSpinner(getActivity());

        setUserId(user.getId());

        initUserInfoLayout(user);

        ImageUtil.displayMyProfileImage(userId, profileImage, new RequestListener<String, GlideBitmapDrawable>() {
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

        followersText.setText(ViewUtil.formatFollowers(user.numFollowers));
        followersText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startFollowersActivity(getActivity(), userId);
            }
        });

        followingsText.setText(ViewUtil.formatFollowings(user.numFollowings));
        followingsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startFollowingsActivity(getActivity(), userId);
            }
        });

        productsButton.setText(ViewUtil.formatProductsTab(user.numProducts));
        likesButton.setText(ViewUtil.formatLikesTab(user.numLikes));
    }

    @Override
    protected void initUserProfile() {
        UserVM user = UserInfoCache.getUser();
        setUserProfile(user);
    }

    @Override
    protected void onRefreshView() {
        super.onRefreshView();
        ViewUtil.showSpinner(getActivity());

        UserInfoCache.refresh(new Callback<UserVM>() {
            @Override
            public void success(UserVM user, Response response) {
                setUserProfile(user);
                ViewUtil.stopSpinner(getActivity());
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(getActivity());
                Log.e(EditProfileActivity.class.getSimpleName(), "onRefreshView: failure", error);
            }
        });
    }

    protected void uploadCoverImage(final long id, final String imagePath) {
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "uploadCoverImage: Id=" + id);

        ImageUtil.clearCoverImageCache(id);

        //File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        //photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload

        File photo = new File(imagePath);
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApiService().uploadCoverPhoto(typedFile, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
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

    protected void uploadProfileImage(final long id, final String imagePath) {
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "uploadProfileImage: id=" + id + " imagePath=" + imagePath);

        ImageUtil.clearProfileImageCache(id);

        //File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        //photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload

        File photo = new File(imagePath);
        if (photo != null) {
            TypedFile typedFile = new TypedFile("application/octet-stream", photo);
            AppController.getApiService().uploadProfilePhoto(typedFile, new Callback<Response>() {
                @Override
                public void success(Response responseObject, Response response) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            ImageUtil.displayMyProfileImage(id, profileImage, new RequestListener<String, GlideBitmapDrawable>() {
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
                    Log.e(MyProfileFeedViewFragment.class.getSimpleName(), "uploadProfileImage: failure", error);
                }
            });
        } else {
            ViewUtil.alert(getActivity(), getActivity().getString(R.string.photo_size_too_big));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {

                String imagePath = "";
                if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE  && data != null) {
                    selectedImageUri = data.getData();
                    imagePath = ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri);
                }

                Log.d(this.getClass().getSimpleName(), "onActivityResult: imagePath=" + imagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(imagePath);
                if (bitmap != null) {
                    ViewUtil.startSelectImageActivity(getActivity(), selectedImageUri);
                } else {
                    Toast.makeText(getActivity(), getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                String croppedImagePath = data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT);
                if (coverImageClicked) {
                    setImagePreviewThumbnail(coverImage, croppedImagePath);
                    uploadCoverImage(userId, croppedImagePath);
                    coverImageClicked = false;
                } else if (profileImageClicked) {
                    setImagePreviewThumbnail(profileImage, croppedImagePath);
                    uploadProfileImage(userId, croppedImagePath);
                    profileImageClicked = false;
                }
            } else if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE) {
                boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
                if (refresh) {
                    initUserProfile();

                    // refresh parent activity
                    ViewUtil.setActivityResult(getActivity(), true);
                }
            }
        }
    }

    private void setImagePreviewThumbnail(ImageView imageView, String imagePath) {
        if (!StringUtils.isEmpty(imagePath)) {
            Bitmap bp = ImageUtil.resizeAsPreviewThumbnail(imagePath);
            imageView.setImageBitmap(bp);
            //imageView.setImageURI(Uri.parse(imagePath);
            imageView.setVisibility(View.VISIBLE);
        }
    }

}