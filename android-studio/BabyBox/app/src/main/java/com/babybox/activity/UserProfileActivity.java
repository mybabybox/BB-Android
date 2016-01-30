package com.babybox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.UserProfileFeedViewFragment;
import com.babybox.util.ViewUtil;

public class UserProfileActivity extends TrackedFragmentActivity {

    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.user_profile_activity);

        backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        Bundle bundle = new Bundle();
        long userId = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L);
        if(userId == 0l && getIntent().getData() != null){
            userId = Long.parseLong(getIntent().getData().getLastPathSegment());
        }
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, userId);

        //set Fragmentclass Arguments
        //Fragmentclass fragobj=new Fragmentclass();

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        UserProfileFeedViewFragment profileFragment = new UserProfileFeedViewFragment();
        profileFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolder, profileFragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }
}


