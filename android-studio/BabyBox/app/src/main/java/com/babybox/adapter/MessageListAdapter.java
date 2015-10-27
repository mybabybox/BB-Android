package com.babybox.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.app.UserInfoCache;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.MessageVM;

public class MessageListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageVM> messages;
    private ImageView messageImage;
    private ImageView senderImage;
    private boolean hasHeader;

    public MessageListAdapter(Activity activity, List<MessageVM> messages, boolean hasHeader) {
        this.activity = activity;
        this.messages = messages;
        this.hasHeader = hasHeader;
    }

    public boolean hasHeader() {
        return hasHeader;
    }

    @Override
    public int getCount() {
        return messages.size();
    }

    @Override
    public MessageVM getItem(int position) {
        if (hasHeader()) {
            return messages.get(position - 1);
        }
        return messages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        final MessageVM message = messages.get(position);

        Long user1Id = UserInfoCache.getUser().getId();
        Long user2Id = message.getSenderId();

        // Identifying the message owner
        if (user1Id.longValue() == user2Id.longValue()) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_left, null);
            senderImage = (ImageView) convertView.findViewById(R.id.senderImage);
            ImageUtil.displayThumbnailProfileImage(message.getSenderId(), senderImage);
        }

        TextView txtMsg = (TextView) convertView.findViewById(R.id.txtMsg);
        TextView dateMsg = (TextView) convertView.findViewById(R.id.messageDate);
        ViewUtil.setHtmlText(message.getBody(), txtMsg, activity, true, true);

        dateMsg.setText(DateTimeUtil.getTimeAgo(message.getCreatedDate()));

        messageImage = (ImageView) convertView.findViewById(R.id.messageImages);
        if(message.hasImage()) {
            if (message.getImage() != null) {
                loadImages(message, messageImage);
            }
            messageImage.setVisibility(View.VISIBLE);
        } else {
            messageImage.setVisibility(View.GONE);
        }

        messageImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.fullscreenImagePopup(activity, ImageUtil.ORIGINAL_MESSAGE_IMAGE_BY_ID_URL + message.getImage());
            }
        });

        return convertView;
    }

    private void loadImages(MessageVM item, final ImageView messageImage) {

        messageImage.setAdjustViewBounds(true);
        messageImage.setScaleType(ImageView.ScaleType.FIT_CENTER);
        messageImage.setPadding(0, 0, 0, ViewUtil.getRealDimension(10));

        ImageUtil.displayOriginalMessageImage(item.getImage(), messageImage);
        /*
        ImageUtil.displayOriginalMessageImage(item.getImgs(), messageImage, new RequestListener<String, GlideBitmapDrawable>() {
            @Override
            public boolean onException(Exception e, String model, Target<GlideBitmapDrawable> target, boolean isFirstResource) {
                messageImage.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(GlideBitmapDrawable resource, String model, Target<GlideBitmapDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                Bitmap loadedImage = resource.getBitmap();
                if (loadedImage != null) {
                    Log.d(MessageListAdapter.class.getSimpleName(), "onLoadingComplete: loaded bitmap - " + loadedImage.getWidth() + "|" + loadedImage.getHeight());

                    int width = loadedImage.getWidth();
                    int height = loadedImage.getHeight();

                    // always stretch to message width
                    int displayWidth = ViewUtil.getDisplayDimensions(MessageListAdapter.this.activity).width();
                    float scaleAspect = (float) displayWidth / (float) width;
                    width = displayWidth;
                    height = (int) (height * scaleAspect);

                    Log.d(MessageListAdapter.class.getSimpleName(), "onLoadingComplete: after resize - " + width + "|" + height + " with scaleAspect=" + scaleAspect);

                    Drawable d = new BitmapDrawable(
                            MessageListAdapter.this.activity.getResources(),
                            Bitmap.createScaledBitmap(loadedImage, width, height, false));
                    messageImage.setImageDrawable(d);
                    messageImage.setVisibility(View.VISIBLE);
                }
                return true;
            }
        });
        */
    }
}
