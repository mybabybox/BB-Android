package com.babybox.util;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.PostVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharingUtil {

    public enum SharingType {
        WHATSAPP
    }

    //public static final String SHARING_MESSAGE_NOTE = AppController.getInstance().getString(R.string.sharing_message_note);
    public static final String SHARING_MESSAGE_NOTE =
            ViewUtil.HTML_LINE_BREAK + AppController.getInstance().getString(R.string.sharing_message_note) +
                    ViewUtil.HTML_LINE_BREAK + UrlUtil.createAndroidAppDownloadUrl();

    private SharingUtil() {}

    public static void shareToWhatapp(GameAccountVM gameAccount, Context context) {
        shareTo(createMessage(gameAccount), SharingType.WHATSAPP, context);
    }

    public static void shareToWhatapp(GameGiftVM gameGift, Context context) {
        shareTo(createMessage(gameGift), SharingType.WHATSAPP, context);
    }

    public static void shareToWhatapp(CategoryVM category, Context context) {
        shareTo(createMessage(category), SharingType.WHATSAPP, context);
    }

    public static void shareToWhatapp(PostVM post, Context context) {
        shareTo(createMessage(post), SharingType.WHATSAPP, context);
    }

    /**
     * http://www.whatsapp.com/faq/en/android/28000012
     *
     * @param message
     * @param type
     * @param context
     */
    public static void shareTo(String message, SharingType type, Context context) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, Html.fromHtml(message).toString());
        sendIntent.setType("text/plain");

        switch(type) {
            case WHATSAPP:
                sendIntent.setPackage("com.whatsapp");
                break;
        }

        try {
            context.startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(AppController.getInstance(),
                    getSharingTypeName(type) + AppController.getInstance().getString(R.string.sharing_app_not_installed),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public static String createMessage(GameAccountVM gameAccount) {
        String message = AppController.getInstance().getResources().getString(R.string.app_desc);
        String url = UrlUtil.createReferralUrl(gameAccount);
        message = message +
                ViewUtil.HTML_LINE_BREAK +
                url +
                ViewUtil.HTML_LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
    }

    public static String createMessage(GameGiftVM gameGift) {
        String message = AppController.getInstance().getResources().getString(R.string.game_gifts_desc)+"："+gameGift.getNm();
        String url = UrlUtil.createGameGiftUrl(gameGift);
        message = message +
                ViewUtil.HTML_LINE_BREAK +
                url +
                ViewUtil.HTML_LINE_BREAK +
                SHARING_MESSAGE_NOTE;
        return message;
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
        String message = post.getTitle();
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
        }
        return "";
    }
}
