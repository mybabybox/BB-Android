package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.image.crop.Crop;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.File;

public class SelectImageActivity extends Activity {
    final int SELECT_PICTURE = 1000;
    public String outputUrl;
	public Uri outputUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.select);

        File imageFolder = new File(ImageUtil.IMAGE_FOLDER_PATH);
        if (!imageFolder.exists()) {
            imageFolder.mkdirs();
        }
        String now = String.valueOf(new DateTime().getSecondOfDay());
        String fname = "Image-"+now+".jpg";
        File file = new File (imageFolder, fname);
        outputUrl = file.getAbsolutePath();

        Uri destination = Uri.fromFile(file);

		outputUri = destination;

        Crop.of(getIntent().getData(), destination).asSquare().start(this);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if(resultCode == RESULT_CANCELED){
            finish();
        }

        if (requestCode == SELECT_PICTURE) {
            beginCrop(data.getData());
        } else if (requestCode == Crop.REQUEST_CROP) {
            handleCrop(resultCode, data);
        }else if(requestCode == ViewUtil.EDIT_IMAGE_REQUEST_CODE){
			handleEffect(resultCode,data);
		}
    }

    private void beginCrop(Uri source) {
        Uri destination = Uri.fromFile(new File(getCacheDir(), "cropped"));
        Crop.of(source, destination).asSquare().start(this);
    }

    private void handleCrop(int resultCode, Intent result) {
        if (resultCode == RESULT_OK) {
            Log.d(this.getClass().getSimpleName(), "handleCrop: outputUrl=" + outputUrl);
            Log.d(this.getClass().getSimpleName(), "handleCrop: outputUri=" + outputUri);

            // set activity result
            /*Intent intent = new Intent();
            intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, outputUrl);
            setResult(RESULT_OK, intent);*/

			if(DefaultValues.IMAGE_ADJUST_ENABLED) {
				Intent intent = new Intent(this, EditImageActivity.class);
				intent.putExtra("uri", outputUri + "");
				intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT, outputUrl);
				intent.putExtra("cropWidth", result.getIntExtra("cropWidth", 0));
				intent.putExtra("cropHeight", result.getIntExtra("cropHeight", 0));
				setResult(RESULT_OK, intent);
				startActivityForResult(intent, ViewUtil.EDIT_IMAGE_REQUEST_CODE);
			}else{
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
