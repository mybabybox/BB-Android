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
import android.widget.SeekBar;

import com.babybox.R;
import com.babybox.image.effect.FilterAdjuster;
import com.babybox.image.effect.GPUImageTransformFilter;
import com.babybox.util.ViewUtil;

import org.joda.time.DateTime;

import java.io.IOException;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageGrayscaleFilter;
import jp.co.cyberagent.android.gpuimage.Rotation;

public class EditImageActivity extends Activity implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {

	private FilterAdjuster mFilterAdjuster;
	GPUImageBrightnessFilter brightnessFilter;
	GPUImageContrastFilter contrastFilter;
	private Button contrastButton, brightButton,applyButton,grayScaleButton;
	private GPUImage imageView;
	private GPUImageFilter mFilter;
	private SeekBar seekBar;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_image);
		brightButton = (Button) findViewById(R.id.brightButton);
		contrastButton = (Button) findViewById(R.id.contrastButton);
		applyButton = (Button) findViewById(R.id.applyButton);
		grayScaleButton = (Button) findViewById(R.id.blackScaleButton);

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

				brightButton.setBackgroundResource(R.drawable.button_pink);
				brightButton.setTextColor(getResources().getColor(R.color.white));

				grayScaleButton.setBackgroundResource(R.drawable.button_light_gray_border);
				grayScaleButton.setTextColor(getResources().getColor(R.color.input_text));
				contrastButton.setBackgroundResource(R.drawable.button_light_gray_border);
				contrastButton.setTextColor(getResources().getColor(R.color.input_text));

				mFilter = new GPUImageBrightnessFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);
			}
		});

		contrastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				seekBar.setVisibility(View.VISIBLE);

				contrastButton.setBackgroundResource(R.drawable.button_pink);
				contrastButton.setTextColor(getResources().getColor(R.color.white));

				grayScaleButton.setBackgroundResource(R.drawable.button_light_gray_border);
				grayScaleButton.setTextColor(getResources().getColor(R.color.input_text));
				brightButton.setBackgroundResource(R.drawable.button_light_gray_border);
				brightButton.setTextColor(getResources().getColor(R.color.input_text));


				mFilter = new GPUImageContrastFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);
			}
		});

		grayScaleButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				seekBar.setVisibility(View.VISIBLE);

				grayScaleButton.setBackgroundResource(R.drawable.button_pink);
				grayScaleButton.setTextColor(getResources().getColor(R.color.white));

				contrastButton.setBackgroundResource(R.drawable.button_light_gray_border);
				contrastButton.setTextColor(getResources().getColor(R.color.input_text));
				brightButton.setBackgroundResource(R.drawable.button_light_gray_border);
				brightButton.setTextColor(getResources().getColor(R.color.input_text));

				mFilter = new GPUImageGrayscaleFilter();
				mFilterAdjuster = new FilterAdjuster(mFilter);
				imageView.setFilter(mFilter);
			}
		});


		applyButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				imageView.saveToPictures("Babybox-Effect",String.valueOf(new DateTime().getSecondOfDay())+".jpg",new GPUImage.OnPictureSavedListener() {
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


