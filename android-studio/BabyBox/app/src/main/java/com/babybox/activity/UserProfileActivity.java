package com.babybox.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.UserProfileFeedViewFragment;
import com.babybox.util.ViewUtil;

public class UserProfileActivity extends TrackedFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.user_profile_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        Bundle bundle = new Bundle();
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0l));

        //set Fragmentclass Arguments
        //Fragmentclass fragobj=new Fragmentclass();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFeedViewFragment profileFragment = new UserProfileFeedViewFragment();
        profileFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolder, profileFragment).commit();
    }
}


