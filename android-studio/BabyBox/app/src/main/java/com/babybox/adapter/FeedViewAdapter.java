package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVM;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

public class FeedViewAdapter extends BaseAdapter {
    private TextView postTitle;
    private TextView username;
    private TextView timeText;
    private TextView numComments;
    private ImageView catIcon;
    private TextView catName;
    private LinearLayout postImagesLayout;

    private Activity activity;
    private LayoutInflater inflater;
    private List<PostVM> items;
    private boolean isFeed = true;
    private int lastPosition = -1;
    private int loadedImageCount = 0;

    public FeedViewAdapter(Activity activity, List<PostVM> items) {
        this(activity, items, true);
    }

    public FeedViewAdapter(Activity activity, List<PostVM> items, boolean isFeed) {
        this.activity = activity;
        this.items = items;
        this.isFeed = isFeed;
    }

    @Override
    public int getCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    @Override
    public PostVM getItem(int location) {
        if (items == null || location > items.size()-1)
            return null;
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.newsfeed_list_item, null);

        postTitle = (TextView) convertView.findViewById(R.id.postTitle);
        username = (TextView) convertView.findViewById(R.id.username);
        timeText = (TextView) convertView.findViewById(R.id.time);
        numComments = (TextView) convertView.findViewById(R.id.numComments);
        catIcon = (ImageView) convertView.findViewById(R.id.commIcon);
        catName = (TextView) convertView.findViewById(R.id.commName);

        // images
        postImagesLayout = (LinearLayout) convertView.findViewById(R.id.postImages);

        final PostVM item = items.get(position);

        //Log.d(this.getClass().getSimpleName(), "getView: Post - " + item.getTitle() + "|#comment: " + item.getNumComments());

        ViewUtil.setHtmlText(item.getTitle(), postTitle, activity);
        username.setText(item.getPostedBy());
        numComments.setText(item.getNumComments()+"");
        timeText.setText(DateTimeUtil.getTimeAgo(item.getUpdatedDate()));

        // admin fields
        LinearLayout adminLayout = (LinearLayout) convertView.findViewById(R.id.adminLayout);
        if (AppController.isUserAdmin()) {
            TextView numViews = (TextView) convertView.findViewById(R.id.numViews);
            numViews.setText(item.getNumViews() + "");

            ImageView androidIcon = (ImageView) convertView.findViewById(R.id.androidIcon);
            ImageView iosIcon = (ImageView) convertView.findViewById(R.id.iosIcon);
            ImageView mobileIcon = (ImageView) convertView.findViewById(R.id.mobileIcon);
            androidIcon.setVisibility(item.isAndroid()? View.VISIBLE : View.GONE);
            iosIcon.setVisibility(item.isIos()? View.VISIBLE : View.GONE);
            mobileIcon.setVisibility(item.isMobile()? View.VISIBLE : View.GONE);

            adminLayout.setVisibility(View.VISIBLE);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        if (isFeed) {
            catName.setText(item.getCategoryName());
            int iconMapped = ImageMapping.map(item.getCategoryIcon());
            if (iconMapped != -1) {
                //Log.d(this.getClass().getSimpleName(), "getView: replace source with local cat icon - " + catIcon);
                catIcon.setImageDrawable(convertView.getResources().getDrawable(iconMapped));
            } else {
                Log.d(this.getClass().getSimpleName(), "getView: load cat icon from background - " + item.getCategoryIcon());
                ImageUtil.displayRoundedCornersImage(item.getCategoryIcon(), catIcon);
            }
        } else {
            catName.setVisibility(View.GONE);
            catIcon.setVisibility(View.GONE);
        }

        // Post icons
        LinearLayout iconsLayout = (LinearLayout) convertView.findViewById(R.id.iconsLayout);
        ImageView iconImage = (ImageView) convertView.findViewById(R.id.iconImage);
        ImageView iconNew = (ImageView) convertView.findViewById(R.id.iconNew);
        ImageView iconHot = (ImageView) convertView.findViewById(R.id.iconHot);
        iconsLayout.setVisibility(View.GONE);
        iconImage.setVisibility(View.GONE);
        iconNew.setVisibility(View.GONE);
        iconHot.setVisibility(View.GONE);

        // New and Hot icons are mutually exclusive
        if (ViewUtil.isNewPost(item)) {
            iconsLayout.setVisibility(View.VISIBLE);
            iconNew.setVisibility(View.VISIBLE);
        } else if (ViewUtil.isHotPost(item)) {
            iconsLayout.setVisibility(View.VISIBLE);
            iconHot.setVisibility(View.VISIBLE);
        }
        //if (PostUtil.isImagePost(item)) {
        //    iconsLayout.setVisibility(View.VISIBLE);
        //    iconImage.setVisibility(View.VISIBLE);
        //}

        // images
        // NOTE: need to load images from UIL cache each time as ListAdapter items are being recycled...
        //       without this item will not show images correctly
        if(item.hasImage) {
            Log.d(this.getClass().getSimpleName(), "getView: load "+item.getImages().length+" images to post #"+position+" - "+item.getTitle());
            loadImages(item, postImagesLayout);
            postImagesLayout.setVisibility(View.VISIBLE);
        } else {
            postImagesLayout.setVisibility(View.GONE);
        }

        //Animation animation = AnimationUtils.loadAnimation(activity.getBaseContext(), (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        // Slide in from bottom
        if (position > lastPosition) {
            if (position > DefaultValues.LISTVIEW_SLIDE_IN_ANIM_START && lastPosition != -1) {
                //Log.d(this.getClass().getSimpleName(), "getView: animate up_from_bottom - " + position+"");
                Animation animation = AnimationUtils.loadAnimation(activity.getBaseContext(), R.anim.up_from_bottom);
                convertView.startAnimation(animation);
            }
            lastPosition = position;
        }

        return convertView;
    }

    private void loadImages(final PostVM item, final LinearLayout layout) {
        layout.removeAllViewsInLayout();

        final int padding = ViewUtil.getRealDimension(3, this.activity.getResources());
        final int totalPadding = padding * DefaultValues.MAX_POST_IMAGES;

        loadedImageCount = 0;
        for (Long imageId : item.getImages()) {
            if (loadedImageCount >= DefaultValues.MAX_POST_IMAGES)
                break;

            ImageView postImage = new ImageView(this.activity);
            postImage.setAdjustViewBounds(true);
            postImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
            postImage.setPadding(0, 0, padding, 0);
            layout.addView(postImage);
            loadedImageCount++;

            ImageUtil.displayPostImage(imageId, postImage, new SimpleImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    ImageView imageView = (ImageView)view;
                    imageView.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    if (loadedImage != null) {
                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "x" + loadedImage.getHeight());

                        int displayDimension =
                                (ViewUtil.getDisplayDimensions(FeedViewAdapter.this.activity).width() /
                                        DefaultValues.MAX_POST_IMAGES) - totalPadding;
                        //Log.d(this.getClass().getSimpleName(), "onLoadingComplete: screen size="+activityUtil.getDisplayDimensions().width()+"x"+activityUtil.getDisplayDimensions().height());

                        // obsolete
                        /*
                        int width = loadedImage.getWidth();
                        int height = loadedImage.getHeight();

                        float scaleAspect = 1;
                        if (width >= height) {  // landscape... scale according to width
                            scaleAspect = (float)displayDimension / (float)width;
                            width = displayDimension;
                            height = (int)(height * scaleAspect);
                        } else {    // portrait... scale according to height
                            scaleAspect = (float)displayDimension / (float)height;
                            width = (int)(width * scaleAspect);
                            height = displayDimension;
                        }

                        Log.d(this.getClass().getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);
                        */

                        Drawable d = new BitmapDrawable(
                                FeedViewAdapter.this.activity.getResources(),
                                ImageUtil.cropToSquare(loadedImage, displayDimension));     // crop to square
                        ImageView imageView = (ImageView)view;
                        imageView.setImageDrawable(d);
                        imageView.setVisibility(View.VISIBLE);
                    }
                }
            });
        }
    }
}