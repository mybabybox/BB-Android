package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.image.crop.Crop;
import com.babybox.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.File;

public class SelectImageActivity extends Activity {
    final int SELECT_PICTURE = 3;
    public String outputUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        String root = Environment.getExternalStorageDirectory().toString();
        File imageFolder = new File(root + "/" + getString(R.string.app_name));
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        String now = String.valueOf(new DateTime().getSecondOfDay());
        String fname = "Image-"+now+".jpg";
        File file = new File (imageFolder, fname);
        outputUrl = file.getAbsolutePath();

        Uri destination = Uri.fromFile(file);
        Crop.of(getIntent().getData(), destination).asSquare().start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == 0){
            finish();
        }

        if (requestCode == SELECT_PICTURE) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            AppController.getInstance().pathList.add(Uri.parse(outputUrl));
            AppController.getInstance().realPathList.add(outputUrl);
            Log.d(this.getClass().getSimpleName(), "handleCrop: size=" + getIntent().getIntExtra(ViewUtil.BUNDLE_KEY_INDEX, 0));
            AppController.getInstance().cropUri = Uri.parse(outputUrl);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
    }
}
