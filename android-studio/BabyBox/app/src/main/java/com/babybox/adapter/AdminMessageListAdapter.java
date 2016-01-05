package com.babybox.adapter;

import android.app.Activity;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.AdminConversationVM;
import com.babybox.viewmodel.MessageVM;

import java.util.List;

public class AdminMessageListAdapter extends BaseAdapter {
    private static final String TAG = AdminMessageListAdapter.class.getName();

    private Activity activity;
    private LayoutInflater inflater;
    private List<MessageVM> messages;
    private ImageView messageImage;
    private ImageView senderImage;
    private boolean hasHeader;

    private AdminConversationVM conversation;

    public AdminMessageListAdapter(Activity activity, List<MessageVM> messages, AdminConversationVM conversation) {
        this.activity = activity;
        this.messages = messages;
        this.conversation = conversation;
        this.hasHeader = true;
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

        // seller as owner perspective
        if (conversation.getUser2Id().equals(message.getSenderId())) {
            // message belongs to you, so load the right aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_right, null);
        } else {
            // message belongs to other person, load the left aligned layout
            convertView = inflater.inflate(R.layout.list_item_message_left, null);
            senderImage = (ImageView) convertView.findViewById(R.id.senderImage);
            ImageUtil.displayThumbnailProfileImage(message.getSenderId(), senderImage);
        }

        TextView bodyText = (TextView) convertView.findViewById(R.id.bodyText);
        TextView dateMsg = (TextView) convertView.findViewById(R.id.messageDate);
        ViewUtil.setHtmlText(message.getBody(), bodyText, activity, true, true);
        bodyText.setTypeface(null, message.system? Typeface.BOLD : Typeface.NORMAL);

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
