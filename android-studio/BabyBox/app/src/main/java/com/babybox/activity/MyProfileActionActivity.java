package com.babybox.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragment;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.MessageListFragment;
import com.babybox.fragment.SettingsFragment;
import com.babybox.fragment.NotificationListFragment;
import com.babybox.fragment.RequestListFragment;
import com.babybox.util.ViewUtil;

public class MyProfileActionActivity extends TrackedFragmentActivity {

    private ImageView backImage;
    private TextView titleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);  // track in fragment

        setContentView(R.layout.child_layout_view);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.my_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        titleText = (TextView) findViewById(R.id.actionbarTitle);

        Bundle bundle = new Bundle();
        TrackedFragment fragment = null;
        switch (getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_ACTION_TYPE)) {
            case "requests":
                titleText.setText(getString(R.string.request_actionbar_title));
                bundle.putString(ViewUtil.BUNDLE_KEY_LISTS, getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_LISTS));
                fragment = new RequestListFragment();
                fragment.setArguments(bundle);
                break;
            case "notifications":
                titleText.setText(getString(R.string.notification_actionbar_title));
                bundle.putString(ViewUtil.BUNDLE_KEY_LISTS, getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_LISTS));
                fragment = new NotificationListFragment();
                fragment.setArguments(bundle);
                break;
            case "settings":
                titleText.setText(getString(R.string.settings_actionbar_title));
                fragment = new SettingsFragment();
                break;
            case "messages":
                titleText.setText(getString(R.string.pm_actionbar_title));
                fragment = new MessageListFragment();
                break;
            case "":
                titleText.setText(getString(R.string.orders));
                //fragment = new MyOrdersFragment();
                break;
        }

        if (fragment != null) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.childLayout, fragment).commit();
            fragment.setTrackedOnce();
        }

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}


