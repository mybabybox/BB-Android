package com.babybox.util;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LevelListDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.bumptech.glide.DrawableRequestBuilder;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.signature.StringSignature;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
 */
public class ImageUtil {
    private static final String TAG = ImageUtil.class.getName();

    public static final int PREVIEW_THUMBNAIL_MAX_WIDTH = 128;
    public static final int PREVIEW_THUMBNAIL_MAX_HEIGHT = 128;

    public static final int IMAGE_UPLOAD_MAX_WIDTH = 1024;
    public static final int IMAGE_UPLOAD_MAX_HEIGHT = 1024;

    public static final int IMAGE_COMPRESS_QUALITY = 80;

    public static final int IMAGE_DISPLAY_CROSS_FADE_DURATION = 200;

    private static final String COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-image-by-id/";
    private static final String THUMBNAIL_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-image-by-id/";
    private static final String PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    private static final String THUMBNAIL_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-profile-image-by-id/";
    private static final String POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-post-image-by-id/";
    private static final String ORIGINAL_POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-original-post-image-by-id/";
    private static final String MINI_POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-mini-post-image-by-id/";
    private static final String MESSAGE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-message-image-by-id/";
    private static final String ORIGINAL_MESSAGE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-original-message-image-by-id/";
    private static final String MINI_MESSAGE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-mini-message-image-by-id/";

    public static final String IMAGE_FOLDER_NAME = AppController.APP_NAME;
    public static final String IMAGE_FOLDER_PATH = Environment.getExternalStorageDirectory() + "/" + IMAGE_FOLDER_NAME;
    public static final String CAMERA_IMAGE_TEMP_PATH = IMAGE_FOLDER_PATH + "/" + "camera.jpg";

    private static ImageCircleTransform circleTransform =
            new ImageCircleTransform(AppController.getInstance());

    private static ImageRoundedTransform roundedTransform =
            new ImageRoundedTransform(AppController.getInstance(), DefaultValues.IMAGE_ROUNDED_RADIUS, 0);

    private static String stringSignature = AppController.getVersionCode()+"";  // default no signature

    private static File tempDir;

    static {
        init();
    }

    private ImageUtil() {}

    public static void init() {
        initImageTempDir();
    }

    // babybox temp directory

    public static void initImageTempDir() {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            File externalRoot = Environment.getExternalStorageDirectory();
            Log.d(TAG, "initImageTempDir: externalRoot="+externalRoot.getAbsolutePath());

            tempDir = new File(externalRoot, IMAGE_FOLDER_NAME);
            if (!tempDir.exists()) {
                tempDir.mkdir();
                Log.d(TAG, "initImageTempDir: create tempDir=" + tempDir.getAbsolutePath());
            } else {
                //clearTempDir();
            }
        } else {
            Log.e(ImageUtil.class.getSimpleName(), "initImageTempDir: no external storage!!!");
            tempDir = null;
        }
    }

    public static File getTempDir() {
        return tempDir;
    }

    private static void clearTempDir() {
        if (tempDir != null && tempDir.exists()) {
            File[] children = tempDir.listFiles();
            for (File f : children) {
                if (!f.isDirectory()) {
                    f.delete();
                }
            }
        }
    }

    // Url helpers

    public static String getOriginalPostImageUrl(long id) {
        return ORIGINAL_POST_IMAGE_BY_ID_URL + id;
    }

    public static String getOriginalMessageImageUrl(long id) {
        return ViewUtil.urlAppendSessionId(ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id);
    }

    // Cover image

    public static void displayCoverImage(long id, ImageView imageView) {
        Glide.clear(imageView);
        displayImage(COVER_IMAGE_BY_ID_URL + id, imageView, null, true, true);
    }

    public static void displayCoverImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(COVER_IMAGE_BY_ID_URL + id, imageView, listener, true, true);
    }

    public static void displayThumbnailCoverImage(long id, ImageView imageView) {
        displayImage(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id, imageView, null, true, true);
    }

    public static void displayThumbnailCoverImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id, imageView, listener, true, true);
    }

    // Profile image

    public static void displayProfileImage(long id, ImageView imageView) {
        displayCircleImage(PROFILE_IMAGE_BY_ID_URL + id, imageView, null, true, false);
    }

    public static void displayProfileImage(long id, ImageView imageView, RequestListener listener) {
        displayCircleImage(PROFILE_IMAGE_BY_ID_URL + id, imageView, listener, true, false);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView) {
        displayCircleImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, null, true, false);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView, RequestListener listener) {
        displayCircleImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, listener, true, false);
    }

    public static void displayMyProfileImage(long id, ImageView imageView, RequestListener listener) {
        displayCircleImage(PROFILE_IMAGE_BY_ID_URL + id, imageView, listener, true, true);
    }

    public static void displayMyThumbnailProfileImage(long id, ImageView imageView) {
        displayCircleImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView, null, true, true);
    }

    // Post image

    public static void displayPostImage(long id, ImageView imageView) {
        displayImage(POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayPostImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(POST_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayOriginalPostImage(long id, ImageView imageView) {
        displayImage(ORIGINAL_POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayOriginalPostImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(ORIGINAL_POST_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    public static void displayMiniPostImage(long id, ImageView imageView) {
        displayImage(MINI_POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayMiniPostImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(MINI_POST_IMAGE_BY_ID_URL + id, imageView, listener);
    }

    // Message image

    public static void displayMessageImage(long id, ImageView imageView) {
        displayImage(
                ViewUtil.urlAppendSessionId(MESSAGE_IMAGE_BY_ID_URL + id),
                imageView);
    }

    public static void displayMessageImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(
                ViewUtil.urlAppendSessionId(MESSAGE_IMAGE_BY_ID_URL + id),
                imageView,
                listener);
    }

    public static void displayOriginalMessageImage(long id, ImageView imageView) {
        displayImage(
                ViewUtil.urlAppendSessionId(ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id),
                imageView);
    }

    public static void displayOriginalMessageImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(
                ViewUtil.urlAppendSessionId(ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id),
                imageView,
                listener);
    }

    public static void displayMiniMessageImage(long id, ImageView imageView) {
        displayImage(
                ViewUtil.urlAppendSessionId(MINI_MESSAGE_IMAGE_BY_ID_URL + id),
                imageView);
    }

    public static void displayMiniMessageImage(long id, ImageView imageView, RequestListener listener) {
        displayImage(
                ViewUtil.urlAppendSessionId(MINI_MESSAGE_IMAGE_BY_ID_URL + id),
                imageView,
                listener);
    }

    // Default image

    public static void displayImage(String url, ImageView imageView) {
        displayImage(url, imageView, null);
    }

    public static void displayImage(String url, ImageView imageView, RequestListener listener) {
        displayImage(url, imageView, listener, true);
    }

    public static void displayImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop) {
        displayImage(url, imageView, listener, centerCrop, false);
    }

    public static void displayImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop, boolean noCache) {
        int resId = ImageMapping.map(url);
        if (resId != -1) {
            imageView.setImageDrawable(imageView.getResources().getDrawable(resId));
            return;
        }

        url = UrlUtil.getFullUrl(url);
        //Log.d(TAG, "displayImage: url="+url);

        DrawableRequestBuilder builder = Glide.with(AppController.getInstance())
                .load(url)
                .signature(new StringSignature(stringSignature))
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .crossFade(IMAGE_DISPLAY_CROSS_FADE_DURATION);

        displayImage(builder, imageView, listener, centerCrop, noCache, null);
    }

    // Circle image

    public static void displayCircleImage(String url, ImageView imageView) {
        displayCircleImage(url, imageView, null);
    }

    public static void displayCircleImage(String url, ImageView imageView, RequestListener listener) {
        displayCircleImage(url, imageView, listener, true);
    }

    public static void displayCircleImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop) {
        displayCircleImage(url, imageView, listener, centerCrop, false);
    }

    public static void displayCircleImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop, boolean noCache) {
        int resId = ImageMapping.map(url);
        if (resId != -1) {
            imageView.setImageDrawable(imageView.getResources().getDrawable(resId));
            return;
        }

        url = UrlUtil.getFullUrl(url);
        DrawableRequestBuilder builder = Glide.with(AppController.getInstance())
                .load(url)
                .signature(new StringSignature(stringSignature))
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .crossFade(IMAGE_DISPLAY_CROSS_FADE_DURATION);
                //.dontAnimate();

        displayImage(builder, imageView, listener, centerCrop, noCache, circleTransform);
    }

    // Rounded image

    public static void displayRoundedImage(String url, ImageView imageView) {
        displayRoundedImage(url, imageView, null);
    }

    public static void displayRoundedImage(String url, ImageView imageView, RequestListener listener) {
        displayRoundedImage(url, imageView, listener, true);
    }

    public static void displayRoundedImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop) {
        displayRoundedImage(url, imageView, listener, centerCrop, false);
    }

    public static void displayRoundedImage(String url, ImageView imageView, RequestListener listener, boolean centerCrop, boolean noCache) {
        int resId = ImageMapping.map(url);
        if (resId != -1) {
            imageView.setImageDrawable(imageView.getResources().getDrawable(resId));
            return;
        }

        url = UrlUtil.getFullUrl(url);
        DrawableRequestBuilder builder = Glide.with(AppController.getInstance())
                .load(url)
                .signature(new StringSignature(stringSignature))
                .placeholder(R.drawable.img_loading)
                .error(R.drawable.img_loading)
                .crossFade(IMAGE_DISPLAY_CROSS_FADE_DURATION);

        displayImage(builder, imageView, listener, centerCrop, noCache, roundedTransform);
    }

    private static void displayImage(
            DrawableRequestBuilder builder,
            ImageView imageView,
            RequestListener listener,
            boolean centerCrop,
            boolean noCache,
            BitmapTransformation transform) {

        if (listener != null) {
            builder = builder.listener(listener);
        }
        if (centerCrop) {
            builder = builder.fitCenter().centerCrop();
        }
        if (noCache) {
            builder = builder.diskCacheStrategy(DiskCacheStrategy.NONE);
        }
        if (transform != null) {
            builder = builder.transform(transform);
        }
        builder.into(imageView);
    }

    public static void clearProfileImageCache(long id){
        clearImageCache(PROFILE_IMAGE_BY_ID_URL + id);
        clearImageCache(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id);
    }

    public static void clearCoverImageCache(long id){
        clearImageCache(COVER_IMAGE_BY_ID_URL + id);
        clearImageCache(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id);
    }

    private static void clearImageCache(String url) {
        //Glide.get(AppController.getInstance()).clearMemory();
    }

    public static void clearImageView(ImageView imageView) {
        Glide.clear(imageView);
    }

    // Select photo

    public static void openPhotoGallery(Activity activity) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) &&
                !Environment.getExternalStorageState().equals(Environment.MEDIA_CHECKING)) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            activity.startActivityForResult(intent, ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE);
        } else {
            ViewUtil.alert(activity, activity.getString(R.string.no_media));
        }
    }

    public static void openPhotoPicker(Activity activity) {
        //openPhotoPicker(activity, activity.getString(R.string.photo_select));
        choosePhotoFrom(activity);
    }

    public static void openPhotoPicker(Activity activity, String title) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, title), ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE);
    }

    public static void choosePhotoFrom(final Activity activity){
        AlertDialog.Builder myAlertDialog = new AlertDialog.Builder(activity);
        myAlertDialog.setTitle(activity.getString(R.string.photo_select_title));
        //myAlertDialog.setMessage(activity.getString(R.string.photo_select_desc));

        myAlertDialog.setPositiveButton(
                activity.getString(R.string.photo_select_gallery),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_PICK);
                        intent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                        activity.startActivityForResult(intent, ViewUtil.SELECT_GALLERY_IMAGE_REQUEST_CODE);
                    }
                });

        myAlertDialog.setNegativeButton(
                activity.getString(R.string.photo_select_camera),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        Intent intent  = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {
                            // Error occurred while creating the File
                            ex.printStackTrace();
                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                            activity.startActivityForResult(intent, ViewUtil.SELECT_CAMERA_IMAGE_REQUEST_CODE);
                        }
                    }
                });

        myAlertDialog.show();
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            cursor = context.getContentResolver().query(contentUri, filePathColumn, null, null, null);
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndexOrThrow(filePathColumn[0]);
                String filePath = cursor.getString(columnIndex);
                return filePath;
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return "";
    }

    private static File createImageFile() throws IOException {
        File storageDir = new File(CAMERA_IMAGE_TEMP_PATH);
        storageDir.createNewFile();
        return storageDir;
    }

    public static Bitmap resizeAsPreviewThumbnail(String path) {
        return resizeImage(path, PREVIEW_THUMBNAIL_MAX_WIDTH, PREVIEW_THUMBNAIL_MAX_HEIGHT);
    }

    public static Bitmap resizeToUpload(String path) {
        return resizeImage(path, IMAGE_UPLOAD_MAX_WIDTH, IMAGE_UPLOAD_MAX_HEIGHT);
    }

    public static Bitmap resizeImage(String path, int maxWidth, int maxHeight) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        Bitmap bp = BitmapFactory.decodeFile(path, opts);

        int originalHeight = opts.outHeight;
        int originalWidth = opts.outWidth;
        int resizeScale = 1;

        Log.d(TAG, "resizeImage: outWidth="+originalWidth+" outHeight="+originalHeight);
        if ( originalWidth > maxWidth || originalHeight > maxHeight ) {
            final int widthRatio = Math.round((float) originalWidth / (float) maxWidth);
            final int heightRatio = Math.round((float) originalHeight / (float) maxHeight);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d(TAG, "resizeImage: resizeScale="+resizeScale);
        }

        // put the scale instruction (1 -> scale to (1/1); 8-> scale to 1/8)
        opts.inSampleSize = resizeScale;
        opts.inJustDecodeBounds = false;

        /*
        int bmSize = (originalWidth / resizeScale) * (originalHeight / resizeScale) * 4;
        if ( Runtime.getRuntime().freeMemory() > bmSize ) {
            bp = BitmapFactory.decodeFile(path, opts);
        } else {
            return null;
        }
        */

        bp = BitmapFactory.decodeFile(path, opts);

        // retain EXIF orientation
        try {
            ExifInterface exif = new ExifInterface(path);
            bp = retainOrientation(bp, exif);
        } catch (IOException ioe) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeImage: failed to retain orientation", ioe);
        }

        return bp;
    }

    public static File resizeAsJPG(File image) {
        return resizeAsFormat(Bitmap.CompressFormat.JPEG, image);
    }

    public static File resizeAsFormat(Bitmap.CompressFormat format, File image) {
        if (tempDir == null) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: tempDir is null!!!");
            return image;
        }

        File resizedImage = new File(tempDir, image.getName());
        if (!tempDir.canWrite()) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: "+tempDir.getAbsolutePath()+" cannot be written!!!");
            return image;
        }

        try {
            FileOutputStream out = new FileOutputStream(resizedImage);
            Bitmap resizedBitmap = ImageUtil.resizeToUpload(image.getAbsolutePath());
            if (resizedBitmap == null) {
                return image;
            }

            resizedBitmap.compress(format, IMAGE_COMPRESS_QUALITY, out);
            Log.d(TAG, "resizeAsFormat: successfully resized to path=" + resizedImage.getAbsolutePath());
            if (out != null) {
                out.close();
                out = null;
            }
        } catch (Exception e) {
            Log.e(ImageUtil.class.getSimpleName(), "resizeAsFormat: " + e.getMessage(), e);
        }

        return resizedImage;
    }

    /**
     * http://stackoverflow.com/questions/7286714/android-get-orientation-of-a-camera-bitmap-and-rotate-back-90-degrees
     *
     * @param bp
     * @param exif
     * @return
     */
    public static Bitmap retainOrientation(Bitmap bp, ExifInterface exif) {
        int rotation = exifToDegrees(exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,ExifInterface.ORIENTATION_NORMAL));
        if (rotation > 0) {
            int width = bp.getWidth();
            int height = bp.getHeight();

            Matrix matrix = new Matrix();
            matrix.preRotate(rotation);

            return Bitmap.createBitmap(bp, 0, 0, width, height, matrix, false);
        }
        return bp;
    }

    private static int exifToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }

    public static Bitmap cropToSquare(Bitmap bitmap) {
        return cropToSquare(bitmap, -1);
    }

    public static Bitmap cropToSquare(Bitmap bitmap, int dimension) {
        int width  = bitmap.getWidth();
        int height = bitmap.getHeight();
        int newWidth = (height > width) ? width : height;
        int newHeight = (height > width)? height - ( height - width) : height;
        int crop = (width - height) / 2;
        crop = (crop < 0)? 0: crop;
        Bitmap cropImg = Bitmap.createBitmap(bitmap, crop, 0, newWidth, newHeight);

        if (dimension != -1)
            cropImg = Bitmap.createScaledBitmap(cropImg, dimension, dimension, false);
        return cropImg;
    }

    public static Drawable getEmptyDrawable() {
        LevelListDrawable d = new LevelListDrawable();
        Drawable empty = AppController.getInstance().getResources().getDrawable(R.drawable.empty);
        d.addLevel(0, 0, empty);
        d.setBounds(0, 0, empty.getIntrinsicWidth(), empty.getIntrinsicHeight());
        return d;
    }
}