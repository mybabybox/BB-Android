package com.babybox.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ViewUtil;

public class WelcomeActivity extends TrackedFragmentActivity {
    private static final String TAG = WelcomeActivity.class.getName();

    private TextView loginText, signupText;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.welcome_activity);

        loginText = (TextView) findViewById(R.id.loginText);
        signupText = (TextView) findViewById(R.id.signupText);

        loginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startLoginActivity(WelcomeActivity.this);
            }
        });

        signupText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startSignupActivity(WelcomeActivity.this);
            }
        });
    }
}


