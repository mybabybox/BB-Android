package com.babybox.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.opengl.GLSurfaceView;
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

import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;

public class EditImageActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

	private FilterAdjuster mFilterAdjuster;
	private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageContrastFilter contrastFilter;
	private RelativeLayout contrastButton, brightButton;
    private Button applyButton;
	private GPUImage imageView;
	private GPUImageFilter mFilter;
	private SeekBar seekBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.edit_image_activity);
		brightButton = (RelativeLayout) findViewById(R.id.brightButton);
		contrastButton = (RelativeLayout) findViewById(R.id.contrastButton);
		applyButton = (Button) findViewById(R.id.applyButton);

		seekBar = (SeekBar) findViewById(R.id.seekBar);

		((SeekBar) findViewById(R.id.seekBar)).setOnSeekBarChangeListener(this);

		imageView = new GPUImage(this);

		brightnessFilter = new GPUImageBrightnessFilter();
		contrastFilter = new GPUImageContrastFilter();

		Uri uri = Uri.parse(getIntent().getStringExtra("uri"));

		imageView.setGLSurfaceView((GLSurfaceView) findViewById(R.id.imageView));

		try {
			Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
			imageView.setImage(bmp);
		} catch (IOException e) {
			e.printStackTrace();
		}

		brightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				seekBar.setVisibility(View.VISIBLE);

				brightButton.setBackgroundResource(R.drawable.button_pink_2);
				contrastButton.setBackgroundResource(R.drawable.button_light_gray_border_2);

				mFilter = new GPUImageBrightnessFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);
			}
		});

		contrastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				seekBar.setVisibility(View.VISIBLE);

				contrastButton.setBackgroundResource(R.drawable.button_pink_2);
				brightButton.setBackgroundResource(R.drawable.button_light_gray_border_2);

				mFilter = new GPUImageContrastFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);
			}
		});

		applyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				imageView.saveToPictures(ImageUtil.IMAGE_FOLDER_PATH,String.valueOf(new DateTime().getSecondOfDay())+".jpg",new GPUImage.OnPictureSavedListener() {
					@Override
					public void onPictureSaved(Uri uri) {

						Intent intent = new Intent();
						intent.setData(uri);
						intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT,uri.toString());
						setResult(RESULT_OK,intent);

						finish();
					}
				});
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
}


