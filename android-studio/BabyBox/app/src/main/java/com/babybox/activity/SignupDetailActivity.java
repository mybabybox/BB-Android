package com.babybox.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.DistrictCache;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ValidationUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.UserVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SignupDetailActivity extends TrackedFragmentActivity {
    private String[] babyNumberArray;
    private Spinner locationSpinner, babySpinner;
    private Button finishButton;
    private EditText displayName;
    private RadioGroup parentType, babyGender1, babyGender2, babyGender3;
    private RadioButton parent, baby1, baby2, baby3;
    private LinearLayout babynumberLayout;
    private LinearLayout babyDetailsLayout1, babyDetailsLayout2, babyDetailsLayout3;
    private TextView titleText;

    private List<String> districtNames;

    String parenttype = "",babynum = "",babygen1 = "", babygen2 = "", babygen3 = "";

    private int locationId = -1;
    private Calendar calendar;
    private ImageView birthday1,birthday2,birthday3;
    private TextView birthdayLabel1,birthdayLabel2,birthdayLabel3;

    /*
    private String year1,month1,day1,year2,month2,day2,year3,month3,day3;

    private boolean birthdayClick1 = false, birthdayClick2 = false, birthdayClick3 = false,parentClicked=false,babySelected=false;

    private DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setBirthdays();
        }
    };
    */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.signup_detail_activity);

        titleText = (TextView) findViewById(R.id.titleText);

        parentType = (RadioGroup) findViewById(R.id.parentRadio);

        babynumberLayout = (LinearLayout) findViewById(R.id.babyNumberLayout);
        babynumberLayout.setVisibility(View.GONE);

        babyDetailsLayout1 = (LinearLayout) findViewById(R.id.babyDetailsLayout1);
        babyDetailsLayout1.setVisibility(View.GONE);
        babyDetailsLayout2 = (LinearLayout) findViewById(R.id.babyDetailsLayout2);
        babyDetailsLayout2.setVisibility(View.GONE);
        babyDetailsLayout3 = (LinearLayout) findViewById(R.id.babyDetailsLayout3);
        babyDetailsLayout3.setVisibility(View.GONE);

        babyGender1 = (RadioGroup) findViewById(R.id.babyRadio1);
        babyGender2 = (RadioGroup) findViewById(R.id.babyRadio2);
        babyGender3 = (RadioGroup) findViewById(R.id.babyRadio3);

        birthday1 = (ImageView) findViewById(R.id.birthday1);
        birthday2 = (ImageView) findViewById(R.id.birthday2);
        birthday3 = (ImageView) findViewById(R.id.birthday3);

        birthdayLabel1 = (TextView) findViewById(R.id.birthdayLabel1);
        birthdayLabel2 = (TextView) findViewById(R.id.birthdayLabel2);
        birthdayLabel3 = (TextView) findViewById(R.id.birthdayLabel3);

        calendar = Calendar.getInstance();

        displayName = (EditText) findViewById(R.id.displaynameEdit);
        locationSpinner = (Spinner) findViewById(R.id.locationSpinner);

        finishButton = (Button) findViewById(R.id.finishButton);

        setDistricts();

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

        /*
        babyNumberArray = new String[]{"1","2","3"};
        ArrayAdapter<String> babyAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item,babyNumberArray);

        babySpinner = (Spinner) findViewById(R.id.babySpinner);
        babySpinner.setAdapter(babyAdapter);

        //titleText.setText(getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_NAME) + " " + getString(R.string.signup_details_greeting));

        parentType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = parentType.getCheckedRadioButtonId();
                parent = (RadioButton) findViewById(id);
                parentClicked=true;
                setMoreDetailsVisible(id);
            }
        });

        babyGender1.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender1.getCheckedRadioButtonId();
                babySelected=true;
                baby1 = (RadioButton) findViewById(id);
            }
        });

        babyGender2.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender2.getCheckedRadioButtonId();
                baby2 = (RadioButton) findViewById(id);
            }
        });

        babyGender3.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                int id = babyGender1.getCheckedRadioButtonId();
                baby3 = (RadioButton) findViewById(id);
            }
        });

        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = true;
                birthdayClick2 = false;
                birthdayClick3 = false;
                showDatePicker();
            }
        };
        birthday1.setOnClickListener(onClickListener);
        birthdayLabel1.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = false;
                birthdayClick2 = true;
                birthdayClick3 = false;
                showDatePicker();
            }
        };
        birthday2.setOnClickListener(onClickListener);
        birthdayLabel2.setOnClickListener(onClickListener);

        onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                birthdayClick1 = false;
                birthdayClick2 = false;
                birthdayClick3 = true;
                showDatePicker();
            }
        };
        birthday3.setOnClickListener(onClickListener);
        birthdayLabel3.setOnClickListener(onClickListener);
        */

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                submitDetails();
            };
        });
    }

    private void submitDetails() {
        final String displayname = displayName.getText().toString().trim();

        /*
        if (parentClicked) {
            parenttype = getParentType(parent);

            if (parenttype.equals("NA")) {
                babynum = "0";
            } else {
                babynum = babySpinner.getSelectedItem().toString();
                if (babynum.equals("1")) {
                    babygen1 = getGender(baby1);
                } else if (babynum.equals("2")) {
                    babygen1 = getGender(baby1);
                    babygen2 = getGender(baby2);
                } else if (babynum.equals("3")) {
                    babygen1 = getGender(baby1);
                    babygen2 = getGender(baby2);
                    babygen3 = getGender(baby3);
                }
            }
        }
        */

        if (isValid()) {
            Log.d(this.getClass().getSimpleName(), "signupInfo: \n displayname="+displayname+"\n locationId="+locationId);

            showSpinner();
            AppController.getApiService().signUpInfo(
                    displayname, locationId, new Callback<Response>() {
                        @Override
                        public void success(Response responseObject, Response response) {
                            Log.d(SignupDetailActivity.class.getSimpleName(), "submitDetails: api.signUpInfo.success");
                            initNewUser();
                        }

                        @Override
                        public void failure(RetrofitError error) {
                            String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                            if (error.getResponse().getStatus() == 500 &&
                                    error.getResponse() != null &&
                                    !StringUtils.isEmpty(errorMsg)) {
                                ViewUtil.alert(SignupDetailActivity.this, errorMsg);
                            } else {
                                //ActivityUtil.alert(SignupDetailActivity.this, getString(R.string.signup_details_error_info));
                                ViewUtil.alert(SignupDetailActivity.this,
                                        "\""+displayname+"\" "+getString(R.string.signup_details_error_displayname_already_exists));
                            }

                            stopSpinner();
                            Log.e(SignupDetailActivity.class.getSimpleName(), "submitDetails.api.signUpInfo: failure", error);
                        }
                    });
        }
    }

    private void initNewUser() {
        Log.d(this.getClass().getSimpleName(), "initNewUser");
        AppController.getApiService().initNewUser(new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, Response response) {
                Log.d(SignupDetailActivity.class.getSimpleName(), "initNewUser.success");
                startActivity(new Intent(SignupDetailActivity.this, SplashActivity.class));
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                stopSpinner();
                Log.e(SignupDetailActivity.class.getSimpleName(), "initNewUser: failure", error);
            }
        });
    }

    private void setDistricts(){
        List<LocationVM> districts = DistrictCache.getDistricts();
        districtNames = new ArrayList<>();
        districtNames.add(getString(R.string.signup_details_location));
        for (int i = 0; i < districts.size(); i++) {
            districtNames.add(districts.get(i).getDisplayName());
        }

        ArrayAdapter<String> locationAdapter = new ArrayAdapter<>(
                SignupDetailActivity.this,
                android.R.layout.simple_spinner_item,
                districtNames);
        locationSpinner.setAdapter(locationAdapter);
    }

    /*
    private String getParentType(RadioButton radioButton) {
        if (radioButton == null)
            return "";

        if (radioButton.getText().toString().equals(getString(R.string.signup_details_mom))) {
            return "MOM";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_dad))) {
            return "DAD";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_soon_mom))) {
            return "SOON_MOM";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_soon_dad))) {
            return "SOON_DAD";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_not_parent))) {
            return "NA";
        }
        return "";
    }

    private String getGender(RadioButton radioButton) {
        if (radioButton == null)
            return "";

        if (radioButton.getText().toString().equals(getString(R.string.signup_details_boy))) {
            return "MALE";
        } else if (radioButton.getText().toString().equals(getString(R.string.signup_details_girl))) {
            return "FEMALE";
        }
        return "";
    }

    private void setMoreDetailsVisible(int id) {
        RadioButton type = (RadioButton) parentType.findViewById(id);
        if (!type.getText().toString().equals(getString(R.string.signup_details_not_parent))) {
            babynumberLayout.setVisibility(View.VISIBLE);
            setBabyDetailsVisible();
        } else {
            babynumberLayout.setVisibility(View.GONE);
            babyDetailsLayout1.setVisibility(View.GONE);
            babyDetailsLayout2.setVisibility(View.GONE);
            babyDetailsLayout3.setVisibility(View.GONE);
        }
    }

    private void setBabyDetailsVisible() {
        if (babySpinner.getSelectedItem() == "1") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.GONE);
            babyDetailsLayout3.setVisibility(View.GONE);
        } else if (babySpinner.getSelectedItem() == "2") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.GONE);
        } else if (babySpinner.getSelectedItem() == "3") {
            babyDetailsLayout1.setVisibility(View.VISIBLE);
            babyDetailsLayout2.setVisibility(View.VISIBLE);
            babyDetailsLayout3.setVisibility(View.VISIBLE);
        }

        babySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (babySpinner.getSelectedItem() == "1") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.GONE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                } else if (babySpinner.getSelectedItem() == "2") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.VISIBLE);
                    babyDetailsLayout3.setVisibility(View.GONE);
                } else if (babySpinner.getSelectedItem() == "3") {
                    babyDetailsLayout1.setVisibility(View.VISIBLE);
                    babyDetailsLayout2.setVisibility(View.VISIBLE);
                    babyDetailsLayout3.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void setBirthdays() {
        if (birthdayClick1) {
            birthdayLabel1.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year1 = calendar.get(Calendar.YEAR) + "";
            month1 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day1 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        } else if (birthdayClick2) {
            birthdayLabel2.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year2 = calendar.get(Calendar.YEAR) + "";
            month2 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day2 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        } else if (birthdayClick3) {
            birthdayLabel3.setText(calendar.get(Calendar.YEAR) + "-" + getMonth(calendar.get(Calendar.MONTH)) + "-" + calendar.get(Calendar.DAY_OF_MONTH));
            year3 = calendar.get(Calendar.YEAR) + "";
            month3 = getMonth(calendar.get(Calendar.MONTH)) + "";
            day3 = calendar.get(Calendar.DAY_OF_MONTH) + "";
        }
    }

    private void showDatePicker() {
        new DatePickerDialog(SignupDetailActivity.this, datePicker, calendar.get(calendar.YEAR), calendar.get(calendar.MONTH), calendar.get(calendar.DAY_OF_MONTH)).show();
    }

    private int getMonth(int month) {
        return month + 1;
    }
    */

    private boolean isValid(){
        boolean valid = true;
        String error = "";
        if (!ValidationUtil.isUserDisplaynameValid(displayName)) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_displayname_format));
            valid = false;
        }

        if (locationId == -1) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_location_not_entered));
            valid = false;
        }

        /*
        if (!parentClicked) {
            error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_status_not_entered));
            valid = false;
        }
        if (!parenttype.equals("NA")) {
            int num = StringUtils.isEmpty(babynum)? 0 : Integer.parseInt(babynum);
            if (num >= 1) {
                if (StringUtils.isEmpty(day1) || StringUtils.isEmpty(babygen1)) {
                    error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
            if (num >= 2) {
                if (StringUtils.isEmpty(day2) || StringUtils.isEmpty(babygen2)) {
                    error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
            if (num >= 3) {
                if (StringUtils.isEmpty(day3) || StringUtils.isEmpty(babygen3)) {
                    error = ValidationUtil.appendError(error, getString(R.string.signup_details_error_baby_not_entered));
                    valid = false;
                }
            }
        }
        */

        if (!valid)
            Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        return valid;
    }

    private void showSpinner() {
        showSpinner(true);
    }

    private void stopSpinner() {
        showSpinner(false);
    }

    private void showSpinner(boolean show) {
        if (show) {
            ViewUtil.showSpinner(this);
        } else {
            ViewUtil.stopSpinner(this);
        }

        if (finishButton != null) {
            finishButton.setEnabled(!show);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        ViewUtil.startLoginActivity(this);
    }
}