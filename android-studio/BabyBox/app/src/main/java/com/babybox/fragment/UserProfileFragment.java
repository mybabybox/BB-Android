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
import android.widget.TextView;

import java.lang.reflect.Field;

import com.babybox.R;
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
    private TextView questionsCount, answersCount, bookmarksCount, userName, userInfoText;
    private LinearLayout questionMenu, answerMenu, bookmarksMenu, settingsMenu, userInfoLayout;
    private Button editButton;
    private LinearLayout gameLayout;
    private FrameLayout uploadProfilePicTipsLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.user_profile_fragment, container, false);

        userName = (TextView) view.findViewById(R.id.usernameText);
        questionsCount = (TextView) view.findViewById(R.id.questionsCount);
        answersCount = (TextView) view.findViewById(R.id.answersCount);
        bookmarksCount = (TextView) view.findViewById(R.id.bookmarksCount);
        coverImage = (ImageView) view.findViewById(R.id.coverImage);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        questionMenu = (LinearLayout) view.findViewById(R.id.menuQuestion);
        answerMenu = (LinearLayout) view.findViewById(R.id.menuAnswer);

        uploadProfilePicTipsLayout = (FrameLayout) view.findViewById(R.id.uploadProfileImageTipsLayout);
        uploadProfilePicTipsLayout.setVisibility(View.GONE);

        bookmarksMenu = (LinearLayout) view.findViewById(R.id.menuBookmarks);
        bookmarksMenu.setVisibility(View.GONE);

        settingsMenu = (LinearLayout) view.findViewById(R.id.menuSettings);
        settingsMenu.setVisibility(View.GONE);

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

        questionMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = new Bundle();
                bundle.putLong(ViewUtil.BUNDLE_KEY_ID,getArguments().getLong("oid"));
                bundle.putString("key","userquestion");

                NewsfeedListFragment fragment = new NewsfeedListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
                */

                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id",getArguments().getLong("oid"));
                intent.putExtra("key","userquestion");
                startActivity(intent);
            }
        });

        answerMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                Bundle bundle = new Bundle();
                bundle.putLong(ViewUtil.BUNDLE_KEY_ID,getArguments().getLong("oid"));
                bundle.putString("key","useranswer");

                NewsfeedListFragment fragment = new NewsfeedListFragment();
                FragmentManager fragmentManager = getFragmentManager();
                fragment.setArguments(bundle);
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.hide(UserProfileFragment.this);
                fragmentTransaction.replace(R.id.placeHolder, fragment);
                fragmentTransaction.commit();
                */

                Intent intent = new Intent(getActivity(), NewsfeedActivity.class);
                intent.putExtra("id", getArguments().getLong("oid"));
                intent.putExtra("key", "useranswer");
                startActivity(intent);
            }
        });

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
                questionsCount.setText(profile.getQc() + "");
                answersCount.setText(profile.getAc() + "");

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