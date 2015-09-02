package com.babybox.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.NewsfeedListFragment;
import com.babybox.util.ViewUtil;

public class NewsfeedActivity extends TrackedFragmentActivity {

    private TextView titleText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.newsfeed_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.my_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        titleText = (TextView) findViewById(R.id.actionbarTitle);

        ImageView backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        Long id = getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L);
        String key = getIntent().getStringExtra("key");

        Log.d(this.getClass().getSimpleName(), "onCreate: Id=" + id + " key=" + key);
        switch (getIntent().getStringExtra("key")) {
            case "question":
            case "userquestion":
                titleText.setText(getString(R.string.questions_title));
                break;
            case "answer":
            case "useranswer":
                titleText.setText(getString(R.string.answers_title));
                break;
            case "bookmark":
                titleText.setText(getString(R.string.bookmarks_title));
                break;
        }

        Bundle bundle = new Bundle();
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, id);
        bundle.putString("key", key);

        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        NewsfeedListFragment newsfeedFragment = new NewsfeedListFragment();
        newsfeedFragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.placeHolders, newsfeedFragment).commit();
    }
}


