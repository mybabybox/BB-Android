package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.babybox.util.DefaultValues;
import com.babybox.util.SharedPreferencesUtil;

public class SettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = SettingsActivity.class.getName();

    private TextView appVersionText;
    private RelativeLayout logout;
    private Spinner langSpinner;
    private String[] langNames;

    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.settings_activity);

        setActionBarTitle(getString(R.string.settings_actionbar_title));

        appVersionText = (TextView) findViewById(R.id.appVersionText);
        logout = (RelativeLayout) findViewById(R.id.logout);
        langSpinner = (Spinner) findViewById(R.id.langSpinner);

        langNames = new String[] {
                getString(R.string.lang_zh),
                getString(R.string.lang_en)
        };

        final ArrayAdapter<String> languageAdapter = new ArrayAdapter<String>(
                SettingsActivity.this,
                R.layout.spinner_item_right,
                langNames);
        langSpinner.setAdapter(languageAdapter);

        appVersionText.setText("v"+ AppController.getVersionName());

        String lang = SharedPreferencesUtil.getInstance().getLang();
        int pos = -1;
        if (DefaultValues.LANG_EN.equalsIgnoreCase(lang)) {
            pos = languageAdapter.getPosition(getString(R.string.lang_en));
        } else {
            pos = languageAdapter.getPosition(getString(R.string.lang_zh));
        }
        langSpinner.setSelection(pos);

        langSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String lang = languageAdapter.getItem(i);
                if (getString(R.string.lang_en).equalsIgnoreCase(lang)) {
                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_EN);
                } else {
                    SharedPreferencesUtil.getInstance().saveLang(DefaultValues.LANG_ZH);
                }
                //ViewUtil.alert(SettingsActivity.this, getString(R.string.lang_complete));
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
}


