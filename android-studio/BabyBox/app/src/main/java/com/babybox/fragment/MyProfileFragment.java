package com.babybox.fragment;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
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

import java.io.File;
import java.lang.reflect.Field;

import com.babybox.R;
import com.babybox.activity.EditProfileActivity;
import com.babybox.activity.GameActivity;
import com.babybox.activity.MyProfileActionActivity;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragment;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.GameConstants;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.UserVM;
import com.bumptech.glide.load.resource.bitmap.GlideBitmapDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.mime.TypedFile;

public class MyProfileFragment extends TrackedFragment {

    private static final String TAG = MyProfileFragment.class.getName();
    private ImageView coverImage, profileImage, editCoverImage, editProfileImage, settingsIcon;
    private TextView userName, followersText, followingText;
    private LinearLayout userInfoLayout;
    private RelativeLayout settingsLayout;
    private Long userId;
    private Boolean isPhoto = false;
    private String selectedImagePath = null;
    private Uri selectedImageUri = null;
    private boolean coverImageClicked = false, profileImageClicked = false;
    private boolean hasProfilePic = false;

    private Button editButton, followButton, ordersButton;
    private LinearLayout gameLayout;
    private TextView pointsText;

    private FrameLayout tipsFrame;
    private TextView tipsDescText, tipsPointsText, tipsEndText;
    private ImageView cancelTipsButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        userName = (TextView) view.findViewById(R.id.usernameText);
        coverImage = (ImageView) view.findViewById(R.id.coverImage);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        editCoverImage = (ImageView) view.findViewById(R.id.editCoverImage);
        editProfileImage = (ImageView) view.findViewById(R.id.editProfileImage);

        settingsLayout = (RelativeLayout) view.findViewById(R.id.settingsLayout);
        editButton = (Button) view.findViewById(R.id.editButton);
        settingsIcon = (ImageView) view.findViewById(R.id.settingsIcon);

        followersText = (TextView) view.findViewById(R.id.followersText);
        followingText = (TextView) view.findViewById(R.id.followingText);

        followButton = (Button) view.findViewById(R.id.followButton);
        followButton.setVisibility(View.GONE);

        ordersButton = (Button) view.findViewById(R.id.ordersButton);
        ordersButton.setVisibility(View.VISIBLE);

        userInfoLayout = (LinearLayout) view.findViewById(R.id.userInfoLayout);
        gameLayout = (LinearLayout) view.findViewById(R.id.gameLayout);
        pointsText = (TextView) view.findViewById(R.id.pointsText);

        tipsFrame = (FrameLayout) view.findViewById(R.id.tipsFrame);
        tipsDescText = (TextView) view.findViewById(R.id.tipsDescText);
        tipsPointsText = (TextView) view.findViewById(R.id.tipsPointsText);
        tipsEndText = (TextView) view.findViewById(R.id.tipsEndText);
        cancelTipsButton = (ImageView) view.findViewById(R.id.cancelTipsButton);

        userInfoLayout.setVisibility(View.GONE);

        gameLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GameActivity.class);
                startActivityForResult(intent, ViewUtil.START_ACTIVITY_REQUEST_CODE);
            }
        });

        editCoverImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFragment.this.getActivity(), getString(R.string.edit_cover_image));
                isPhoto = true;
                coverImageClicked = true;
            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFragment.this.getActivity(), getString(R.string.edit_profile_image));
                isPhoto = true;
                profileImageClicked = true;
            }
        });

        editProfileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageUtil.openPhotoPicker(MyProfileFragment.this.getActivity(), getString(R.string.edit_profile_image));
                isPhoto = true;
                profileImageClicked = true;
            }
        });

        settingsLayout.setVisibility(View.VISIBLE);

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

        getUserInfo();
        getGameAccount();

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.SELECT_PICTURE_REQUEST_CODE) {
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
                getUserInfo();
                getGameAccount();

                // refresh parent activity
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_VALUE_REFRESH, true);
                getActivity().setResult(Activity.RESULT_OK, intent);
            }
        }
    }

    private void getUserInfo() {
        ViewUtil.showSpinner(getActivity());

        UserVM user = UserInfoCache.getUser();

        userId = user.getId();
        userName.setText(user.getDisplayName());

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

        followersText.setText(ViewUtil.followersFormat(user.numFollowers));
        followingText.setText(ViewUtil.followingFormat(user.numFollowing));
    }

    private void getGameAccount() {
        ViewUtil.showSpinner(getActivity());
        GameAccountVM gameAccount = UserInfoCache.getGameAccount();
        pointsText.setText(gameAccount.getGmpt() + "");
        hasProfilePic = gameAccount.hasProfilePic();
        if (hasProfilePic ||
                SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.UPLOAD_PROFILE_PIC_TIPS)) {
            tipsFrame.setVisibility(View.GONE);
        } else {
            tipsFrame.setVisibility(View.VISIBLE);
            tipsDescText.setText(getString(R.string.game_upload_profile_pic_title));
            tipsPointsText.setText("+" + GameConstants.POINTS_UPLOAD_PROFILE_PHOTO);
            cancelTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.UPLOAD_PROFILE_PIC_TIPS);
                    tipsFrame.setVisibility(View.GONE);
                }
            });
        }
        ViewUtil.stopSpinner(getActivity());
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

    private void uploadCoverImage(final long id){
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeCoverPhoto: Id=" + id);

        ImageUtil.clearCoverImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApi().uploadCoverPhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>() {
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
                Log.e(MyProfileFragment.class.getSimpleName(), "uploadCoverPhoto: failure", error);
            }
        });
    }

    private void uploadProfileImage(final long id) {
        ViewUtil.showSpinner(getActivity());

        Log.d(this.getClass().getSimpleName(), "changeProfilePhoto: Id=" + id);

        ImageUtil.clearProfileImageCache(id);

        File photo = new File(ImageUtil.getRealPathFromUri(getActivity(), selectedImageUri));
        photo = ImageUtil.resizeAsJPG(photo);   // IMPORTANT: resize before upload
        TypedFile typedFile = new TypedFile("application/octet-stream", photo);
        AppController.getApi().uploadProfilePhoto(typedFile,AppController.getInstance().getSessionId(),new Callback<Response>(){
            @Override
            public void success(Response response, Response response2) {
                if (!hasProfilePic) {
                    hasProfilePic = true;
                    tipsFrame.setVisibility(View.GONE);
                    ViewUtil.alertGameStatus(getActivity(),
                            getActivity().getString(R.string.game_upload_profile_pic_title),
                            GameConstants.POINTS_UPLOAD_PROFILE_PHOTO);
                }

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
                Log.e(MyProfileFragment.class.getSimpleName(), "uploadCoverPhoto: failure", error);
            }
        });
    }
}
