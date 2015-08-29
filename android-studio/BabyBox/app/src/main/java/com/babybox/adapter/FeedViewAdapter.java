package com.babybox.adapter;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.DetailActivity;
import com.babybox.util.DefaultValues;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVM;
//import com.nostra13.universalimageloader.core.assist.FailReason;
//import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;
//import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

import java.util.List;

/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.FeedViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private View headerView;

    private Activity activity;

    private List<PostVM> items;
    private boolean isFeed = true;

    public FeedViewAdapter(Activity activity, List<PostVM> items, View header) {
        this(activity, items, header, true);
    }

    public FeedViewAdapter(Activity activity, List<PostVM> items, View header, boolean isFeed) {
        this.activity = activity;
        this.items = items;
        this.headerView = header;
        this.isFeed = isFeed;
    }

    public boolean isHeader(int position) {
        return position == 0;
    }

    public PostVM getItem(int position) {
        return items.get(position - 1);     // header
    }

    @Override
    public int getItemCount() {
        return items.size() + 1;    // header
    }

    @Override
    public FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new FeedViewHolder(headerView);  // Dummy!! This will not be binded!!
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.feed_view_item, parent, false);
        FeedViewHolder holder = new FeedViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final FeedViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }

        final PostVM item = getItem(position);

        // NOTE: need to load images from UIL cache each time as ListAdapter items are being recycled...
        //       without this item will not show images correctly
        if(item.hasImage) {
            Log.d(this.getClass().getSimpleName(), "getView: load " + item.getImages().length + " images to post #" + position + " - " + item.getTitle());
            loadImage(item.getImages()[0], holder.imageView);
        }

        holder.textView.setText(item.getTitle());

        holder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, DetailActivity.class);
                if (item != null) {
                    intent.putExtra("postId", item.getId());
                    intent.putExtra("catId", item.getCategoryId());
                    intent.putExtra("flag", "FromFeedView");
                    activity.startActivity(intent);
                }
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    private void loadImage(Long imageId, ImageView imageView) {

        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(0, 0, 0, 0);

        ImageUtil.displayPostImage(imageId, imageView);
        /*
        ImageUtil.displayPostImage(imageId, imageView, new SimpleImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                ImageView imageView = (ImageView) view;
                imageView.setVisibility(View.GONE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                if (loadedImage != null) {
                    Log.d(FeedViewAdapter.class.getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "x" + loadedImage.getHeight());

                    int displayDimension =
                            (ViewUtil.getDisplayDimensions(FeedViewAdapter.this.activity).width() / DefaultValues.MAX_POST_IMAGES);

                    //Log.d(FeedViewAdapter.class.getSimpleName(), "onLoadingComplete: screen size="+activityUtil.getDisplayDimensions().width()+"x"+activityUtil.getDisplayDimensions().height());

                    // obsolete - start
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

                    Log.d(FeedViewAdapter.class.getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);
                    // obsolete - end

                    Drawable d = new BitmapDrawable(
                            FeedViewAdapter.this.activity.getResources(),
                            ImageUtil.cropToSquare(loadedImage, displayDimension));     // crop to square
                    ImageView imageView = (ImageView) view;
                    imageView.setImageDrawable(d);
                    imageView.setVisibility(View.VISIBLE);
                }
            }
        });
        */
    }

    /**
     * View item.
     */
    class FeedViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public FeedViewHolder(View holder) {
            super(holder);

            imageView = (ImageView) holder.findViewById(R.id.image);
            textView = (TextView) holder.findViewById(R.id.title);
        }
    }
}