/**
 * Copyright 2010-present Facebook.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.babybox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.gcm.GCMClient;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.Arrays;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

// FB API v4.0

/**
 * For Hosting activity, set the no history property as false if have
 * android:noHistory="false"
 *
 * For Activity to be started, set the launch mode as standard if have
 * android:launchMode="standard"
 *
 * http://stackoverflow.com/a/14088099
 */
public abstract class AbstractLoginActivity extends TrackedFragmentActivity {

    protected static final String[] REQUEST_FACEBOOK_PERMISSIONS = {
            "public_profile", "email", "user_friends"
    };

    // FB API v4.0
    protected CallbackManager callbackManager;

    private View loginButton;
    private View facebookButton;

    protected void setLoginButton(View loginButton) {
        this.loginButton = loginButton;
    }

    protected void setFacebookButton(View facebookButton) {
        this.facebookButton = facebookButton;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // FB API v4.0
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onComplete: fb doLoginUsingAccessToken");
                fbLogin(loginResult.getAccessToken().getToken());
            }

            @Override
            public void onCancel() {
                stopSpinner();
                Log.d(AbstractLoginActivity.this.getClass().getSimpleName(), "loginToFacebook.onCancel: fb login cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                stopSpinner();
                ViewUtil.alert(AbstractLoginActivity.this,
                        getString(R.string.login_error_title),
                        getString(R.string.login_error_message)
                                + "\n" + e.getLocalizedMessage());
                e.printStackTrace();

                // FB API v4.0 - FB log out previous user to allow login again
                if (e instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        Log.e(AbstractLoginActivity.class.getSimpleName(), "FacebookCallback.onError: Log out existing valid access token");
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });
    }

    protected void emailLogin(String username, String password) {
        showSpinner();

        AppController.getApiService().login(username, password, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                stopSpinner();

                Log.d(this.getClass().getSimpleName(), "emailLogin: success");
                if (saveToSession(responseObject)) {
                    onSuccessLogin();
                } else {
                    ViewUtil.alert(AbstractLoginActivity.this,
                            getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                stopSpinner();
                if (error.getResponse() != null &&
                        error.getResponse().getStatus() == DefaultValues.HTTP_STATUS_BAD_REQUEST) {
                    String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                    if (!StringUtils.isEmpty(errorMsg)) {
                        ViewUtil.alert(AbstractLoginActivity.this,
                                getString(R.string.login_error_title),
                                errorMsg);
                    } else {
                        ViewUtil.alert(AbstractLoginActivity.this,
                                getString(R.string.login_error_title),
                                getString(R.string.login_id_error_message));
                    }
                } else {
                    ViewUtil.alert(AbstractLoginActivity.this,
                            getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                }

                Log.e(LoginActivity.class.getSimpleName(), "api.login: failure", error);
            }
        });
    }

    protected void facebookLogin() {
        //showSpinner();

        // FB API v4.0
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList(REQUEST_FACEBOOK_PERMISSIONS));
    }

    protected void fbLogin(String access_token) {
        //showSpinner();

        Log.d(this.getClass().getSimpleName(), "fbLogin: access_token - " + access_token);
        AppController.getApiService().loginByFacebook(access_token, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                stopSpinner();

                Log.d(this.getClass().getSimpleName(), "fbLogin: success");
                if (saveToSession(responseObject)) {
                    onSuccessLogin();
                } else {
                    ViewUtil.alert(AbstractLoginActivity.this,
                            getString(R.string.login_error_title),
                            getString(R.string.login_error_message));
                }
            }

            @Override
            public void failure(RetrofitError error) {
                stopSpinner();
                ViewUtil.alert(AbstractLoginActivity.this,
                        getString(R.string.login_error_title),
                        getString(R.string.login_error_message)
                                + "\n" + ViewUtil.getResponseBody(error.getResponse()));
                Log.e(AbstractLoginActivity.class.getSimpleName(), "fbLogin: failure", error);
            }
        });
    }

    protected void onSuccessLogin() {
        GCMClient.getInstance().registerGCM();
    }

    protected boolean saveToSession(Response response) {
        if (response == null) {
            return false;
        }

        String key = ViewUtil.getResponseBody(response);
        Log.d(this.getClass().getSimpleName(), "saveToSession: sessionID - " + key);
        AppController.getInstance().saveSessionId(key);

        ViewUtil.startSplashActivity(this, key);

        finish();

        return true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(this.getClass().getSimpleName(), "onActivityResult: callbackManager - requestCode:" + requestCode + " resultCode:" + resultCode + " data:" + data);

        try {
            // FB API v4.0
            callbackManager.onActivityResult(requestCode, resultCode, data);
        } catch (Exception e) {
            Log.d(this.getClass().getSimpleName(), "onActivityResult: callbackManager exception");
            e.printStackTrace();
        }
    }

    protected void showSpinner() {
        showSpinner(true);
    }

    protected void stopSpinner() {
        showSpinner(false);
    }

    private void showSpinner(boolean show) {
        if (show) {
            ViewUtil.showSpinner(this);
        } else {
            ViewUtil.stopSpinner(this);
        }

        if (loginButton != null) {
            loginButton.setEnabled(!show);
            loginButton.setAlpha(show ? 0.8F : 1.0F);
        }
        if (facebookButton != null) {
            facebookButton.setEnabled(!show);
            facebookButton.setAlpha(show ? 0.8F : 1.0F);
        }
    }
}

