package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.image.crop.Crop;
import com.babybox.util.ViewUtil;

import org.appsroid.fxpro.PhotoActivity;
import org.appsroid.fxpro.library.Constants;

import java.io.File;

public class SelectImageActivity extends Activity {
    final int SELECT_PICTURE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(getIntent().getData(), destination).asSquare().start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
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
            Intent i = new Intent(SelectImageActivity.this, PhotoActivity.class);
            i.putExtra("id",getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, -1L));
            i.putExtra(Constants.EXTRA_KEY_IMAGE_SOURCE, 2);
            i.putExtra("size", getIntent().getIntExtra("size", 0));
            i.setData(Crop.getOutput(result));
            Log.d(this.getClass().getSimpleName(), "handleCrop: size=" + getIntent().getIntExtra("size", 0));
            startActivity(i);
            finish();
        } else if (resultCode == Crop.RESULT_ERROR) {
            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed();
        finish();
    }
}
