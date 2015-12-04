package com.babybox.util;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.PostVM;
import com.facebook.FacebookSdk;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharingUtil {

    public enum SharingType {
        WHATSAPP,
        FACEBOOK
    }

    //public static final String SHARING_MESSAGE_NOTE = AppController.getInstance().getString(R.string.sharing_message_note);
    public static final String SHARING_MESSAGE_NOTE =
            ViewUtil.HTML_LINE_BREAK + AppController.getInstance().getString(R.string.sharing_message_note) +
                    ViewUtil.HTML_LINE_BREAK + UrlUtil.createAndroidAppDownloadUrl();

    private SharingUtil() {}

    public static void shareToWhatsapp(CategoryVM category, Activity activity) {
        shareTo(createMessage(category), UrlUtil.createCategoryUrl(category), SharingType.WHATSAPP, activity);

    }

    public static void shareToFacebook(CategoryVM category, Activity activity) {
        shareTo(createMessage(category), UrlUtil.createCategoryUrl(category), SharingType.FACEBOOK, activity);
    }

    public static void shareToWhatsapp(PostVM post, Activity activity) {
        shareTo(createMessage(post), UrlUtil.createPostUrl(post), SharingType.WHATSAPP, activity);
    }

    public static void shareToFacebook(PostVM post, Activity activity) {
        shareTo(createMessage(post), UrlUtil.createPostUrl(post), SharingType.FACEBOOK, activity);
    }

    /**
     * http://www.whatsapp.com/faq/en/android/28000012
     *
     * @param message
     * @param type
     * @param activity
     */
    public static void shareTo(String message, String url, SharingType type, Activity activity) {
        switch(type) {
            case WHATSAPP:
                shareToWhatsapp(message, url, activity);
                break;
            case FACEBOOK:
                shareToFacebook(message, url, activity);
                break;
        }
    }

    private static void shareToWhatsapp(String message, String url, Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message).toString());
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        try {
            activity.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AppController.getInstance(),
                    getSharingTypeName(SharingType.WHATSAPP) + AppController.getInstance().getString(R.string.sharing_app_not_installed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    private static void shareToFacebook(String message, String url, Activity activity) {
        FacebookSdk.sdkInitialize(activity);
        ShareDialog shareDialog = new ShareDialog(activity);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(AppController.APP_NAME)
                    .setContentDescription(message)
                    .setContentUrl(Uri.parse(url))
                    .build();
            shareDialog.show(linkContent);
        }
    }

    public static String createMessage(CategoryVM category) {
        String message = category.getName();
        String url = UrlUtil.createCategoryUrl(category);
        message = message +
                ViewUtil.HTML_LINE_BREAK +
                url +
                ViewUtil.HTML_LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    public static String createMessage(PostVM post) {
        String message = post.getTitle() + " - $" + post.getPrice();
        String url = UrlUtil.createPostUrl(post);
        message = message +
                ViewUtil.HTML_LINE_BREAK +
                url +
                ViewUtil.HTML_LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    private static String getSharingTypeName(SharingType type) {
        switch (type) {
            case WHATSAPP:
                return "Whatsapp";
            case FACEBOOK:
                return "Facebook";
        }
        return "";
    }
}
