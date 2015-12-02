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

public class PopupCategoryListAdapter extends BaseAdapter {
    private ImageView catIcon;
    private TextView catName;
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

        catIcon = (ImageView) convertView.findViewById(R.id.catIcon);
        catName = (TextView) convertView.findViewById(R.id.catName);

        CategoryVM item = categories.get(position);
        catName.setText(item.getName());
        ImageUtil.displayImage(item.getIcon(), catIcon);

        return convertView;
    }
}