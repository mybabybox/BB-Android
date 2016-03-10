package com.babybox.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.opengl.GLSurfaceView;
import android.provider.MediaStore;
import android.os.Bundle;
import android.util.Log;
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
import java.util.ArrayList;
import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImage;
import jp.co.cyberagent.android.gpuimage.GPUImageBrightnessFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageContrastFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilter;
import jp.co.cyberagent.android.gpuimage.GPUImageFilterGroup;
import jp.co.cyberagent.android.gpuimage.GPUImageSaturationFilter;

public class EditImageActivity extends Activity {
    private static final String TAG = EditImageActivity.class.getName();

	private FilterAdjuster mFilterAdjuster;
	private GPUImageBrightnessFilter brightnessFilter;
    private GPUImageContrastFilter contrastFilter;
	private GPUImageSaturationFilter saturationFilter;
	private RelativeLayout brightButton, contrastButton, saturationButton, resetButton;
    private Button applyButton;
	public GPUImage imageView;
	private GPUImageFilter mFilter;
	private SeekBar brightSeekBar, contrastSeekBar, saturationSeekBar;
	private GLSurfaceView glSurfaceView;
	private GPUImageFilterGroup gpuImageFilterGroup;
	private List<GPUImageFilter> gpuImageFilters;
	private RelativeLayout relativeLayout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.edit_image_activity);

		brightButton = (RelativeLayout) findViewById(R.id.brightButton);
		contrastButton = (RelativeLayout) findViewById(R.id.contrastButton);
		saturationButton = (RelativeLayout) findViewById(R.id.saturationButton);
		resetButton = (RelativeLayout) findViewById(R.id.resetButton);
		applyButton = (Button) findViewById(R.id.applyButton);
		//glSurfaceView = (GLSurfaceView) findViewById(R.id.imageView);

		brightSeekBar = (SeekBar) findViewById(R.id.brightSeekBar);
		contrastSeekBar = (SeekBar) findViewById(R.id.contrastSeekBar);
		saturationSeekBar = (SeekBar) findViewById(R.id.saturationSeekBar);
		relativeLayout = (RelativeLayout) findViewById(R.id.imageLayout);

		gpuImageFilters = new ArrayList<>();

		glSurfaceView = new GLSurfaceView(this);

		imageView = new GPUImage(this);

		brightnessFilter = new GPUImageBrightnessFilter();
		contrastFilter = new GPUImageContrastFilter();
		saturationFilter = new GPUImageSaturationFilter();

		gpuImageFilters.add(brightnessFilter);
		gpuImageFilters.add(contrastFilter);
		gpuImageFilters.add(saturationFilter);

		gpuImageFilterGroup = new GPUImageFilterGroup(gpuImageFilters);

		Uri uri = Uri.parse(getIntent().getStringExtra("uri"));

        resetFilters();

		try {
			Bitmap bmp = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

			Bitmap b = Bitmap.createScaledBitmap(bmp, getIntent().getIntExtra("cropWidth", 0), getIntent().getIntExtra("cropHeight", 0), false);

            Log.d(TAG, "image width="+b.getWidth()+" height="+b.getHeight());

            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(b.getWidth(),b.getHeight());
			params.addRule(RelativeLayout.CENTER_IN_PARENT);
			glSurfaceView.setLayoutParams(params);

			relativeLayout.addView(glSurfaceView);

			imageView.setScaleType(GPUImage.ScaleType.CENTER_INSIDE);
			imageView.setImage(b);
			imageView.setGLSurfaceView(glSurfaceView);

		} catch (IOException e) {
			e.printStackTrace();
		}

		//imageView.setFilter(gpuImageFilterGroup);

		((SeekBar) findViewById(R.id.brightSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				brightnessFilter.setBrightness(range(i, -0.30f, 0.30f));
				imageView.setFilter(gpuImageFilterGroup);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		((SeekBar) findViewById(R.id.contrastSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				contrastFilter.setContrast(range(i, 0.5f, 1.5f));
				imageView.setFilter(gpuImageFilterGroup);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		((SeekBar) findViewById(R.id.saturationSeekBar)).setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
			@Override
			public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
				saturationFilter.setSaturation(range(i, 0.5f, 1.5f));
				imageView.setFilter(gpuImageFilterGroup);
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

		brightButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectFilter(true, false, false);
			}
		});

		contrastButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectFilter(false, true, false);
			}
		});

		saturationButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				selectFilter(false, false, true);
			}
		});

		resetButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
                resetFilters();
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

    private void resetFilters() {
        selectFilter(false, false, false);

        brightnessFilter.setBrightness(0.0f);
        contrastFilter.setContrast(1.0f);
        saturationFilter.setSaturation(1.0f);

        brightSeekBar.setProgress(50);
        contrastSeekBar.setProgress(50);
        saturationSeekBar.setProgress(50);

        imageView.setFilter(gpuImageFilterGroup);
    }

 	private void selectFilter(boolean brightness, boolean contrast, boolean saturation) {
		brightSeekBar.setVisibility(brightness? View.VISIBLE : View.GONE);
		contrastSeekBar.setVisibility(contrast? View.VISIBLE : View.GONE);
		saturationSeekBar.setVisibility(saturation? View.VISIBLE : View.GONE);

		brightButton.setBackgroundColor(
				brightness ? getResources().getColor(R.color.light_gray) : getResources().getColor(R.color.light_gray_3));
		contrastButton.setBackgroundColor(
				contrast? getResources().getColor(R.color.light_gray) : getResources().getColor(R.color.light_gray_3));
		saturationButton.setBackgroundColor(
				saturation ? getResources().getColor(R.color.light_gray) : getResources().getColor(R.color.light_gray_3));

		if (brightness) {
			mFilter = new GPUImageBrightnessFilter();
		} else if (contrast) {
			mFilter = new GPUImageContrastFilter();
		} else if (saturation) {
			mFilter = new GPUImageSaturationFilter();
		}
		mFilterAdjuster = new FilterAdjuster(mFilter);
	}

	protected float range(final int percentage, final float start, final float end) {
		return (end - start) * percentage / 100.0f + start;
	}

	private void saveImage(final Bitmap image) {
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

	@Override
	public void onBackPressed() {

		AlertDialog.Builder builder = new AlertDialog.Builder(EditImageActivity.this);
		builder.setMessage(getString(R.string.cancel_image))
				.setCancelable(false)
				.setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						Intent intent = new Intent();
						intent.setData(null);
						intent.putExtra(ViewUtil.INTENT_RESULT_OBJECT,"");
						setResult(RESULT_OK,intent);
						EditImageActivity.super.onBackPressed();
					}
				})
				.setNegativeButton(getString(R.string.no), new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
}




