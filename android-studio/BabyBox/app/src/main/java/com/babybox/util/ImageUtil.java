package com.babybox.util;

import android.app.Activity;
import android.content.Context;
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
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.babybox.R;
import com.babybox.app.AppController;

/**
 * http://inthecheesefactory.com/blog/get-to-know-glide-recommended-by-google/en
 */
public class ImageUtil {

    public static final String BABYBOX_TEMP_DIR_NAME = "BabyBox";

    public static final int PREVIEW_THUMBNAIL_MAX_WIDTH = 350;
    public static final int PREVIEW_THUMBNAIL_MAX_HEIGHT = 350;

    public static final int IMAGE_UPLOAD_MAX_WIDTH = 1024;
    public static final int IMAGE_UPLOAD_MAX_HEIGHT = 1024;

    public static final int IMAGE_COMPRESS_QUALITY = 85;

    public static final String COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-community-image-by-id/";
    public static final String THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-community-image-by-id/";
    public static final String COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-cover-image-by-id/";
    public static final String THUMBNAIL_COVER_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-cover-image-by-id/";
    public static final String PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-profile-image-by-id/";
    public static final String THUMBNAIL_PROFILE_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-thumbnail-image-by-id/";
    public static final String POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-post-image-by-id/";
    public static final String ORIGINAL_POST_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-original-post-image-by-id/";
    //public static final String COMMENT_IMAGE_BY_ID_URL = AppController.BASE_URL + "/image/get-comment-image-by-id/";
    public static final String MESSAGE_IMAGE_BY_ID_URL= AppController.BASE_URL + "/image/get-message-image-by-id/";
    public static final String ORIGINAL_MESSAGE_IMAGE_BY_ID_URL= AppController.BASE_URL + "/image/get-original-private-image-by-id/";

    private static ImageCircleTransform circleTransform = new ImageCircleTransform(AppController.getInstance());

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
            Log.d(ImageUtil.class.getSimpleName(), "initImageTempDir: externalRoot="+externalRoot.getAbsolutePath());

            tempDir = new File(externalRoot, BABYBOX_TEMP_DIR_NAME);
            if (!tempDir.exists()) {
                tempDir.mkdir();
                Log.d(ImageUtil.class.getSimpleName(), "initImageTempDir: create tempDir=" + tempDir.getAbsolutePath());
            } else {
                clearTempDir();
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

    // Community cover image

    public static void displayCommunityCoverImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayCommunityCoverImage: loading "+COMMUNITY_COVER_IMAGE_BY_ID_URL + id);
        displayImage(COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayThumbnailCommunityCoverImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayThumbnailCommunityCoverImage: loading " + THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL + id);
        displayImage(THUMBNAIL_COMMUNITY_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    // Cover image

    public static void displayCoverImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayCoverImage: loading "+COVER_IMAGE_BY_ID_URL + id);
        displayImage(COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayThumbnailCoverImage(long id, ImageView imageView, Context context) {
        Log.d(ImageUtil.class.getSimpleName(), "displayThumbnailCoverImage: loading "+THUMBNAIL_COVER_IMAGE_BY_ID_URL + id);
        displayImage(THUMBNAIL_COVER_IMAGE_BY_ID_URL + id, imageView);
    }

    // Profile image

    public static void displayProfileImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayProfileImage: loading "+PROFILE_IMAGE_BY_ID_URL + id);
        displayImage(PROFILE_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayThumbnailProfileImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayThumbnailProfileImage: loading " + THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id);
        displayImage(THUMBNAIL_PROFILE_IMAGE_BY_ID_URL + id, imageView);
    }

    // Post image

    public static void displayPostImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayPostImage: loading " + POST_IMAGE_BY_ID_URL + id);
        displayImage(POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayOriginalPostImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayOriginalPostImage: loading "+ORIGINAL_POST_IMAGE_BY_ID_URL + id);
        displayImage(ORIGINAL_POST_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayMessageImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayMessageImage: loading " + MESSAGE_IMAGE_BY_ID_URL + id);
        displayImage(MESSAGE_IMAGE_BY_ID_URL + id, imageView);
    }

    public static void displayOriginalMessageImage(long id, ImageView imageView) {
        Log.d(ImageUtil.class.getSimpleName(), "displayOriginalMessageImage: loading "+ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id);
        displayImage(ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + id, imageView);
    }

    // Generic

    public static void displayImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL)) {
            url = AppController.BASE_URL + url;
        }
        Glide.with(AppController.getInstance())
                .load(url)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .centerCrop()
                .crossFade()
                .into(imageView);
    }

    public static void displayCircleImage(String url, ImageView imageView) {
        if (!url.startsWith(AppController.BASE_URL)) {
            url = AppController.BASE_URL + url;
        }
        Glide.with(AppController.getInstance())
                .load(url)
                .placeholder(R.drawable.image_loading)
                .error(R.drawable.image_loading)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .transform(new ImageCircleTransform(AppController.getInstance()))
                .dontAnimate()
                .into(imageView);
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

    }

    // Select photo

    public static void openPhotoPicker(Activity activity) {
        openPhotoPicker(activity, activity.getString(R.string.photo_select));
    }

    public static void openPhotoPicker(Activity activity, String title) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_PICK);
        intent.setData(android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        //intent.setType("image/*");
        //intent.setAction(Intent.ACTION_GET_CONTENT);
        activity.startActivityForResult(Intent.createChooser(intent, title), ViewUtil.SELECT_PICTURE_REQUEST_CODE);
    }

    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
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

        Log.d(ImageUtil.class.getSimpleName(), "resizeImage: outWidth="+originalWidth+" outHeight="+originalHeight);
        if ( originalWidth > maxWidth || originalHeight > maxHeight ) {
            final int widthRatio = Math.round((float) originalWidth / (float) maxWidth);
            final int heightRatio = Math.round((float) originalHeight / (float) maxHeight);
            resizeScale = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d(ImageUtil.class.getSimpleName(), "resizeImage: resizeScale="+resizeScale);
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
            resizedBitmap.compress(format, IMAGE_COMPRESS_QUALITY, out);
            Log.d(ImageUtil.class.getSimpleName(), "resizeAsFormat: successfully resized to path=" + resizedImage.getAbsolutePath());
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
    private static Bitmap retainOrientation(Bitmap bp, ExifInterface exif) {
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