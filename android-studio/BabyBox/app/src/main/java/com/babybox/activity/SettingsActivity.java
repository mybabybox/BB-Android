package com.babybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;

import java.util.Locale;

public class SettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = SettingsActivity.class.getName();

    private TextView appVersionText;
    private RelativeLayout logout;
    private Spinner languageSpinner;
    private String[] languageNames;
    private String langString;

    private ImageView backImage;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private boolean refreshFlag = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        setActionBarTitle(getString(R.string.settings_actionbar_title));

        appVersionText = (TextView) findViewById(R.id.appVersionText);
        logout = (RelativeLayout) findViewById(R.id.logout);
        languageSpinner = (Spinner) findViewById(R.id.languageSpinner);

        languageNames = new String[]{"Chinese","English"};

        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(
                SettingsActivity.this,
                android.R.layout.simple_spinner_item,
                languageNames);

        languageSpinner.setAdapter(languageAdapter);

        appVersionText.setText("v"+ AppController.getVersionName());


        prefs = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        editor = prefs.edit();

        if(prefs.getBoolean("chinese", false)){
            setLocale("zh");
            int pos = languageAdapter.getPosition("Chinese");
            languageSpinner.setSelection(pos);
        }else{
            setLocale("en");
            int pos = languageAdapter.getPosition("English");
            languageSpinner.setSelection(pos);
        }

        languageSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                langString = languageAdapter.getItem(i);

                if(langString.equals("English")) {
                    setLocale("en");
                }else if(langString.equals("Chinese")){
                    setLocale("zh");
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // confirm exit
                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.logout_message)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppController.getInstance().logout();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
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


    public void setLocale(String lang) {

        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);


        SharedPreferences prefs = getSharedPreferences("prefs", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        if(lang.equals("zh")){
            editor.putBoolean("chinese", true);
        }else{
            editor.putBoolean("chinese", false);
        }
        editor.commit();

    }
}


