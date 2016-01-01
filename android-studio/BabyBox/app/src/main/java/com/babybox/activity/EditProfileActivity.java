package com.babybox.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.DistrictCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ValidationUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.EditUserInfoVM;
import com.babybox.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends TrackedFragmentActivity {

    private Spinner locationSpinner;
    private Button finishButton;
    private ImageView fbLoginIcon, mbLoginIcon;
    private EditText emailEdit, displayNameEdit, aboutMeEdit;
    private EditText lastNameEdit,firstNameEdit;
    private ImageView backImage;

    private boolean emailCanEdit = false;

    private EditUserInfoVM userInfoVM;

    private int locationId = 1;

    private List<String> districtNames;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.edit_profile_activity);

        setActionBarTitle(getString(R.string.edit_user_info));

        fbLoginIcon = (ImageView) findViewById(R.id.fbLoginIcon);
        mbLoginIcon = (ImageView) findViewById(R.id.mbLoginIcon);

        lastNameEdit = (EditText) findViewById(R.id.lastNameEditText);
        firstNameEdit = (EditText) findViewById(R.id.firstNameEditText);

        emailEdit = (EditText) findViewById(R.id.emailEdit);
        aboutMeEdit = (EditText) findViewById(R.id.aboutMeEdit);

        displayNameEdit = (EditText) findViewById(R.id.displayNameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        finishButton = (Button) findViewById(R.id.finishButton);

        initDistrictSpinner();

        locationSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                locationId = -1;
                String loc = locationSpinner.getSelectedItem().toString();
                List<LocationVM> districts = DistrictCache.getDistricts();
                for (LocationVM vm : districts) {
                    if (vm.getDisplayName().equals(loc)) {
                        locationId = vm.getId().intValue();
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        getUserInfo();

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isValid()) {
                    userInfoVM = new EditUserInfoVM();
                    userInfoVM.setEmail(emailEdit.getText().toString().trim());
                    userInfoVM.setDisplayName(displayNameEdit.getText().toString().trim());
                    userInfoVM.setFirstName(firstNameEdit.getText().toString().trim());
                    userInfoVM.setLastName(lastNameEdit.getText().toString().trim());
                    userInfoVM.setLocation(locationId);
                    userInfoVM.setAboutMe(aboutMeEdit.getText().toString().trim());
                    editUserInfo(userInfoVM);
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

    private void initDistrictSpinner(){
        List<LocationVM> districts = DistrictCache.getDistricts();
        districtNames = new ArrayList<>();
        districtNames.add(getString(R.string.signup_details_location));
        for (int i = 0; i < districts.size(); i++) {
            districtNames.add(districts.get(i).getDisplayName());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                districtNames);
        locationSpinner.setAdapter(adapter);

        // set previous value
        int pos = 0;
        if (AppController.getUserLocation() != null) {
            pos = adapter.getPosition(AppController.getUserLocation().getDisplayName());
        }
        locationSpinner.setSelection(pos);
    }

    private void getUserInfo() {
        UserVM user = UserInfoCache.getUser();
        fbLoginIcon.setVisibility(user.isFbLogin()? View.VISIBLE : View.GONE);
        mbLoginIcon.setVisibility(user.isFbLogin() ? View.GONE : View.VISIBLE);
        emailEdit.setText(user.getEmail());
        displayNameEdit.setText(user.getDisplayName());
        aboutMeEdit.setText(user.getAboutMe());
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());

        // Only enable email edit for FB signups with email hidden
        emailCanEdit = user.isFbLogin && !user.emailProvidedOnSignup;
        emailEdit.setEnabled(emailCanEdit);
        emailEdit.setTextColor(getResources().getColor(R.color.gray));

        //(new ActivityUtil(this)).hideInputMethodWindow(this.finishButton);
    }

    private void editUserInfo(EditUserInfoVM userInfoVM){
        ViewUtil.showSpinner(this);
        AppController.getApiService().editUserInfo(userInfoVM, new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                UserInfoCache.refresh(new Callback<UserVM>() {
                    @Override
                    public void success(UserVM userVM, Response response) {
                        // refresh parent activity
                        ViewUtil.setActivityResult(EditProfileActivity.this, true);
                        ViewUtil.stopSpinner(EditProfileActivity.this);
                        finish();
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Log.e(EditProfileActivity.class.getSimpleName(), "editUserInfo: failure", error);
                    }
                });
            }

            @Override
            public void failure(RetrofitError error) {
                String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                if (error.getResponse().getStatus() == 500 &&
                        error.getResponse() != null &&
                        !StringUtils.isEmpty(errorMsg)) {
                    ViewUtil.alert(EditProfileActivity.this, errorMsg);
                } else {
                    ViewUtil.alert(EditProfileActivity.this, getString(R.string.signup_details_error_info));
                }
                ViewUtil.stopSpinner(EditProfileActivity.this);
                Log.e(EditProfileActivity.class.getSimpleName(), "editUserInfo: failure", error);
            }
        });
    }

    private boolean isValid() {
        boolean valid = true;
        String error = "";
        if (emailCanEdit) {
            if (!ValidationUtil.isEmailValid(emailEdit)) {
                error = ValidationUtil.appendError(error, getString(R.string.signup_error_email_format));
                valid = false;
            }
        }
        if (!ValidationUtil.isUserDisplaynameValid(displayNameEdit)) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_displayname_format));
            valid = false;
        }
        if (!ValidationUtil.hasText(firstNameEdit) || !ValidationUtil.hasText(lastNameEdit)) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_name_not_entered));
            valid = false;
        }
        if (locationId == -1) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_location_not_entered));
            valid = false;
        }

        if (!valid)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        return valid;
    }
}