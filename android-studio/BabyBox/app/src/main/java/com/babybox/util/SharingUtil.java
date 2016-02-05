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
import com.babybox.viewmodel.UserVM;
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

    public static final String SHARING_MESSAGE_NOTE = "";
            //ViewUtil.HTML_LINE_BREAK + AppController.getInstance().getString(R.string.sharing_message_note) +
            //        ViewUtil.HTML_LINE_BREAK + UrlUtil.createAppsDownloadUrl();

    private SharingUtil() {}

    public static void shareToWhatsapp(UserVM user, Activity activity) {
        String title = AppController.getInstance().getString(R.string.sharing_seller_msg_prefix) +
                ViewUtil.HTML_LINE_BREAK + user.getDisplayName();
        shareTo(title, "", UrlUtil.createSellerUrl(user), SharingType.WHATSAPP, activity);
    }

    public static void shareToFacebook(UserVM user, Activity activity) {
        String title = user.getDisplayName();
        String description = AppController.getInstance().getString(R.string.sharing_seller_msg_prefix);
        shareTo(title, description, UrlUtil.createSellerUrl(user), SharingType.FACEBOOK, activity);
    }

    public static void shareToWhatsapp(PostVM post, Activity activity) {
        String title = post.getTitle() + " $" + (int)post.getPrice() + ViewUtil.HTML_LINE_BREAK;
        shareTo(title, "", UrlUtil.createProductUrl(post), SharingType.WHATSAPP, activity);
    }

    public static void shareToFacebook(PostVM post, Activity activity) {
        String title = post.getTitle();
        String description = "$" + (int)post.getPrice() + " " + post.getBody();
        shareTo(title, description, UrlUtil.createProductUrl(post), SharingType.FACEBOOK, activity);
    }

    public static void shareToWhatsapp(CategoryVM category, Activity activity) {
        String title = category.getName();
        shareTo(title, "", UrlUtil.createCategoryUrl(category), SharingType.WHATSAPP, activity);
    }

    public static void shareToFacebook(CategoryVM category, Activity activity) {
        String title = category.getName();
        String description = category.getDescription();
        shareTo(title, description, UrlUtil.createCategoryUrl(category), SharingType.FACEBOOK, activity);
    }

    /**
     * http://www.whatsapp.com/faq/en/android/28000012
     *
     * @param title
     * @param description
     * @param type
     * @param activity
     */
    public static void shareTo(String title, String description, String url, SharingType type, Activity activity) {
        switch(type) {
            case WHATSAPP:
                shareToWhatsapp(title, description, url, activity);
                break;
            case FACEBOOK:
                shareToFacebook(title, description, url, activity);
                break;
        }
    }

    private static void shareToWhatsapp(String title, String description, String url, Activity activity) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(title + url).toString());
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

    private static void shareToFacebook(String title, String description, String url, Activity activity) {
        FacebookSdk.sdkInitialize(activity);

        ShareDialog shareDialog = new ShareDialog(activity);
        if (ShareDialog.canShow(ShareLinkContent.class)) {
            ShareLinkContent linkContent = new ShareLinkContent.Builder()
                    .setContentTitle(title)
                    .setContentDescription(description)
                    .setContentUrl(Uri.parse(url))
                    .build();
            shareDialog.show(linkContent);
        }
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
