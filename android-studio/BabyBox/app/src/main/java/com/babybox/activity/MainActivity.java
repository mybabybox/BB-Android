package com.babybox.activity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.NotificationCache;
import com.babybox.app.TrackedFragment;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.fragment.ActivityMainFragment;
import com.babybox.fragment.HomeMainFragment;
import com.babybox.fragment.ProfileMainFragment;
import com.babybox.listener.EndlessScrollListener;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.NotificationsParentVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends TrackedFragmentActivity {

    private View actionBarView;
    private RelativeLayout userLayout;
    private ImageView userImage;
    private TextView userNameText;
    private ImageView signInImage;
    private ImageView newPostIcon;

    private ViewGroup chatLayout;
    private TextView chatCountText;
    private LinearLayout bottomBarLayout;

    private LinearLayout homeLayout;
    private ImageView homeImage;
    private TextView homeText;

    private LinearLayout activityLayout;
    private ImageView activityImage;
    private TextView activityText;

    private LinearLayout profileLayout;
    private ImageView profileImage;
    private TextView profileText;

    private boolean homeClicked = false, activityClicked = false, profileClicked = false;

    private TextView notificationCount;

    private boolean showBottomMenuBar = true;

    private TrackedFragment selectedFragment;

    private static MainActivity mInstance;

    public static synchronized MainActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);

        setContentView(R.layout.main_activity);

        mInstance = this;

        // actionbar
        actionBarView = getLayoutInflater().inflate(R.layout.main_actionbar, null);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getActionBar().setCustomView(actionBarView, lp);
        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().show();

        userLayout = (RelativeLayout) actionBarView.findViewById(R.id.userLayout);
        userImage = (ImageView) actionBarView.findViewById(R.id.userImage);
        userNameText = (TextView) actionBarView.findViewById(R.id.userNameText);

        signInImage = (ImageView) actionBarView.findViewById(R.id.signInImage);
        chatCountText = (TextView) actionBarView.findViewById(R.id.chatCountText);
        chatLayout = (ViewGroup) actionBarView.findViewById(R.id.chatLayout);
        newPostIcon = (ImageView) actionBarView.findViewById(R.id.newPostIcon);

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(mascotIcon);
            }
        }, 2000);
        */

        // user profile thumbnail
        ImageUtil.displayThumbnailProfileImage(UserInfoCache.getUser().getId(), userImage);
        userNameText.setText(UserInfoCache.getUser().getDisplayName());

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressProfileTab();
            }
        });

        signInImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch game
                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                startActivity(intent);
            }
        });

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MyProfileActionActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ACTION_TYPE, "messages");
                startActivity(intent);
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NewPostActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
                intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromMainActivity");
                startActivity(intent);
            }
        });

        refreshNotifications();

        // bottom menu bar
        bottomBarLayout = (LinearLayout) findViewById(R.id.bottomBarLayout);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        homeImage = (ImageView) findViewById(R.id.homeImage);
        homeText = (TextView) findViewById(R.id.homeText);

        activityLayout = (LinearLayout) findViewById(R.id.activityLayout);
        activityImage = (ImageView) findViewById(R.id.activityImage);
        activityText = (TextView) findViewById(R.id.activityText);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileText = (TextView) findViewById(R.id.profileText);
        notificationCount = (TextView) findViewById(R.id.notificationCount);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Home tab clicked");
                pressHomeTab();
            }
        });

        activityLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Activity tab clicked");
                pressActivityTab();
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Profile tab clicked");
                pressProfileTab();
            }
        });

        pressHomeTab();
    }

    @Override
    public void onStart() {
        super.onStart();

        NotificationCache.refresh(new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM notificationsParentVM, Response response) {
                setUnreadNotificationsCount();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(MainActivity.class.getSimpleName(), "onStart: NotificationCache.refresh: failure", error);
            }
        });
    }

    public void refreshNotifications() {
        chatCountText.setVisibility(View.INVISIBLE);
        if (NotificationCache.getNotifications() == null) {
            return;
        }

        if (NotificationCache.getNotifications().getMessageCount() > 0) {
            chatCountText.setVisibility(View.VISIBLE);
            chatCountText.setText(NotificationCache.getNotifications().getMessageCount() + "");
        }
    }

    public void pressHomeTab() {
        if (!homeClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new HomeMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home_sel, R.color.sharp_pink);
        homeClicked = true;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressActivityTab() {
        if (!activityClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new ActivityMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif_sel, R.color.sharp_pink);
        activityClicked = true;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressProfileTab() {
        if (!profileClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new ProfileMainFragment();
            selectedFragment.setTrackedOnce();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
            notificationCount.setVisibility(View.INVISIBLE);
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile_sel, R.color.sharp_pink);
        profileClicked = true;

        setUnreadNotificationsCount();
    }

    private void setMenuButton(ImageView imageView, TextView textView, int image, int textColor) {
        imageView.setImageDrawable(this.getResources().getDrawable(image));
        textView.setTextColor(this.getResources().getColor(textColor));
    }

    @Override
    public void onBackPressed() {
        if (selectedFragment != null && !selectedFragment.allowBackPressed()) {
            return;
        }

        if (isTaskRoot()) {
            if (!showBottomMenuBar) {
                resetControls();
                return;
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearAll();
                            MainActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(this.getClass().getSimpleName(), "onDestroy: clear all");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void setUnreadNotificationsCount() {
        NotificationsParentVM notificationsParentVM = NotificationCache.getNotifications();
        if (notificationsParentVM == null) {
            return;
        }

        long count = notificationsParentVM.getRequestCounts() + notificationsParentVM.getNotifyCounts()+notificationsParentVM.getMessageCount();

        Log.d(this.getClass().getSimpleName(), "setUnreadNotificationsCount: requestCount=" + notificationsParentVM.getRequestCounts() + " notifCount=" + notificationsParentVM.getNotifyCounts());

        if(count == 0) {
            notificationCount.setVisibility(View.INVISIBLE);
        } else {
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(count+"");
        }
    }

    public void showActionBar(boolean show) {
        ActionBar actionBar = getActionBar();
        if (actionBar == null) {
            return;
        }

        if (show) {
            actionBar.show();
        } else {
            actionBar.hide();
        }
    }

    public void showBottomMenuBar(boolean show) {
        if (bottomBarLayout == null) {
            return;
        }

        if (show) {
            bottomBarLayout.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
        } else {
            bottomBarLayout.animate().translationY(bottomBarLayout.getHeight()).setInterpolator(new AccelerateInterpolator(2)).start();
        }
        showBottomMenuBar = show;
    }

    public void resetControls() {
        showBottomMenuBar(true);
        EndlessScrollListener.setScrollReset();
    }
}

