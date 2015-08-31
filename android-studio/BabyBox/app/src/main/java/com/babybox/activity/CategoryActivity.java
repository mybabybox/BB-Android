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
import com.babybox.fragment.CategoryFeedViewFragment;
import com.babybox.fragment.CommunityFragment;

public class CategoryActivity extends TrackedFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.category_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        Bundle bundle = new Bundle();
        if (getIntent().getStringExtra("flag") != null) {
            bundle.putString("flag", (getIntent().getStringExtra("flag")));
        }

        bundle.putLong("id", getIntent().getLongExtra("id", 0L));
        bundle.putString("key", "category_popular");
        CategoryFeedViewFragment fragment = new CategoryFeedViewFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_layout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
