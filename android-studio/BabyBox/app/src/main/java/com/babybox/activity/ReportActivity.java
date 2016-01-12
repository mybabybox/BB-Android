package com.babybox.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.NewReportedPostVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by User on 08-01-2016.
 */
public class ReportActivity extends TrackedFragmentActivity {
    private static final String TAG = ReportActivity.class.getName();

    private ImageView backImage;
    private ImageView image1, image2, image3, image4, image5, image6;
    private Long postId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.report_activity);

        backImage = (ImageView) findViewById(R.id.backImage);
        image1 = (ImageView) findViewById(R.id.image1);
        image2 = (ImageView) findViewById(R.id.image2);
        image3 = (ImageView) findViewById(R.id.image3);
        image4 = (ImageView) findViewById(R.id.image4);
        image5 = (ImageView) findViewById(R.id.image5);
        image6 = (ImageView) findViewById(R.id.image6);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            postId = extras.getLong(ViewUtil.BUNDLE_KEY_ID, 0L);
        }

        setActionBarTitle(getString(R.string.report_post));

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        image1.setOnClickListener(reportListener);
        image2.setOnClickListener(reportListener);
        image3.setOnClickListener(reportListener);
        image4.setOnClickListener(reportListener);
        image5.setOnClickListener(reportListener);
        image6.setOnClickListener(reportListener);
    }

    private View.OnClickListener reportListener = new View.OnClickListener() {
        @Override
        public void onClick(final View v) {
            final EditText editText = new EditText(ReportActivity.this);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ReportActivity.this);

            if (v.getId() == R.id.image6) {
                alertDialogBuilder.setMessage(getString(R.string.report_post_other_title));
                //editText.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, 100));
                editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                editText.setSingleLine(false);
                editText.setLines(2);
                editText.setMinLines(2);
                editText.setMaxLines(5);
                editText.setTextSize(15);
                editText.setHint(getString(R.string.report_post_other_hint));
                alertDialogBuilder.setView(editText);
            } else {
                alertDialogBuilder.setMessage(getReportMessage(v));
            }

            alertDialogBuilder.setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String body = editText.getText().toString();
                    if (body.trim().isEmpty()) {
                        Toast.makeText(ReportActivity.this, ReportActivity.this.getString(R.string.invalid_post_desc_empty), Toast.LENGTH_SHORT).show();
                        return;
                    }
                    showSpinner();
                    reportPost(v, postId, body);
                }
            });

            alertDialogBuilder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            });

            alertDialogBuilder.show();
        }
    };

    private void reportPost(View view, Long postId, String body) {
        NewReportedPostVM reportedPost = null;
        switch (view.getId()) {
            case R.id.image1:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.POST_WRONG_CATEGORY);
                break;
            case R.id.image2:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.POST_WRONG_CATEGORY);
                break;
            case R.id.image3:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.POST_REPEATED_LISTING);
                break;
            case R.id.image4:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.POST_SPAM);
                break;
            case R.id.image5:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.POST_PROHIBITED_ITEM);
                break;
            case R.id.image6:
                reportedPost = new NewReportedPostVM(
                        postId,
                        UserInfoCache.getUser().id,
                        body,
                        ViewUtil.ReportedType.OTHER);
                break;
        }

        showSpinner();
        AppController.getApiService().reportPost(
                reportedPost, new Callback<Response>() {
                    @Override
                    public void success(Response responseObject, Response response) {
                        finish();
                        Toast.makeText(ReportActivity.this, ReportActivity.this.getString(R.string.report_post_success), Toast.LENGTH_SHORT).show();
                        stopSpinner();
                        Log.d(TAG, "api.reportPost success");
                    }

                    @Override
                    public void failure(RetrofitError error) {
                        Toast.makeText(ReportActivity.this, ReportActivity.this.getString(R.string.report_post_failed), Toast.LENGTH_SHORT).show();
                        stopSpinner();
                        Log.e(TAG, "reportPost: failure", error);
                    }
                });
    }

    private String getReportMessage(View view) {
        String message = getString(R.string.report_post_confirm);
        switch (view.getId()) {
            case R.id.image1:
                return message + "\n" + getString(R.string.report_wrong_cat);
            case R.id.image2:
                return message + "\n" + getString(R.string.report_counterfeit);
            case R.id.image3:
                return message + "\n" + getString(R.string.report_repeat);
            case R.id.image4:
                return message + "\n" + getString(R.string.report_spam);
            case R.id.image5:
                return message + "\n" + getString(R.string.report_prohibited);
            case R.id.image6:
                return message + "\n" + getString(R.string.report_other);
        }
        return message;
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
