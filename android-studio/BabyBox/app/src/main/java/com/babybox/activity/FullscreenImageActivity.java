package com.babybox.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

import it.sephiroth.android.library.imagezoom.ImageViewTouch;
import it.sephiroth.android.library.imagezoom.ImageViewTouchBase;

public class FullscreenImageActivity extends TrackedFragmentActivity {

    private RelativeLayout fullscreenLayout;
    private ImageViewTouch fullscreenImage;

    protected String selectedImagePath = null;
    protected Uri selectedImageUri = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fullscreen_image_activity);

        getActionBar().hide();

        fullscreenLayout = (RelativeLayout) findViewById(R.id.fullscreenLayout);
        fullscreenLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        fullscreenImage = (ImageViewTouch) findViewById(R.id.fullscreenImage);
        fullscreenImage.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);

        String source = getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_IMAGE_SOURCE);
        ImageUtil.displayImage(source, fullscreenImage);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE || requestCode == ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE ) {
                selectedImageUri = data.getData();
                selectedImagePath = ImageUtil.getRealPathFromUri(this, selectedImageUri);

                String path = selectedImageUri.getPath();
                Log.d(this.getClass().getSimpleName(), "onActivityResult: selectedImageUri=" + path + " selectedImagePath=" + selectedImagePath);

                Bitmap bitmap = ImageUtil.resizeToUpload(selectedImagePath);
                if (bitmap != null) {
                    setPostImage(bitmap);
                } else {
                    Toast.makeText(FullscreenImageActivity.this, FullscreenImageActivity.this.getString(R.string.photo_size_too_big), Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == ViewUtil.CROP_IMAGE_REQUEST_CODE) {
                setPostImage();
            }
        }
    }

    protected void setPostImage(Bitmap bp){
        fullscreenImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        fullscreenImage.setVisibility(View.VISIBLE);
    }

    protected void setPostImage() {
        // ImageView fullscreenImage = postImages.get(getIntent().getIntExtra(ViewUtil.BUNDLE_KEY_INDEX,0));
        // fullscreenImage.setImageURI(getIntent().getData());
        // fullscreenImage.setImageDrawable(new BitmapDrawable(this.getResources(), bp));
        if (ImageUtil.pathList.size() != 0) {
            for (int i = 0; i < ImageUtil.pathList.size(); i++) {
                //fullscreenImage.setImageBitmap(ImageUtil.resizeAsPreviewThumbnail(AppController.getInstance().realPathList.get(i)));
                fullscreenImage.setImageURI(ImageUtil.pathList.get(i));
                Log.d(this.getClass().getSimpleName(), "path=" + ImageUtil.realPathList.get(i));
            }
        }
    }
}
