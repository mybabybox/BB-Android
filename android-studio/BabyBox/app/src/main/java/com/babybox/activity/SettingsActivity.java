package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;

import java.util.Set;

public class SettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = SettingsActivity.class.getName();

    private TextView appVersionText;
    private RelativeLayout logoutLayout, adminLayout;
    private Spinner langSpinner;
    private ImageView backImage;

    private String[] langNames;
    private boolean userSelect = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        setActionBarTitle(getString(R.string.settings_actionbar_title));

        appVersionText = (TextView) findViewById(R.id.appVersionText);
        langSpinner = (Spinner) findViewById(R.id.langSpinner);
        logoutLayout = (RelativeLayout) findViewById(R.id.logoutLayout);
        adminLayout = (RelativeLayout) findViewById(R.id.adminLayout);

        // version
        appVersionText.setText("v"+ AppController.getVersionName());

        // lang
        langNames = new String[] {
                getString(R.string.lang_zh),
                getString(R.string.lang_en)
        };

        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                SettingsActivity.this,
                R.layout.spinner_item_right,
                langNames);
        langSpinner.setAdapter(languageAdapter);

        String lang = SharedPreferencesUtil.getInstance().getLang();
        int pos = -1;
        if (DefaultValues.LANG_EN.equalsIgnoreCase(lang)) {
            pos = languageAdapter.getPosition(getString(R.string.lang_en));
        } else {
            pos = languageAdapter.getPosition(getString(R.string.lang_zh));
        }
        langSpinner.setSelection(pos);

        langSpinner.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                userSelect = true;
                return false;
            }
        });

        final int selected = pos;
        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (!userSelect) {
                    return;
                }

                userSelect = false;

                final String lang = languageAdapter.getItem(i);

                AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
                builder.setMessage(R.string.lang_confirm)
                        .setCancelable(false)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                if (getString(R.string.lang_en).equalsIgnoreCase(lang)) {
                                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_EN);
                                } else {
                                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_ZH);
                                }
                                ViewUtil.startSplashActivity(SettingsActivity.this, "");
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                langSpinner.setSelection(selected);
                                dialog.cancel();
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();

                //ViewUtil.alert(SettingsActivity.this, getString(R.string.lang_complete));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        // logout
        logoutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

        // admin
        adminLayout.setVisibility(AppController.isUserAdmin()? View.VISIBLE : View.GONE);
        if (AppController.isUserAdmin()) {
            adminLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ViewUtil.startAdminActivity(SettingsActivity.this);
                }
            });
        }

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }
}


