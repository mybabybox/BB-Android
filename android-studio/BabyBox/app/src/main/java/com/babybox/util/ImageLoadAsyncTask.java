package com.babybox.util;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.AppController;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Async load image to bitmap class.
 */
public class ImageLoadAsyncTask extends AsyncTask<Object, Void, Bitmap> {

    protected ImageView imageView;

    public static void loadImage(String source, ImageView imageView) {
        new ImageLoadAsyncTask().execute(source, imageView);
    }

    @Override
    protected Bitmap doInBackground(Object... params) {
        String source = (String) params[0];
        if (!source.startsWith(AppController.getInstance().getResources().getString(R.string.base_url))) {
            source = AppController.getInstance().getResources().getString(R.string.base_url) + source;
        }

        imageView = (ImageView) params[1];
        try {
            InputStream is = new URL(source).openStream();
            return BitmapFactory.decodeStream(is);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (bitmap != null) {
            Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());

            /*
            Drawable d = new BitmapDrawable(
                    AppController.getInstance().getResources(),
                    Bitmap.createScaledBitmap(bitmap, bitmap.getWidth(), bitmap.getHeight(), false));
            imageView.setImageDrawable(d);
            */
            imageView.setImageBitmap(bitmap);
            imageView.setVisibility(View.VISIBLE);
        }
    }
}