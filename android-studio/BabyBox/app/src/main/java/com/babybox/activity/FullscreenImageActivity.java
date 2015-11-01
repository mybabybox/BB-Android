package com.babybox.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ImageLoadAsyncTask;
import com.babybox.util.ViewUtil;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class FullscreenImageActivity extends TrackedFragmentActivity {

    private RelativeLayout fullscreenLayout;
    private ImageViewTouch fullscreenImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_image_activity);

        fullscreenLayout = (RelativeLayout) findViewById(R.id.fullscreenLayout);
        fullscreenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        fullscreenImage = (ImageViewTouch) findViewById(R.id.fullscreenImage);
        fullscreenImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
        fullscreenImage.setSingleTapListener(new ImageViewTouch.OnImageViewTouchSingleTapListener() {
            @Override
            public void onSingleTapConfirmed() {
                finish();
            }
        });

        String source = getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_IMAGE_SOURCE);
        ImageLoadAsyncTask.loadImage(this, source, fullscreenImage);
    }
}