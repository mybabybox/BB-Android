package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;

public class PopupCategoryListAdapter extends BaseAdapter {
    private ImageView communityIcon;
    private TextView communityName;
    private Activity activity;
    private List<CategoryVM> categories;

    private LayoutInflater inflater;

    public PopupCategoryListAdapter(Activity activity, List<CategoryVM> categories) {
        this.activity = activity;
        this.categories = categories;
    }

    @Override
    public int getCount() {
        if (categories == null)
            return 0;
        return categories.size();
    }

    @Override
    public CategoryVM getItem(int location) {
        if (categories == null || location > categories.size()-1)
            return null;
        return categories.get(location);
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
            convertView = inflater.inflate(R.layout.category_popup_item, null);

        communityIcon = (ImageView) convertView.findViewById(R.id.commIcon);
        communityName = (TextView) convertView.findViewById(R.id.communityName);

        CategoryVM item = categories.get(position);

        communityName.setText(item.getName());
        int iconMapped = ImageMapping.map(item.getIcon());
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "getView: replace source with local comm icon - " + commIcon);
            communityIcon.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load comm icon from background - " + item.getIcon());
            ImageUtil.displayCircleImage(item.getIcon(), communityIcon);
        }

        return convertView;
    }
}