package com.babybox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.CategoryFeedViewFragment;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;

public class CategoryActivity extends TrackedFragmentActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.category_activity);

        // feed filter keys
        Bundle bundle = new Bundle();
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, DefaultValues.DEFAULT_CATEGORY_FEED_TYPE.name());
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_PRODUCT_TYPE, DefaultValues.DEFAULT_FEED_PRODUCT_TYPE.name());
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));

        CategoryFeedViewFragment fragment = new CategoryFeedViewFragment();
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.childLayout, fragment).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ViewUtil.START_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            boolean refresh = data.getBooleanExtra(ViewUtil.INTENT_RESULT_REFRESH, false);
            if (refresh) {
                // refresh parent activity
                ViewUtil.setActivityResult(this, true);
                finish();
            }
        }

        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            if (fragment != null)
                fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onBackPressed() {
            super.onBackPressed();
    }
}
