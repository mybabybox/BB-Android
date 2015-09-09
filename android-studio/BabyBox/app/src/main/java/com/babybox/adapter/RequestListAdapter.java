package com.babybox.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.util.ImageUtil;
import com.babybox.viewmodel.NotificationVM;

public class RequestListAdapter extends BaseAdapter {
    private TextView message;
    private ImageView userPhoto;
    private Button acceptButton, ignoreButton;
    private Activity activity;
    private LayoutInflater inflater;
    private List<NotificationVM> requestItems;

    public RequestListAdapter(Activity activity, List<NotificationVM> requestItems) {
        this.activity = activity;
        this.requestItems = requestItems;
    }

    @Override
    public int getCount() {
        if (requestItems == null)
            return 0;
        return requestItems.size();
    }

    @Override
    public NotificationVM getItem(int location) {
        if (requestItems == null || location > requestItems.size()-1)
            return null;
        return requestItems.get(location);
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
            convertView = inflater.inflate(R.layout.request_list_item, null);

        final NotificationVM item = requestItems.get(position);

        LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.mainLayout);
        if (item.getSta() == 0)
            layout.setBackgroundDrawable(this.activity.getResources().getDrawable(R.drawable.rect_border_notification_new));

        message = (TextView) convertView.findViewById(R.id.requestText);
        userPhoto = (ImageView) convertView.findViewById(R.id.userImage);
        acceptButton = (Button) convertView.findViewById(R.id.acceptButton);
        ignoreButton = (Button) convertView.findViewById(R.id.ignoreButton);

        if (item.getTp().equals("COMM_JOIN_APPROVED") || item.getTp().equals("FRD_ACCEPTED")) {
            acceptButton.setVisibility(View.INVISIBLE);
            ignoreButton.setVisibility(View.INVISIBLE);
        } else {
            acceptButton.setVisibility(View.VISIBLE);
            ignoreButton.setVisibility(View.VISIBLE);
        }

        message.setText(item.getMsg());

        ImageUtil.displayImage(item.getUrl().getPhoto(), userPhoto);

        return convertView;
    }
}