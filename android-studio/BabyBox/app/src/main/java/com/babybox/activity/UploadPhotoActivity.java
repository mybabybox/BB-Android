package com.babybox.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;

import org.appsroid.fxpro.library.Constants;
import org.appsroid.fxpro.library.Toaster;
import org.appsroid.fxpro.library.UriToUrl;

import java.lang.reflect.Method;

public class UploadPhotoActivity extends TrackedFragmentActivity {

    private Button bottom_holder;
    private static UploadPhotoActivity mInstance;
    private boolean click_status = true;
    private Animation animation;
    private Uri imageUri;

    private RelativeLayout top_holder;
  //  private RelativeLayout bottom_holder;
    private RelativeLayout step_number;

    public static synchronized UploadPhotoActivity getInstance() {
        return mInstance;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTracked(false);

        setContentView(R.layout.activity_upload);
        bottom_holder = (Button) findViewById(R.id.choose_photo);

        mInstance = this;

        bottom_holder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startGallery(view);
            }
        });

        //getActionBar().show();



    }

    @Override
    public void onStart() {
        super.onStart();
        overridePendingTransition(0, 0);
        flyIn();


    }

    @Override
    public void onBackPressed() {
        System.out.println("In UploadActivity back..");
        super.onBackPressed();
        overridePendingTransition(0, 0);
        flyIn();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        Log.d(this.getClass().getSimpleName(), "onDestroy: clear all");
    }

    public void startGallery(View view) {
        System.out.println("clicked::::");
        flyOut("displayGallery");
    }

    @SuppressWarnings("unused")
    private void displayGallery() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) && !Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent();
            intent.setType("image/jpeg");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent, Constants.REQUEST_GALLERY);
        } else {
            Toaster.make(getApplicationContext(), R.string.no_media);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == Constants.REQUEST_CAMERA) {
            try{
                if (resultCode == RESULT_OK) {
                    displayPhotoActivity(1);
                } else {
                    UriToUrl.deleteUri(getApplicationContext(), imageUri);
                }
            } catch (Exception e) {
                Toaster.make(getApplicationContext(), R.string.error_img_not_found);
            }
        } else if (resultCode == RESULT_OK && requestCode == Constants.REQUEST_GALLERY) {
            try {
                imageUri = data.getData();
                displayPhotoActivity(2);
            } catch (Exception e) {
                Toaster.make(getApplicationContext(), R.string.error_img_not_found);
            }
        }
    }

    private void displayPhotoActivity(int source_id) {
        Intent intent = new Intent(getApplicationContext(), SelectImageActivity.class);
        intent.putExtra(Constants.EXTRA_KEY_IMAGE_SOURCE, source_id);
        intent.setData(imageUri);
        startActivity(intent);
        overridePendingTransition(0, 0);
        finish();
    }

    private void flyOut(final String method_name) {
        if (click_status) {
            click_status = false;

            /*animation = AnimationUtils.loadAnimation(this, R.anim.step_number_back);
            step_number.startAnimation(animation);

            animation = AnimationUtils.loadAnimation(this, R.anim.holder_top_back);
            top_holder.startAnimation(animation);*/

            animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom_back);
            bottom_holder.startAnimation(animation);

            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation arg0) {
                }

                @Override
                public void onAnimationRepeat(Animation arg0) {
                }

                @Override
                public void onAnimationEnd(Animation arg0) {
                    callMethod(method_name);
                }
            });
        }
    }

    private void callMethod(String method_name) {
        if (method_name.equals("finish")) {
            overridePendingTransition(0, 0);
            finish();
        } else {
            try {
                Method method = getClass().getDeclaredMethod(method_name);
                method.invoke(this, new Object[] {});
            } catch (Exception e) {}
        }
    }

    private void flyIn() {
        click_status = true;

       /* animation = AnimationUtils.loadAnimation(this, R.anim.holder_top);
        top_holder.startAnimation(animation);*/

        animation = AnimationUtils.loadAnimation(this, R.anim.holder_bottom);
        bottom_holder.startAnimation(animation);

        /*animation = AnimationUtils.loadAnimation(this, R.anim.step_number);
        step_number.startAnimation(animation);*/
    }

}

