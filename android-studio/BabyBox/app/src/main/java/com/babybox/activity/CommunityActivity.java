package com.babybox.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.CommunityFragment;
import com.babybox.util.ViewUtil;

public class CommunityActivity extends TrackedFragmentActivity {
    private ImageView backImage, newPostAction;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.child_layout_view);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.community_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        backImage = (ImageView) this.findViewById(R.id.backImage);
        newPostAction = (ImageView) this.findViewById(R.id.newPostIcon);

        Bundle bundle = new Bundle();
        if (getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_SOURCE) != null) {
            bundle.putString(ViewUtil.BUNDLE_KEY_SOURCE, (getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_SOURCE)));
        }

        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));
        CommunityFragment fragment = new CommunityFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.childLayout, fragment).commit();

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                //finish();
                /*Intent intent = new Intent(CommunityActivity.this, ActivityMain.class);
                startActivity(intent);*/
            }
        });

        newPostAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CommunityActivity.this,NewPostActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));
                intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromCommActivity");
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
