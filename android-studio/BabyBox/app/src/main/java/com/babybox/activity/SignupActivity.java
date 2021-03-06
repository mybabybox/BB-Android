package com.babybox.activity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.DefaultValues;
import com.babybox.util.ValidationUtil;
import com.babybox.util.ViewUtil;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupActivity extends AbstractLoginActivity {

    private EditText lastName,firstName,email,password,repeatPassword;
    private Button signupButton;
    private PopupWindow signupSuccessPopup;
    private ImageView facebookButton, emailButton;
    private LinearLayout emailEditLayout;
    private TextView errorMessage;
    private CheckBox termsCheckbox, privacyCheckbox;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_activity);

        lastName = (EditText) findViewById(R.id.lastNameEditText);
        firstName = (EditText) findViewById(R.id.firstNameEditText);
        email = (EditText) findViewById(R.id.emailEditText);
        password = (EditText) findViewById(R.id.passwordEditText);
        repeatPassword = (EditText) findViewById(R.id.repeatPasswordEditText);
        signupButton = (Button) findViewById(R.id.signupButton);
        facebookButton = (ImageView) findViewById(R.id.facebookButton);
        emailButton = (ImageView) findViewById(R.id.emailButton);
        emailEditLayout = (LinearLayout) findViewById(R.id.emailEditLayout);
        errorMessage = (TextView) findViewById(R.id.errorMessage);
        termsCheckbox = (CheckBox) findViewById(R.id.termsCheckbox);
        privacyCheckbox = (CheckBox) findViewById(R.id.privacyCheckbox);

        setLoginButton(signupButton);
        setFacebookButton(facebookButton);

        emailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                emailEditLayout.setVisibility(View.VISIBLE);
                emailButton.setVisibility(View.GONE);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    signUp(lastName.getText().toString(), firstName.getText().toString(), email.getText().toString(), password.getText().toString(), repeatPassword.getText().toString());
                } else {
                    Toast.makeText(SignupActivity.this, getString(R.string.signup_error_please_check), Toast.LENGTH_LONG).show();
                }
            }
        });

        facebookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                facebookLogin();
            }
        });

        facebookButton.requestFocus();

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                //Validation.isEmailAddress(email, true);
            }
        });

        termsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent(SignupActivity.this, TermsActivity.class);
                    startActivity(intent);
                }
            }
        });

        privacyCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    Intent intent = new Intent(SignupActivity.this, PrivacyActivity.class);
                    startActivity(intent);
                }
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

    private void signUp(final String lname, final String fname, final String email, final String password, final String repeatPassword) {
        showErrorMessage(false);
        ViewUtil.hideInputMethodWindow(this, signupButton);

        showSpinner();
        AppController.getApiService().signUp(lname, fname, email, password, repeatPassword, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                stopSpinner();
                if (responseObject.getStatus() == DefaultValues.HTTP_STATUS_OK) {
                    if (DefaultValues.EMAIL_SIGNUP_VERIFICATION_REQUIRED) {
                        initSuccessPopup();
                    } else {
                        emailLogin(email, password);
                    }
                }
            }

            @Override
            public void failure(RetrofitError error) {
                stopSpinner();
                if (error.getResponse().getStatus() == DefaultValues.HTTP_STATUS_BAD_REQUEST) {
                    showErrorMessage(true);
                }
                Log.e(SignupActivity.class.getSimpleName(), "signUp: failure", error);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        if (signupSuccessPopup != null) {
            signupSuccessPopup.dismiss();
            signupSuccessPopup = null;
        }
    }

    private void showErrorMessage(boolean show) {
        if (show) {
            errorMessage.setVisibility(View.VISIBLE);
            errorMessage.requestFocus();
        } else {
            errorMessage.setVisibility(View.INVISIBLE);
        }
    }

    private void initSuccessPopup() {
        try {
            Dialog dialog = ViewUtil.alert(
                    this,
                    R.layout.signup_success_popup_window,
                    R.id.okButton,
                    new View.OnClickListener() {
                        public void onClick(View view) {
                            SignupActivity.this.onBackPressed();
                        }
                    });

            TextView emailText = (TextView) dialog.findViewById(R.id.emailText);
            emailText.setText(email.getText().toString());
        } catch (Exception e) {
            Log.e(SignupActivity.class.getSimpleName(), "initSuccessPopup: failure", e);
        }
    }

    private boolean isValid() {
        boolean valid = true;
        if (!ValidationUtil.hasText(lastName))
            valid = false;
        if (!ValidationUtil.hasText(firstName))
            valid = false;
        if (!ValidationUtil.hasText(email) || !ValidationUtil.isEmailValid(email))
            valid = false;
        if (!ValidationUtil.hasText(password))
            valid = false;
        if (!ValidationUtil.hasText(repeatPassword))
            valid = false;
        if (!isPasswordValid(password.getText().toString(),repeatPassword.getText().toString()))
            valid = false;
        return valid;
    }

    private boolean isPasswordValid(String password,String rePassword) {
        if (password.length() < DefaultValues.MIN_CHAR_SIGNUP_PASSWORD) {
            repeatPassword.setError(getString(R.string.signup_error_password_min_char));
            return false;
        } else if (StringUtils.isEmpty(password) || StringUtils.isEmpty(rePassword) || !password.equals(rePassword)) {
            repeatPassword.setError(getString(R.string.signup_error_password_not_identical));
            return false;
        }
        repeatPassword.setError(null);
        return true;
    }
}

