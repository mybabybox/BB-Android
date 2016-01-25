package com.babybox.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ViewUtil;

public class AdminActivity extends TrackedFragmentActivity {
    private static final String TAG = AdminActivity.class.getName();

    private LinearLayout newUsersLayout, latestLoginsLayout, latestConversationsLayout;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.admin_activity);

        setActionBarTitle(getString(R.string.admin));

        setTracked(false);

        newUsersLayout = (LinearLayout) findViewById(R.id.newUsersLayout);
        latestLoginsLayout = (LinearLayout) findViewById(R.id.latestLoginsLayout);
        latestConversationsLayout = (LinearLayout) findViewById(R.id.latestConversationsLayout);

        // new users
        newUsersLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminNewUsersActivity(AdminActivity.this);
            }
        });

        // latest logins
        latestLoginsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminLatestLoginsActivity(AdminActivity.this);
            }
        });

        // latest conversations
        latestConversationsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startAdminConversationListActivity(AdminActivity.this);
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}


