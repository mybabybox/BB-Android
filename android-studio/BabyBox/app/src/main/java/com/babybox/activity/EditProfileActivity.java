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
import android.widget.TextView;
import android.widget.Toast;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.DistrictCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.ValidationUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.UserProfileDataVM;
import com.babybox.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class EditProfileActivity extends TrackedFragmentActivity {

    private Spinner locationSpinner;
    private Button finishButton;
    private ImageView fbLoginIcon, mbLoginIcon;
    private EditText displayName,aboutmeEdit;
    private TextView displayEmailText;
    private EditText lastNameEdit,firstNameEdit;
    private ImageView backImage;

    private UserProfileDataVM profileDataVM;

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

        displayEmailText = (TextView) findViewById(R.id.displayEmailText);
        aboutmeEdit = (EditText) findViewById(R.id.aboutmeEdit);

        profileDataVM = new UserProfileDataVM();

        displayName = (EditText) findViewById(R.id.displaynameEdit);
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
                    profileDataVM.setParent_aboutme(aboutmeEdit.getText().toString());
                    profileDataVM.setParent_displayname(displayName.getText().toString());
                    profileDataVM.setParent_firstname(firstNameEdit.getText().toString());
                    profileDataVM.setParent_lastname(lastNameEdit.getText().toString());
                    profileDataVM.setParent_birth_year(String.valueOf(DefaultValues.DEFAULT_PARENT_BIRTH_YEAR));
                    profileDataVM.setParent_location(locationId);
                    updateUserProfileData(profileDataVM);
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
        displayEmailText.setText(user.getEmail());
        displayName.setText(user.getDisplayName());
        aboutmeEdit.setText(user.getAboutMe());
        firstNameEdit.setText(user.getFirstName());
        lastNameEdit.setText(user.getLastName());

        //(new ActivityUtil(this)).hideInputMethodWindow(this.finishButton);
    }

    private void updateUserProfileData(UserProfileDataVM userProfileDataVM){
        ViewUtil.showSpinner(this);
        AppController.getApiService().updateUserProfileData(userProfileDataVM, new Callback<UserVM>() {
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
                        Log.e(EditProfileActivity.class.getSimpleName(), "updateUserProfileData: failure", error);
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
                Log.e(EditProfileActivity.class.getSimpleName(), "setUserProfileData: failure", error);
            }
        });
    }

    private boolean isValid() {
        boolean valid = true;
        String error = "";
        if (!ValidationUtil.isUserDisplaynameValid(displayName)) {
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