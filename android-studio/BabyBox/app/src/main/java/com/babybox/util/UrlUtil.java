package com.babybox.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.babybox.app.AppController;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.GameGiftVM;
import com.babybox.viewmodel.PostVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class UrlUtil {

    private static final String CATEGORY_URL = AppController.BASE_URL + "/#!/category/%d";
    private static final String POST_URL = AppController.BASE_URL + "/#!/post/%d";
    //private static final String ANDROID_APP_DOWNLOAD_URL = AppController.BASE_URL + "/#!/apps/android";
    private static final String ANDROID_APP_DOWNLOAD_URL = "https://goo.gl/gdHjty";
    private static final String REFERRAL_URL = AppController.BASE_URL + "/signup-code/%s";
    private static final String GAME_GIFT_URL = AppController.BASE_URL + "/#!/game-gift/%d";

    private static String POST_URL_REGEX = ".*/post/(\\d+)";
    private static String CATEGORY_URL_REGEX = ".*/category/(\\d+)";

    public static String createReferralUrl(GameAccountVM gameAccount) {
        return String.format(REFERRAL_URL, gameAccount.getPmcde());
    }

    public static String createGameGiftUrl(GameGiftVM gameGift) {
        return String.format(GAME_GIFT_URL, gameGift.getId());
    }

    public static String createCategoryUrl(CategoryVM category) {
        return String.format(CATEGORY_URL, category.getId());
    }

    public static String createPostUrl(PostVM post) {
        return String.format(POST_URL, post.getId());
    }

    public static String createAndroidAppDownloadUrl() {
        return ANDROID_APP_DOWNLOAD_URL;
    }

    public static long parseCategoryUrlId(String url) {
        return parseUrlMatcher(CATEGORY_URL_REGEX, url);
    }

    public static long parsePostUrlId(String url) {
        return parseUrlMatcher(POST_URL_REGEX, url);
    }

    /**
     * Group always starts at 1. Group 0 is whole string.
     *
     * @param regex
     * @param url
     * @param pos
     * @return
     */
    private static long parseUrlMatcherAtPosition(String regex, String url, int pos) {
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(url);
        if (m.find()) {
            return Long.parseLong(m.group(pos));
        }
        return -1;
    }

    private static long parseUrlMatcher(String regex, String url) {
        return parseUrlMatcherAtPosition(regex, url, 1);
    }
}
