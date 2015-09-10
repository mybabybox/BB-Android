package com.babybox.adapter;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.ProductActivity;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PostVM;
import com.babybox.viewmodel.PostVMLite;

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

    private List<PostVMLite> items;

    public FeedViewAdapter(Activity activity, List<PostVMLite> items, View header) {
        this.activity = activity;
        this.items = items;
        this.headerView = header;
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

        loadImage(item.getImages()[0], holder.image);

        holder.title.setText(item.getTitle());
        holder.price.setText(ViewUtil.priceFormat(item.getPrice()));

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(activity, ProductActivity.class);
                if (item != null) {
                    intent.putExtra(ViewUtil.BUNDLE_KEY_POST_ID, item.getId());    // obsolete
                    intent.putExtra(ViewUtil.BUNDLE_KEY_ID, item.getId());
                    intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, "FromFeedView");
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
    }

    /**
     * View item.
     */
    class FeedViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView image;
        TextView title;
        TextView price;

        public FeedViewHolder(View holder) {
            super(holder);

            itemLayout = (LinearLayout) holder.findViewById(R.id.itemLayout);
            image = (ImageView) holder.findViewById(R.id.image);
            title = (TextView) holder.findViewById(R.id.title);
            price = (TextView) holder.findViewById(R.id.price);
        }
    }
}