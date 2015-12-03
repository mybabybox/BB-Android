package com.babybox.adapter;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.GameBadgeVM;

import java.util.List;

/**
 * http://blog.sqisland.com/2014/12/recyclerview-grid-with-header.html
 * https://github.com/chiuki/android-recyclerview
 */
public class GameBadgesViewAdapter extends RecyclerView.Adapter<GameBadgesViewAdapter.GameBadgesViewHolder> {

    private static final int ITEM_VIEW_TYPE_HEADER = 0;
    private static final int ITEM_VIEW_TYPE_ITEM = 1;

    private View headerView;

    private Activity activity;

    private List<GameBadgeVM> items;

    private int clickedPosition = -1;

    private boolean pending = false;

    public GameBadgesViewAdapter(Activity activity, List<GameBadgeVM> items, View header) {
        this.activity = activity;
        this.items = items;
        this.headerView = header;
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

    public GameBadgeVM getItem(int position) {
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
    public GameBadgesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == ITEM_VIEW_TYPE_HEADER) {
            return new GameBadgesViewHolder(headerView);  // Dummy!! This will not be binded!!
        }

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.game_badges_view_item, parent, false);
        GameBadgesViewHolder holder = new GameBadgesViewHolder(layoutView);
        return holder;
    }

    @Override
    public void onBindViewHolder(final GameBadgesViewHolder holder, final int position) {
        if (isHeader(position)) {
            return;
        }

        final GameBadgeVM item = getItem(position);
        loadImage(item.getIcon(), holder.image);
        holder.title.setText(item.getName());
        if (item.awarded) {
            holder.title.setTextColor(activity.getResources().getColor(R.color.pink));
        }

        holder.itemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clickedPosition = position;
                String msg = item.description;
                if (item.awarded) {
                    msg = activity.getString(R.string.game_badges_awarded_already) + "\n" + msg;
                }
                ViewUtil.alertGameStatus(activity, item.icon, msg);
                //Toast.makeText(activity, item.description, Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        return isHeader(position) ? ITEM_VIEW_TYPE_HEADER : ITEM_VIEW_TYPE_ITEM;
    }

    private void loadImage(String icon, ImageView imageView) {
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        imageView.setPadding(0, 0, 0, 0);
        ImageUtil.displayImage(icon, imageView);
    }

    /**
     * View item.
     */
    class GameBadgesViewHolder extends RecyclerView.ViewHolder {
        LinearLayout itemLayout;
        ImageView image;
        TextView title;

        public GameBadgesViewHolder(View holder) {
            super(holder);

            itemLayout = (LinearLayout) holder.findViewById(R.id.itemLayout);
            image = (ImageView) holder.findViewById(R.id.image);
            title = (TextView) holder.findViewById(R.id.title);
        }

        void setTag(long score) {
            itemLayout.setTag(score);
        }
    }
}