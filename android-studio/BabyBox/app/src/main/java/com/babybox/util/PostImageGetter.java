package com.babybox.util;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.babybox.R;

public class PostImageGetter implements Html.ImageGetter{

    private TextView textView;

    private Activity activity;

    public PostImageGetter(Activity activity) {
        this.activity = activity;
    }

    public void setTextView(TextView textView) {
        this.textView = textView;
    }

    @Override
    public Drawable getDrawable(String source) {
        LevelListDrawable d = new LevelListDrawable();

        Log.d(this.getClass().getSimpleName(), "getDrawable: load post image from background - " + source);
        Drawable empty = activity.getResources().getDrawable(R.drawable.empty);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        new LoadImageToBody().execute(source, d);
        return d;
    }

    class LoadPostImage extends ImageLoadAsyncTask {

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (bitmap != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: loaded bitmap - " + bitmap.getWidth() + "|" + bitmap.getHeight());

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();

                // always stretch to screen width
                int displayWidth = ViewUtil.getDisplayDimensions(PostImageGetter.this.activity).width();
                float scaleAspect = (float)displayWidth / (float)width;
                width = displayWidth;
                height = (int)(height * scaleAspect);

                Log.d(this.getClass().getSimpleName(), "onPostExecute: after shrink - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                Drawable d = new BitmapDrawable(
                        activity.getResources(),
                        Bitmap.createScaledBitmap(bitmap, width, height, false));
                imageView.setImageDrawable(d);
                imageView.setVisibility(View.VISIBLE);
            }
        }
    }

    class LoadImageToBody extends ImageLoadAsyncTask {
        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);

            if (bitmap != null && textView != null) {
                Log.d(this.getClass().getSimpleName(), "onPostExecute: refresh body text");
                // i don't know yet a better way to refresh TextView
                // textView.invalidate() doesn't work as expected
                CharSequence t = textView.getText();
                textView.setText(t);
            }
        }
    }
}


