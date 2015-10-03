package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.List;

import com.babybox.R;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
import com.babybox.viewmodel.EmoticonVM;

public class EmoticonListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private ImageView imageView;
    private List<EmoticonVM> emoticonVMList;

    public EmoticonListAdapter(Activity activity, List<EmoticonVM> emoticonVMList) {
        this.activity = activity;
        this.emoticonVMList = emoticonVMList;
    }

    @Override
    public int getCount() {
        return emoticonVMList.size();
    }

    @Override
    public Object getItem(int i) {
        EmoticonVM temp = emoticonVMList.get(i);
        return temp;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (view == null)
            view = inflater.inflate(R.layout.emoticon_grid_item, null);

        imageView = (ImageView) view.findViewById(R.id.emoImage);
        int resId = ImageMapping.map(emoticonVMList.get(i).getUrl());
        if (resId != -1) {
            imageView.setImageDrawable(activity.getResources().getDrawable(resId));
        } else {
            Log.d(this.getClass().getSimpleName(), "getView: load emoticon from background - " + emoticonVMList.get(i).getUrl());
            ImageUtil.displayImage(emoticonVMList.get(i).getUrl(), imageView);
        }

        return view;
    }
}
