package com.babybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.NewReportedPostVM;

import org.parceler.apache.commons.lang.StringUtils;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 08-01-2016.
 */
public class ReportActivity extends Activity {

    ImageView backImage;
    ImageView cat1Layout, cat2Layout, cat3Layout, cat4Layout, cat5Layout, cat6Layout;
    Long postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.report_acivity);
        backImage = (ImageView) findViewById(R.id.backImage);
        cat1Layout = (ImageView) findViewById(R.id.image_1);
        cat2Layout = (ImageView) findViewById(R.id.image_2);
        cat3Layout = (ImageView) findViewById(R.id.image_3);
        cat4Layout = (ImageView) findViewById(R.id.image_4);
        cat5Layout = (ImageView) findViewById(R.id.image_5);
        cat6Layout = (ImageView) findViewById(R.id.image_6);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postId = extras.getLong("post_id", 0L);
        }

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cat1Layout.setOnClickListener(reportListener);
        cat2Layout.setOnClickListener(reportListener);
        cat3Layout.setOnClickListener(reportListener);
        cat4Layout.setOnClickListener(reportListener);
        cat5Layout.setOnClickListener(reportListener);
        cat6Layout.setOnClickListener(reportListener);
    }

    private View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            System.out.println("on click listener");
            final EditText edittext = new EditText(ReportActivity.this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportActivity.this);

            if (v.getId() == R.id.image_6) {
                alertDialogBuilder.setMessage(getString(R.string.other_report_confirm));
                edittext.setLines(5);
                edittext.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                edittext.setHint(getString(R.string.other_report_editview_placeholder));
                alertDialogBuilder.setView(edittext);
            } else {
                alertDialogBuilder.setMessage(getString(R.string.report_confirm));
            }
            alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String body = edittext.getText().toString();
                    showSpinner();
                    reportPost(v, postId, body);
                }
            });
            alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            AlertDialog alertDialog = alertDialogBuilder.show();
        }
    };

    private void reportPost(View v, Long postId, String body) {
        NewReportedPostVM reportedPostVM = new NewReportedPostVM();
        switch (v.getId()) {
            case R.id.image_1:
                reportedPostVM.reportedType = ViewUtil.ReportedType.POST_WRONG_CATEGORY.name();
                break;

            case R.id.image_2:
                reportedPostVM.reportedType = ViewUtil.ReportedType.POST_COUNTERFEIT.name();
                break;

            case R.id.image_3:
                reportedPostVM.reportedType = ViewUtil.ReportedType.POST_REPEATED_LISTING.name();
                break;

            case R.id.image_4:
                reportedPostVM.reportedType = ViewUtil.ReportedType.POST_SPAM.name();
                break;

            case R.id.image_5:
                reportedPostVM.reportedType = ViewUtil.ReportedType.POST_PROHIBITED_ITEM.name();
                break;

            case R.id.image_6:
                reportedPostVM.reportedType = ViewUtil.ReportedType.OTHER.name();
                break;

        }
        reportedPostVM.body = body;
        reportedPostVM.postId = postId;


        AppController.getApiService().reportPost(
                reportedPostVM, new Callback<Response>() {
                    @Override
                    public void success(Response responseObject, Response response) {
                        Log.d(SignupDetailActivity.class.getSimpleName(), "api.reportPost success");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        String errorMsg = ViewUtil.getResponseBody(error.getResponse());
                        if (error.getResponse().getStatus() == 500 &&
                                error.getResponse() != null &&
                                !StringUtils.isEmpty(errorMsg)) {
                            ViewUtil.alert(ReportActivity.this, errorMsg);
                        }

                        stopSpinner();
                        Log.e(ReportActivity.class.getSimpleName(), "reportPost: failure", error);
                    }
                });
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
    }


}
