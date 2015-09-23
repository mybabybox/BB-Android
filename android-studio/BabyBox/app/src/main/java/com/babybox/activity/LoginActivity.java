package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;

public class LoginActivity extends AbstractLoginActivity {

    private EditText username = null;
    private EditText password = null;
    private TextView loginButton;
    private ImageView facebookButton;
    private TextView signup;
    private TextView forgetPassword;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        ImageView loginUserImage = (ImageView) findViewById(R.id.loginUserImage);
        loginUserImage.bringToFront();
        ImageView loginLockImage = (ImageView) findViewById(R.id.loginLockImage);
        loginLockImage.bringToFront();

        username = (EditText) findViewById(R.id.userName);
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
                Intent intent = new Intent(LoginActivity.this, SignupActivity.class);
                startActivity(intent);
            }
        });

        forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
                startActivity(intent);
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                emailLogin(username.getText().toString(), password.getText().toString());
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                facebookLogin();
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
                            AppController.getInstance().clearAll();
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
}