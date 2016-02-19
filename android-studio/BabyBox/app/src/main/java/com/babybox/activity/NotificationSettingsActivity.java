package com.babybox.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;

public class NotificationSettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = NotificationSettingsActivity.class.getName();

    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_settings_activity);

        setActionBarTitle(getString(R.string.settings_actionbar_title));

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}


