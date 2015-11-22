package com.babybox.util;

/**
 * Should read from server.
 */
public class DefaultValues {

    // Email signup need verification email
    public static final boolean EMAIL_SIGNUP_VERIFICATION_REQUIRED = false;

    // Http return code
    public static final int HTTP_STATUS_OK = 200;
    public static final int HTTP_STATUS_BAD_REQUEST = 400;

    // Default feeds
    public static final FeedFilter.FeedType DEFAULT_HOME_FEED_TYPE = FeedFilter.FeedType.HOME_EXPLORE;
    public static final FeedFilter.FeedType DEFAULT_CATEGORY_FEED_TYPE = FeedFilter.FeedType.CATEGORY_POPULAR;
    public static final FeedFilter.FeedType DEFAULT_USER_FEED_TYPE = FeedFilter.FeedType.USER_POSTED;
    public static final FeedFilter.FeedProductType DEFAULT_FEED_PRODUCT_TYPE = FeedFilter.FeedProductType.ALL;

    // From server
    public static final int DEFAULT_INFINITE_SCROLL_VISIBLE_THRESHOLD = 1;

    public static final int CONVERSATION_MESSAGE_COUNT = 20;

    // UI
    public static final String LANG_EN = "en";
    public static final String LANG_ZH = "zh";
    public static final String DEFAULT_LANG = LANG_ZH;

    public static final int MIN_CHAR_SIGNUP_PASSWORD = 4;

    public static final int SPLASH_DISPLAY_MILLIS = 500;
    public static final int DEFAULT_CONNECTION_TIMEOUT_MILLIS = 3000;
    public static final int DEFAULT_HANDLER_DELAY = 100;

    public static final int PULL_TO_REFRESH_DELAY = 250;

    public static final int FEEDVIEW_ITEM_TOP_MARGIN = 4;
    public static final int FEEDVIEW_ITEM_BOTTOM_MARGIN = 0;
    public static final int FEEDVIEW_ITEM_SIDE_MARGIN = 2;

    public static final int LISTVIEW_SLIDE_IN_ANIM_START = 10;
    public static final int LISTVIEW_SCROLL_FRICTION_SCALE_FACTOR = 2;

    public static final int CATEGORY_PICKER_POPUP_WIDTH = 250;
    public static final int CATEGORY_PICKER_POPUP_HEIGHT = 350;

    public static final int EMOTICON_POPUP_WIDTH = 300;
    public static final int EMOTICON_POPUP_HEIGHT = 100;

    public static final int MAX_POST_IMAGES = 4;
    public static final int MAX_MESSAGE_IMAGES = 1;
    public static final int MAX_POST_IMAGE_DIMENSION = 100;
    public static final int LATEST_COMMENTS_COUNT = 3;

    public static final int IMAGE_ROUNDED_RADIUS = 25;
    public static final int IMAGE_CIRCLE_RADIUS = 120;

    public static final int NEW_POST_DAYS_AGO = 3;
    public static final int NEW_POST_NOC = 3;
    public static final int HOT_POST_NOV = 200;
    public static final int HOT_POST_NOL = 5;
    public static final int HOT_POST_NOC = 5;

    public static final int DEFAULT_PARENT_BIRTH_YEAR = 9999;
}
