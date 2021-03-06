package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.image.crop.Crop;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.File;

public class SelectImageActivity extends Activity {
    private static final String TAG = SelectImageActivity.class.getName();

    final int SELECT_PICTURE = 1000;
    public String outputUrl;
    public Uri outputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        File imageFolder = new File(ImageUtil.getImageTempDirPath());
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        String now = String.valueOf(new DateTime().getSecondOfDay());
        String fname = "Image-"+now+".jpg";
        File file = new File (imageFolder, fname);
        outputUrl = file.getAbsolutePath();

        Uri destination = Uri.fromFile(file);
        outputUri = destination;

        Crop.of(getIntent().getData(), destination)
                .asSquare()
                .withMaxSize(ImageUtil.IMAGE_UPLOAD_MAX_WIDTH, ImageUtil.IMAGE_UPLOAD_MAX_HEIGHT)
                .start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_CANCELED) {
            finish();
        }

        if (requestCode == SELECT_PICTURE) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        } else if(requestCode == ViewUtil.ADJUST_IMAGE_REQUEST_CODE) {
            handleEffect(resultCode,data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination)
                .asSquare()
                .withMaxSize(ImageUtil.IMAGE_UPLOAD_MAX_WIDTH, ImageUtil.IMAGE_UPLOAD_MAX_HEIGHT)
                .start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.d(TAG, "handleCrop: outputUrl=" + outputUrl);

            if (DefaultValues.IMAGE_ADJUST_ENABLED) {
                Intent intent = new Intent(this, AdjustImageActivity.class);
                intent.putExtra("uri", outputUri + "");
                intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, outputUrl);
                intent.putExtra("cropWidth", result.getIntExtra("cropWidth", 0));
                intent.putExtra("cropHeight", result.getIntExtra("cropHeight", 0));
                setResult(RESULT_OK, intent);
                startActivityForResult(intent, ViewUtil.ADJUST_IMAGE_REQUEST_CODE);
            } else {
                Intent intent = new Intent();
                intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, outputUrl);
                setResult(RESULT_OK, intent);
                finish();
            }

        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        //finish();
    }

    private void handleEffect(int resultCode,Intent data){
        if (resultCode == RESULT_OK) {
            Intent intent = new Intent();
            intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, data.getStringExtra(ViewUtil.INTENT_RESULT_OBJECT));
            intent.setData(data.getData());
            setResult(RESULT_OK, intent);
            onBackPressed();
        }
    }
}