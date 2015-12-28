package com.babybox.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.ProductActivity;
import com.babybox.app.AppController;
import com.babybox.app.CountryCache;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVMLite;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class FeedViewAdapter extends RecyclerView.Adapter<FeedViewAdapter.FeedViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private View headerView;

    private Activity activity;

    private List<PostVMLite> items;

    private boolean showSeller = false;

    private int clickedPosition = -1;

    private boolean pending = false;

    public FeedViewAdapter(Activity activity, List<PostVMLite> items, View header) {
        this(activity, items, header, false);
    }

    public FeedViewAdapter(Activity activity, List<PostVMLite> items, View header, boolean showSeller) {
        this.activity = activity;
        this.items = items;
        this.headerView = header;
        this.showSeller = showSeller;
    }

    public int getClickedPosition() {
        return clickedPosition;
    }

    public boolean hasHeader() {
        return headerView != null;
    }

    public boolean isHeader(int position) {
        if (hasHeader()) {
            return position == 0;
        }
        return false;
    }

    public PostVMLite getItem(int position) {
        if (hasHeader()) {
            return items.get(position - 1);
        }
        return items.get(position);
    }

    public boolean isEmpty() {
        return items.size() == 0;
    }

    public PostVMLite getLastItem() {
        return getItem(getItemCount() - 1);
    }

    @Override
    public int getItemCount() {
        if (hasHeader()) {
            return items.size() + 1;
        }
        return items.size();
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

        final PostVMLite item = getItem(position);

        if (item.getImages() != null && item.getImages().length != 0) {
            loadImage(item.getImages()[0], holder.image);
        }

        if (showSeller) {
            ImageUtil.displayThumbnailProfileImage(item.getOwnerId(), holder.sellerImage);
            holder.sellerImage.setVisibility(View.VISIBLE);
        } else {
            holder.sellerImage.setVisibility(View.INVISIBLE);
        }

        if (item.freeDelivery) {
            holder.freeDeliveryImage.setVisibility(View.VISIBLE);
        } else {
            holder.freeDeliveryImage.setVisibility(View.INVISIBLE);
        }

        if (item.sold) {
            holder.soldImage.setVisibility(View.VISIBLE);
        } else {
            holder.soldImage.setVisibility(View.INVISIBLE);
        }

        if (!StringUtils.isEmpty(item.countryCode) &&
                !item.countryCode.equalsIgnoreCase(CountryCache.COUNTRY_CODE_NA)) {
            ImageUtil.displayImage(item.countryIcon, holder.countryImage);
            holder.countryImage.setVisibility(View.VISIBLE);
        } else {
            holder.countryImage.setVisibility(View.GONE);
        }

        if (UserInfoCache.getUser().isAdmin()) {
            holder.timeScoreText.setVisibility(View.VISIBLE);
            holder.timeScoreText.setText(item.getTimeScore()+"");
        } else {
            holder.timeScoreText.setVisibility(View.INVISIBLE);
        }

        ViewUtil.setHtmlText(item.getTitle(), holder.title, activity, true);
        holder.price.setText(ViewUtil.priceFormat(item.getPrice()));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPosition = position;
                ViewUtil.startProductActivity(activity, item.getId());
            }
        });

        PostVMLite lastItem = getLastItem();
        if (lastItem != null) {
            ((View) holder.itemLayout.getParent()).setTag(lastItem.getOffset());
            //holder.itemLayout.setTag(item.getOffset());
        }

        // like
        if (item.isLiked()) {
            ViewUtil.selectLikeTipsStyle(holder.likeImage, holder.likeText, item.getNumLikes());
        } else {
            ViewUtil.unselectLikeTipsStyle(holder.likeImage, holder.likeText, item.getNumLikes());
        }

        holder.likeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (item.isLiked) {
                    unlike(item, holder);
                } else {
                    like(item, holder);
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
    }

    private void like(final PostVMLite post, final FeedViewHolder holder) {
        if (pending) {
            return;
        }

        pending = true;
        AppController.getApiService().likePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                post.isLiked = true;
                post.numLikes++;
                ViewUtil.selectLikeTipsStyle(holder.likeImage, holder.likeText, post.getNumLikes());

                UserInfoCache.incrementNumLikes();

                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "like: failure", error);
                pending = false;
            }
        });
    }

    private void unlike(final PostVMLite post, final FeedViewHolder holder) {
        if (pending) {
            return;
        }

        if (post.numLikes <= 0) {
            return;
        }

        pending = true;
        AppController.getApiService().unlikePost(post.id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                post.isLiked = false;
                post.numLikes--;
                ViewUtil.unselectLikeTipsStyle(holder.likeImage, holder.likeText, post.getNumLikes());

                UserInfoCache.decrementNumLikes();

                pending = false;
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(ProductActivity.class.getSimpleName(), "unlike: failure", error);
                pending = false;
            }
        });
    }

    /**
     * View item.
     */
    class FeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView image;
        ImageView sellerImage;
        ImageView freeDeliveryImage;
        ImageView soldImage;
        ImageView countryImage;
        TextView timeScoreText;
        TextView title;
        TextView price;
        LinearLayout likeLayout;
        ImageView likeImage;
        TextView likeText;

        public FeedViewHolder(View holder) {
            super(holder);

            itemLayout = (LinearLayout) holder.findViewById(R.id.itemLayout);
            image = (ImageView) holder.findViewById(R.id.image);
            sellerImage = (ImageView) holder.findViewById(R.id.sellerImage);
            freeDeliveryImage = (ImageView) holder.findViewById(R.id.freeDeliveryImage);
            soldImage = (ImageView) holder.findViewById(R.id.soldImage);
            countryImage = (ImageView) holder.findViewById(R.id.countryImage);
            timeScoreText = (TextView) holder.findViewById(R.id.timeScoreText);
            title = (TextView) holder.findViewById(R.id.title);
            price = (TextView) holder.findViewById(R.id.price);
            likeLayout = (LinearLayout) holder.findViewById(R.id.likeLayout);
            likeImage = (ImageView) holder.findViewById(R.id.likeImage);
            likeText = (TextView) holder.findViewById(R.id.likeText);
        }

        void setTag(long score) {
            itemLayout.setTag(score);
        }
    }
}