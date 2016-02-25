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
import com.babybox.app.NotificationCounter;
import com.babybox.app.TrackedFragment;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.fragment.ActivityMainFragment;
import com.babybox.fragment.HomeMainFragment;
import com.babybox.fragment.ProfileMainFragment;
import com.babybox.fragment.SellerMainFragment;
import com.babybox.listener.EndlessScrollListener;
import com.babybox.util.ImageUtil;
import com.babybox.util.LocationUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.NotificationCounterVM;

public class MainActivity extends TrackedFragmentActivity {

    private RelativeLayout userLayout;
    private ImageView userImage, gameBadgeImage;
    private TextView userNameText;

    private ViewGroup chatLayout, newPostLayout;
    private TextView chatCountText;
    private LinearLayout bottomBarLayout;

    private LinearLayout homeLayout;
    private ImageView homeImage;
    private TextView homeText;

    private LinearLayout sellerLayout;
    private ImageView sellerImage;
    private TextView sellerText;

    private LinearLayout activityLayout;
    private ImageView activityImage;
    private TextView activityText;
    private TextView activityCountText;

    private LinearLayout profileLayout;
    private ImageView profileImage;
    private TextView profileText;

    private boolean homeClicked = false, sellerClicked = false, activityClicked = false, profileClicked = false;

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

        userLayout = (RelativeLayout) findViewById(R.id.userLayout);
        userImage = (ImageView) findViewById(R.id.userImage);
        userNameText = (TextView) findViewById(R.id.userNameText);
        gameBadgeImage = (ImageView) findViewById(R.id.gameBadgeImage);

        chatCountText = (TextView) findViewById(R.id.chatCountText);
        chatLayout = (ViewGroup) findViewById(R.id.chatLayout);
        newPostLayout = (ViewGroup) findViewById(R.id.newPostLayout);

        // user profile thumbnail
        setUserProfileThumbnail();

        userLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pressProfileTab();
            }
        });

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(mascotIcon);
            }
        }, 2000);
        */

        gameBadgeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startGameBadgesActivity(MainActivity.this, UserInfoCache.getUser().getId());
            }
        });

        chatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startConversationListActivity(MainActivity.this);
            }
        });

        newPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startNewPostActivity(MainActivity.this, 0L);
            }
        });

        // bottom menu bar
        bottomBarLayout = (LinearLayout) findViewById(R.id.bottomBarLayout);

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        homeImage = (ImageView) findViewById(R.id.homeImage);
        homeText = (TextView) findViewById(R.id.homeText);

        sellerLayout = (LinearLayout) findViewById(R.id.sellerLayout);
        sellerImage = (ImageView) findViewById(R.id.sellerImage);
        sellerText = (TextView) findViewById(R.id.sellerText);

        activityLayout = (LinearLayout) findViewById(R.id.activityLayout);
        activityImage = (ImageView) findViewById(R.id.activityImage);
        activityText = (TextView) findViewById(R.id.activityText);
        activityCountText = (TextView) findViewById(R.id.activityCountText);

        profileLayout = (LinearLayout) findViewById(R.id.profileLayout);
        profileImage = (ImageView) findViewById(R.id.profileImage);
        profileText = (TextView) findViewById(R.id.profileText);

        homeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Home tab clicked");
                pressHomeTab();
            }
        });

        sellerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Seller tab clicked");
                pressSellerTab();
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

        checkAndroidUpgrade();
    }

    @Override
    public void onResume() {
        super.onResume();

        NotificationCounter.refresh();

        // handle gcm
        if (ViewUtil.isGcmLaunchTarget(getIntent())) {
            pressActivityTab();
            getIntent().removeExtra(ViewUtil.GCM_LAUNCH_TARGET);
        }
    }

    public void setUserProfileThumbnail() {
        ImageUtil.clearImageView(userImage);
        ImageUtil.displayMyThumbnailProfileImage(UserInfoCache.getUser().getId(), userImage);
        userNameText.setText(UserInfoCache.getUser().getDisplayName());
    }

    public void pressHomeTab() {
        if (!homeClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new HomeMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home_sel, R.color.sharp_pink);
        homeClicked = true;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;
    }

    public void pressSellerTab() {
        if (!sellerClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new SellerMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller_sel, R.color.sharp_pink);
        sellerClicked = true;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;
    }

    public void pressActivityTab() {
        if (!activityClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new ActivityMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();

            // read... clear gcm notifs
            SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_COMMENT_NOTIFS);
            SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.GCM_FOLLOW_NOTIFS);
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif_sel, R.color.sharp_pink);
        activityClicked = true;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;
    }

    public void pressProfileTab() {
        pressProfileTab(false);
    }

    public void pressProfileTab(boolean refresh) {
        if (!profileClicked || refresh) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new ProfileMainFragment();
            selectedFragment.setTrackedOnce();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(sellerImage, sellerText, R.drawable.mn_seller, R.color.dark_gray_2);
        sellerClicked = false;

        setMenuButton(activityImage, activityText, R.drawable.mn_notif, R.color.dark_gray_2);
        activityClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile_sel, R.color.sharp_pink);
        profileClicked = true;
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
                            AppController.getInstance().clearUserCaches();
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
        Log.d(this.getClass().getSimpleName(), "onActivityResult: requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
            if (refresh) {
                setUserProfileThumbnail();
                pressProfileTab(true);
                return;     // handled... dont trickle down to fragments
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void refreshNotifications() {
        NotificationCounterVM counter = NotificationCounter.getCounter();
        if (counter == null) {
            return;
        }

        Log.d(this.getClass().getSimpleName(), "refreshNotifications: activitiesCount=" + counter.activitiesCount + " conversationsCount=" + counter.conversationsCount);

        if (counter.activitiesCount == 0) {
            activityCountText.setVisibility(View.INVISIBLE);
        } else {
            activityCountText.setVisibility(View.VISIBLE);
            activityCountText.setText(counter.activitiesCount+"");
        }

        if (counter.conversationsCount == 0) {
            chatCountText.setVisibility(View.INVISIBLE);
        } else {
            chatCountText.setVisibility(View.VISIBLE);
            chatCountText.setText(counter.conversationsCount+"");
        }
    }

    public void checkAndroidUpgrade() {
        if (UserInfoCache.requestAndroidUpgrade()) {
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
            alertDialogBuilder.setMessage(getString(R.string.request_upgrade));
            alertDialogBuilder.setPositiveButton(getString(R.string.request_upgrade_confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    ViewUtil.openPlayStoreForUpgrade(MainActivity.this);
                }
            });
            alertDialogBuilder.setNegativeButton(getString(R.string.request_upgrade_cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    UserInfoCache.skipAndroidUpgrade();
                }
            });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
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