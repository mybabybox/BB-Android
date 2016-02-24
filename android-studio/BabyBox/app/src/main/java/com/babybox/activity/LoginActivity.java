package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.ValidationUtil;
import com.babybox.util.ViewUtil;

public class LoginActivity extends AbstractLoginActivity {

    private EditText email = null;
    private EditText password = null;
    private TextView loginButton;
    private ImageView facebookButton;
    private TextView signup;
    private TextView forgetPassword;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        ImageView loginUserImage = (ImageView) findViewById(R.id.loginUserImage);
        loginUserImage.bringToFront();
        ImageView loginLockImage = (ImageView) findViewById(R.id.loginLockImage);
        loginLockImage.bringToFront();

        email = (EditText) findViewById(R.id.email);
        password = (EditText) findViewById(R.id.password);
        facebookButton = (ImageView) findViewById(R.id.buttonFbLogin);
        loginButton = (TextView) findViewById(R.id.buttonLogin);
        signup = (TextView) findViewById(R.id.signupText);
        forgetPassword = (TextView) findViewById(R.id.forgetPasswordText);

        setLoginButton(loginButton);
        setFacebookButton(facebookButton);

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startSignupActivity(LoginActivity.this);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.startForgetPasswordActivity(LoginActivity.this);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (isValid()) {
                    emailLogin(email.getText().toString().trim(), password.getText().toString().trim());
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
            }
        });

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (isTaskRoot()) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.exit_app)
                    .setCancelable(false)
                    .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            AppController.getInstance().clearUserCaches();
                            LoginActivity.super.onBackPressed();
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        } else {
            super.onBackPressed();
        }
    }

    private boolean isValid() {
        boolean valid = true;
        if (!ValidationUtil.hasText(email) || !ValidationUtil.isEmailValid(email))
            valid = false;
        if (!ValidationUtil.hasText(password))
            valid = false;
        return valid;
    }
}