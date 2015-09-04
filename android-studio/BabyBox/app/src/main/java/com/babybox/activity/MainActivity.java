package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.LocalCommunityTabCache;
import com.babybox.app.NotificationCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.CommunityMainFragment;
import com.babybox.app.TrackedFragment;
import com.babybox.fragment.HomeMainFragment;
import com.babybox.fragment.ProfileMainFragment;
import com.babybox.fragment.SchoolsMainFragment;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunityCategoryMapVM;
import com.babybox.viewmodel.NotificationsParentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainActivity extends TrackedFragmentActivity {

    private LinearLayout homeLayout;
    private ImageView homeImage;
    private TextView homeText;

    private LinearLayout commsLayout;
    private ImageView commsImage;
    private TextView commsText;

    private LinearLayout schoolsLayout;
    private ImageView schoolsImage;
    private TextView schoolsText;

    private LinearLayout profileLayout;
    private ImageView profileImage;
    private TextView profileText;

    private boolean homeClicked = false, commsClicked = false, schoolsClicked = false, profileClicked = false;

    private boolean topicCommunityTabLoaded = false;
    private boolean yearCommunityTabLoaded = false;

    private TextView notificationCount;

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

        getActionBar().hide();

        homeLayout = (LinearLayout) findViewById(R.id.homeLayout);
        homeImage = (ImageView) findViewById(R.id.homeImage);
        homeText = (TextView) findViewById(R.id.homeText);

        commsLayout = (LinearLayout) findViewById(R.id.commsLayout);
        commsImage = (ImageView) findViewById(R.id.commsImage);
        commsText = (TextView) findViewById(R.id.commsText);

        schoolsLayout = (LinearLayout) findViewById(R.id.schoolsLayout);
        schoolsImage = (ImageView) findViewById(R.id.schoolsImage);
        schoolsText = (TextView) findViewById(R.id.schoolsText);

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

        commsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Community tab clicked");
                pressCommTab();
            }
        });

        schoolsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Schools tab clicked");
                pressSearchTab();
            }
        });

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(MainActivity.this.getClass().getSimpleName(), "onClick: Profile tab clicked");
                pressProfileTab();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

        init();

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

    public void pressHomeTab() {
        getActionBar().hide();

        if (!homeClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new HomeMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home_sel, R.color.sharp_pink);
        homeClicked = true;

        setMenuButton(commsImage, commsText, R.drawable.mn_comm, R.color.dark_gray_2);
        commsClicked = false;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag, R.color.dark_gray_2);
        schoolsClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressCommTab() {
        getActionBar().hide();

        if (!commsClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new CommunityMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(commsImage, commsText, R.drawable.mn_comm_sel, R.color.sharp_pink);
        commsClicked = true;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag, R.color.dark_gray_2);
        schoolsClicked = false;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressSearchTab() {
        getActionBar().hide();

        if (!schoolsClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new SchoolsMainFragment();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(commsImage, commsText, R.drawable.mn_comm, R.color.dark_gray_2);
        commsClicked = false;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag_sel, R.color.sharp_pink);
        schoolsClicked = true;

        setMenuButton(profileImage, profileText, R.drawable.mn_profile, R.color.dark_gray_2);
        profileClicked = false;

        setUnreadNotificationsCount();
    }

    public void pressProfileTab() {
        getActionBar().show();

        if (!profileClicked) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            selectedFragment = new ProfileMainFragment();
            selectedFragment.setTrackedOnce();
            fragmentTransaction.replace(R.id.placeHolder, selectedFragment).commit();
            notificationCount.setVisibility(View.INVISIBLE);
        }

        setMenuButton(homeImage, homeText, R.drawable.mn_home, R.color.dark_gray_2);
        homeClicked = false;

        setMenuButton(commsImage, commsText, R.drawable.mn_comm, R.color.dark_gray_2);
        commsClicked = false;

        setMenuButton(schoolsImage, schoolsText, R.drawable.mn_tag, R.color.dark_gray_2);
        schoolsClicked = false;

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
            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void init() {
        if (LocalCommunityTabCache.getMyCommunities() == null) {
            LocalCommunityTabCache.refreshMyCommunities();
        }

        if (LocalCommunityTabCache.isCommunityCategoryMapListEmpty()) {
            topicCommunityTabLoaded = false;
            yearCommunityTabLoaded = false;

            AppController.getApi().getTopicCommunityCategoriesMap(false, AppController.getInstance().getSessionId(),
                    new Callback<List<CommunityCategoryMapVM>>() {
                        @Override
                        public void success(List<CommunityCategoryMapVM> array, retrofit.client.Response response) {
                            Log.d("MainActivity", "api.getTopicCommunityCategoriesMap.success: CommunityCategoryMapVM list size - " + array.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.TOPIC_COMMUNITY, array);

                            topicCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                pressHomeTab();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(MainActivity.class.getSimpleName(), "init.api.getTopicCommunityCategoriesMap: failure", error);
                        }
                    });

            AppController.getApi().getZodiacYearCommunities(AppController.getInstance().getSessionId(),
                    new Callback<CommunitiesParentVM>() {
                        @Override
                        public void success(CommunitiesParentVM communitiesParent, retrofit.client.Response response) {
                            Log.d("MainActivity", "api.getZodiacYearCommunities.success: CommunitiesParentVM list size - " + communitiesParent.communities.size());

                            LocalCommunityTabCache.addToCommunityCategoryMapList(LocalCommunityTabCache.CommunityTabType.ZODIAC_YEAR_COMMUNITY, communitiesParent);

                            yearCommunityTabLoaded = true;
                            if (topicCommunityTabLoaded && yearCommunityTabLoaded) {
                                pressHomeTab();
                            }
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            Log.e(MainActivity.class.getSimpleName(), "init.api.getZodiacYearCommunities: failure", error);
                        }
                    });
        }
    }

    private void setUnreadNotificationsCount() {
        NotificationsParentVM notificationsParentVM = NotificationCache.getNotifications();
        if (notificationsParentVM == null) {
            return;
        }

        long count = notificationsParentVM.getRequestCounts() + notificationsParentVM.getNotifyCounts()+notificationsParentVM.getMessageCount();

        Log.d(this.getClass().getSimpleName(), "setUnreadNotificationsCount: requestCount="+notificationsParentVM.getRequestCounts()+" notifCount="+notificationsParentVM.getNotifyCounts());

        if(count == 0) {
            notificationCount.setVisibility(View.INVISIBLE);
        } else {
            notificationCount.setVisibility(View.VISIBLE);
            notificationCount.setText(count+"");
        }
    }
}

