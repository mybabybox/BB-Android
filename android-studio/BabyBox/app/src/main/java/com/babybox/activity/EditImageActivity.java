package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.SeekBar;

import com.babybox.R;
import com.babybox.image.effect.FilterAdjuster;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;

public class EditImageActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

	private FilterAdjuster mFilterAdjuster;
	private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageContrastFilter contrastFilter;
	private RelativeLayout contrastButton, brightButton;
    private Button applyButton;
	public GPUImage imageView;
	private GPUImageFilter mFilter;
	private SeekBar contrastSeekBar,brightSeekBar;
	private GLSurfaceView glSurfaceView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_image_activity);
		brightButton = (RelativeLayout) findViewById(R.id.brightButton);
		contrastButton = (RelativeLayout) findViewById(R.id.contrastButton);
		applyButton = (Button) findViewById(R.id.applyButton);
		glSurfaceView = (GLSurfaceView) findViewById(R.id.imageView);

		contrastSeekBar = (SeekBar) findViewById(R.id.contrastSeekBar);
		brightSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);

		contrastSeekBar.setProgress(25);
		brightSeekBar.setProgress(50);

		((SeekBar) findViewById(R.id.brightSeekBar)).setOnSeekBarChangeListener(this);
		((SeekBar) findViewById(R.id.contrastSeekBar)).setOnSeekBarChangeListener(this);

		imageView = new GPUImage(this);

		brightnessFilter = new GPUImageBrightnessFilter();
		contrastFilter = new GPUImageContrastFilter();

		Uri uri = Uri.parse(getIntent().getStringExtra("uri"));



		try {
			Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			glSurfaceView.setMinimumHeight(bmp.getHeight());
			glSurfaceView.setMinimumWidth(bmp.getWidth());
			imageView.setGLSurfaceView(glSurfaceView);

			imageView.setImage(bmp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		brightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				contrastSeekBar.setVisibility(View.GONE);
				brightSeekBar.setVisibility(View.VISIBLE);

				brightButton.setBackgroundResource(R.drawable.button_pink_2);
				contrastButton.setBackgroundResource(R.drawable.button_light_gray_border_2);

				mFilter = new GPUImageBrightnessFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);

				onProgressChanged(brightSeekBar,brightSeekBar.getProgress(),true);

			}
		});

		contrastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				brightSeekBar.setVisibility(View.GONE);
				contrastSeekBar.setVisibility(View.VISIBLE);

				contrastButton.setBackgroundResource(R.drawable.button_pink_2);
				brightButton.setBackgroundResource(R.drawable.button_light_gray_border_2);

				mFilter = new GPUImageContrastFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);

				onProgressChanged(contrastSeekBar,contrastSeekBar.getProgress(),true);
			}
		});

		applyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {

				Bitmap bitmap = imageView.getBitmapWithFilterApplied();

				saveImage(bitmap);



				/*imageView.saveToPictures(ImageUtil.IMAGE_FOLDER_PATH,String.valueOf(new DateTime().getSecondOfDay())+".jpg",new GPUImage.OnPictureSavedListener() {
					@Override
					public void onPictureSaved(Uri uri) {

						Intent intent = new Intent();
						intent.setData(uri);
						intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT,uri.toString());
						setResult(RESULT_OK,intent);

						finish();
					}
				});*/
			}
		});
	}

	@Override
	public void onClick(View view) {

	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

	 	 if(mFilterAdjuster != null) {
			if(mFilter instanceof GPUImageBrightnessFilter){
				brightnessFilter.setBrightness(range(i, -1.0f, 1.0f));
				imageView.setFilter(brightnessFilter);
			}else if (mFilter instanceof GPUImageContrastFilter){
				contrastFilter.setContrast(range(i, 0.0f, 4.0f));
				imageView.setFilter(contrastFilter);
			}
			mFilterAdjuster.adjust(i);
		}
	}

	@Override
	public void onStartTrackingTouch (SeekBar seekBar){

	}

	@Override
	public void onStopTrackingTouch (SeekBar seekBar){

	}

	protected float range(final int percentage, final float start, final float end) {
		return (end - start) * percentage / 100.0f + start;
	}

	private void saveImage(final Bitmap image) {
		//File path = ImageUtil.IMAGE_FOLDER_PATH;
		File file = new File(ImageUtil.IMAGE_FOLDER_PATH, String.valueOf(new DateTime().getSecondOfDay())+".jpg");
		try {
			file.getParentFile().mkdirs();
			image.compress(Bitmap.CompressFormat.JPEG, 80, new FileOutputStream(file));
			MediaScannerConnection.scanFile(this,
					new String[]{
							file.toString()
					}, null,
					new MediaScannerConnection.OnScanCompletedListener() {
						@Override
						public void onScanCompleted(final String path, final Uri uri) {
							Intent intent = new Intent();
							intent.setData(uri);
							intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT,uri.toString());
							setResult(RESULT_OK,intent);

							finish();

						}
					});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

}


