package com.babybox.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.babybox.R;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async load image to bitmap class.
 */
public class ImageLoadAsyncTask extends AsyncTask<Object, Void, Bitmap> {

    protected Activity activity;
    protected ImageView imageView;

    public static void loadImage(Activity activity, String source, ImageView imageView) {
        new ImageLoadAsyncTask().execute(activity, source, imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        activity = (Activity) params[0];
        showSpinner(true);

        String source = (String) params[1];
        if (!source.startsWith(activity.getResources().getString(R.string.base_url))) {
            source = activity.getResources().getString(R.string.base_url) + source;
        }

        imageView = (ImageView) params[2];
        try {
            InputStream is = new URL(source).openStream();
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            showSpinner(false);
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        showSpinner(false);

        if (bitmap != null) {
            Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());

            imageView.setVisibility(View.VISIBLE);
            imageView.setImageBitmap(bitmap);

            /*
            Drawable d = new BitmapDrawable(
                    activity.getResources(),
                    Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
            imageView.setImageDrawable(d);
            */

            Animation anim = AnimationUtils.loadAnimation(activity, R.anim.fade_in);
            imageView.startAnimation(anim);
        }
    }

    protected void showSpinner(final boolean show) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (show)
                    ViewUtil.showSpinner(activity);
                else
                    ViewUtil.stopSpinner(activity);
            }
        });
    }
}