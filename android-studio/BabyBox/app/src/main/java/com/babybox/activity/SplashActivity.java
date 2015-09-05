package com.babybox.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.NotificationCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class SplashActivity extends TrackedFragmentActivity {

    private boolean fromLoginActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);

        setContentView(R.layout.splash_activity);
    }

    @Override
    public void onStart() {
        super.onStart();

        String sessionId = AppController.getInstance().getSessionId();

        Intent intent = getIntent();
        if (intent != null &&
                intent.getStringExtra(ViewUtil.BUNDLE_KEY_SOURCE) != null &&
                intent.getStringExtra(ViewUtil.BUNDLE_KEY_SOURCE).equals("FromLoginActivity")) {
            fromLoginActivity = true;
            sessionId = intent.getStringExtra(ViewUtil.BUNDLE_KEY_LOGIN_KEY);
        }

        if (sessionId == null) {
            LoginActivity.startLoginActivity(SplashActivity.this);
        } else {
            Log.d(this.getClass().getSimpleName(), "onStart: sessionID - " + sessionId);
            startMainActivity(sessionId);
        }
    }

    private void startMainActivity(final String sessionId) {
        Log.d(this.getClass().getSimpleName(), "startMainActivity: UserInfoCache.refresh");

        UserInfoCache.refresh(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM user, retrofit.client.Response response) {
                Log.d(SplashActivity.this.getClass().getSimpleName(), "startMainActivity: getUserInfo.success: user="+user.getDisplayName()+" id="+user.getId()+" newUser="+user.newUser);

                // clear session id, redirect to login page
                if (user.getId() == -1) {
                    Toast.makeText(SplashActivity.this, "Cannot find user. Please login again.", Toast.LENGTH_LONG).show();
                    AppController.getInstance().clearPreferences();
                    LoginActivity.startLoginActivity(SplashActivity.this);
                }

                // new user flow
                if(user.isNewUser() || StringUtils.isEmpty(user.getDisplayName())) {
                    if (!user.isEmailValidated()) {
                        Toast.makeText(SplashActivity.this, SplashActivity.this.getString(R.string.signup_error_email_unverified)+user.email, Toast.LENGTH_LONG).show();
                        AppController.getInstance().clearPreferences();
                        LoginActivity.startLoginActivity(SplashActivity.this);
                    } else {
                        Intent intent = new Intent(SplashActivity.this, SignupDetailActivity.class);
                        intent.putExtra(ViewUtil.BUNDLE_KEY_ARG1, user.firstName);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    // save to preferences
                    if (AppController.getInstance().getSessionId() == null) {
                        AppController.getInstance().saveSessionId(sessionId);
                    }

                    //AppController.initCaches();
                    NotificationCache.refresh();

                    // display splash
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            startActivity(new Intent(SplashActivity.this, MainActivity.class));
                            finish();
                        }
                    }, DefaultValues.SPLASH_DISPLAY_MILLIS);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                AppController.getInstance().clearPreferences();

                if (RetrofitError.Kind.NETWORK.equals(error.getKind().name()) ||
                        RetrofitError.Kind.HTTP.equals(error.getKind().name())) {
                    showNetworkProblemAlert();
                } else {
                    ViewUtil.alert(SplashActivity.this,
                            getString(R.string.login_error_title),
                            getString(R.string.login_error_message),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    LoginActivity.startLoginActivity(SplashActivity.this);
                                }
                            });
                }

                /*
                if (!isOnline()) {
                    SplashActivity.this.startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
                }
                */
            }
        }, null);
    }

    private void showNetworkProblemAlert() {
        ViewUtil.alert(SplashActivity.this,
                getString(R.string.connection_timeout_title),
                getString(R.string.connection_timeout_message));
    }

    private boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(getApplicationContext(), getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }
}
