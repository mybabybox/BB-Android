package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.activity.GameGiftActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;

public class GameGiftListAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<GameGiftVM> items;
    private LinearLayout gameGiftLayout, viewsLayout;
    private ImageView gameGiftImage;
    private TextView titleText, pointsText, viewsText;
    private GameAccountVM gameAccount;

    public GameGiftListAdapter(Activity activity, List<GameGiftVM> items, GameAccountVM gameAccount) {
        this.activity = activity;
        this.items = items;
        this.gameAccount = gameAccount;
    }

    @Override
    public int getCount() {
        if (items == null)
            return 0;
        return items.size();
    }

    @Override
    public GameGiftVM getItem(int location) {
        if (items == null || location > items.size()-1)
            return null;
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.game_gift_item, null);

        gameGiftLayout = (LinearLayout) convertView.findViewById(R.id.gameGiftLayout);
        viewsLayout = (LinearLayout) convertView.findViewById(R.id.viewsLayout);
        gameGiftImage = (ImageView) convertView.findViewById(R.id.gameGiftImage);
        titleText = (TextView) convertView.findViewById(R.id.titleText);
        pointsText = (TextView) convertView.findViewById(R.id.pointsText);
        viewsText = (TextView) convertView.findViewById(R.id.viewsText);

        final GameGiftVM item = items.get(position);

        int resId = ImageMapping.map(item.getImt());
        if (resId != -1) {
            gameGiftImage.setImageDrawable(activity.getResources().getDrawable(resId));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load game gift image from background - " + item.getImt());
            ImageUtil.displayImage(item.getImt(), gameGiftImage);
        }

        titleText.setText(item.getNm());
        pointsText.setText(item.getRp()+"");

        gameGiftLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, GameGiftActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, item.getId());
                intent.putExtra("userGamePoints", gameAccount.getGmpt());
                activity.startActivityForResult(intent, ViewUtil.START_ACTIVITY_REQUEST_CODE);
            }
        });

        if (UserInfoCache.getUser().isAdmin()) {
            viewsLayout.setVisibility(View.VISIBLE);
            viewsText.setTextColor(activity.getResources().getColor(R.color.admin_green));
            viewsText.setText(item.getNov()+"");
        } else {
            viewsLayout.setVisibility(View.GONE);
        }

        return convertView;
    }
}