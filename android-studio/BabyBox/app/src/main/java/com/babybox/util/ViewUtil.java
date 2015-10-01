package com.babybox.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.Selection;
import android.text.Spannable;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.CategoryActivity;
import com.babybox.activity.LoginActivity;
import com.babybox.activity.ProductActivity;
import com.babybox.activity.ProductCommentsActivity;
import com.babybox.activity.SignupDetailActivity;
import com.babybox.activity.SplashActivity;
import com.babybox.activity.UserProfileActivity;
import com.babybox.app.AppController;
import com.babybox.app.MyImageGetter;
import com.babybox.fragment.AbstractFeedViewFragment;
import com.babybox.viewmodel.CommentVM;
import com.babybox.viewmodel.PostVM;

import org.parceler.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import retrofit.RetrofitError;

/**
 * Created by keithlei on 3/16/15.
 */
public class ViewUtil {

    public static final int PAGER_INDICATOR_DOT_DIMENSION = 10;

    public static final String BUNDLE_KEY_LOGIN_KEY = "loginKey";
    public static final String BUNDLE_KEY_ID = "id";
    public static final String BUNDLE_KEY_OWNER_ID = "ownerId";
    public static final String BUNDLE_KEY_CATEGORY_ID = "catId";
    public static final String BUNDLE_KEY_POST_ID = "postId";
    public static final String BUNDLE_KEY_SOURCE = "flag";
    public static final String BUNDLE_KEY_FEED_TYPE = "feedType";
    public static final String BUNDLE_KEY_FEED_PRODUCT_TYPE = "feedProductType";
    public static final String BUNDLE_KEY_ACTION_TYPE = "actionType";
    public static final String BUNDLE_KEY_LISTS = "lists";

    public static final String BUNDLE_KEY_ARG1 = "arg1";
    public static final String BUNDLE_KEY_ARG2 = "arg2";
    public static final String BUNDLE_KEY_ARG3 = "arg3";

    public static final String INTENT_VALUE_REFRESH = "refresh";
    public static final int START_ACTIVITY_REQUEST_CODE = 1;
    public static final int SELECT_IMAGE_REQUEST_CODE = 2;

    public static final String HTML_LINE_BREAK = "<br>";

    public enum FeedItemPosition {
        UNKNOWN,
        HEADER,
        LEFT_COLUMN,
        RIGHT_COLUMN
    }

    private static Rect displayDimensions = null;

    private ViewUtil() {}

    //
    // Helper
    //

    public static int random(int low, int high) {
        return low + (int)(Math.random() * (high - low));
    }

    //
    // View
    //

    public static void addDots(Activity activity, final int numPages, LinearLayout dotsLayout, final List<ImageView> dots, ViewPager viewPager) {
        if (dotsLayout == null) {
            return;
        }

        Log.d(ViewUtil.class.getSimpleName(), "addDots: numPages="+numPages);

        dotsLayout.removeAllViews();

        int imageResource = R.drawable.ic_dot_sel;      // select the first dot by default
        for (int i = 0; i < numPages; i++) {
            ImageView dot = new ImageView(activity);
            dot.setImageDrawable(AppController.getInstance().getResources().getDrawable(imageResource));
            //dot.setAlpha(80F);

            int dimension = ViewUtil.getRealDimension(PAGER_INDICATOR_DOT_DIMENSION);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(dimension, dimension);
            params.gravity = Gravity.CENTER_VERTICAL;
            dotsLayout.addView(dot, params);

            dots.add(dot);
            imageResource = R.drawable.ic_dot;
        }

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                selectDot(position, numPages, dots);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });

        dotsLayout.setVisibility(numPages > 1? View.VISIBLE : View.GONE);
    }

    public static void selectDot(int index, int numPages, List<ImageView> dots) {
        if (dots.size() == 0) {
            return;
        }

        Resources res = AppController.getInstance().getResources();
        for (int i = 0; i < numPages; i++) {
            int drawableId = (i == index)? R.drawable.ic_dot_sel : R.drawable.ic_dot;
            Drawable drawable = res.getDrawable(drawableId);
            dots.get(i).setImageDrawable(drawable);
        }
    }

    public static void showSpinner(Activity activity) {
        if (activity != null) {
            ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
            if (spinner != null) {
                AnimationUtil.show(spinner);
            }
        }
    }

    public static void stopSpinner(Activity activity) {
        if (activity != null) {
            ProgressBar spinner = (ProgressBar) activity.findViewById(R.id.spinner);
            if (spinner != null) {
                AnimationUtil.cancel(spinner);
            }
        }
    }

    public static int getRealDimension(int size) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, size,
                AppController.getInstance().getResources().getDisplayMetrics());
    }

    public static Rect getDisplayDimensions(Activity activity) {
        if (displayDimensions == null) {
            int padding = 60;
            DisplayMetrics displaymetrics = new DisplayMetrics();
            activity.getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
            int width = displaymetrics.widthPixels - padding;
            int height = displaymetrics.heightPixels - padding;
            displayDimensions = new Rect(0, 0, width, height);
        }
        return displayDimensions;
    }

    public static void setHeightBasedOnChildren(Activity activity, ListView listView) {
        BaseAdapter adapter = (BaseAdapter) listView.getAdapter();
        if (adapter == null) {
            // pre-condition
            return;
        }

        // http://stackoverflow.com/questions/19908003/getting-height-of-text-view-before-rendering-to-layout
        int widthSpec = View.MeasureSpec.makeMeasureSpec(getDisplayDimensions(activity).width(), View.MeasureSpec.AT_MOST);
        int heightSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int totalHeight = 0;
        for (int i = 0; i < adapter.getCount(); i++) {
            View item = adapter.getView(i, null, listView);
            item.measure(widthSpec, heightSpec);
            totalHeight += item.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }

    public static void scrollTop(final ScrollView scrollView) {
        scrollTop(scrollView, true);
    }

    public static void scrollTop(final ScrollView scrollView, boolean delay) {
        if (delay) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    scrollView.fullScroll(View.FOCUS_UP);
                }
            }, DefaultValues.DEFAULT_HANDLER_DELAY);
        } else {
            scrollView.fullScroll(View.FOCUS_UP);
        }
    }

    public static List<CommentVM> getLastComments(List<CommentVM> comments, int n) {
        int start = Math.max(0, comments.size() - n);
        return comments.subList(start, comments.size());
    }

    public static String followersFormat(Long value) {
        return value + " " + AppController.getInstance().getString(R.string.followers);
    }

    public static String followingsFormat(Long value) {
        return value + " " + AppController.getInstance().getString(R.string.followings);
    }

    public static String productsTabFormat(Long value) {
        return value + "\n" + AppController.getInstance().getString(R.string.products);
    }

    public static String likesTabFormat(Long value) {
        return value + "\n" + AppController.getInstance().getString(R.string.likes);
    }

    public static String collectionsTabFormat(Long value) {
        return value + "\n" + AppController.getInstance().getString(R.string.collections);
    }

    public static void selectLikeButtonStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_liked);
        text.setText(likes+"");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.pink));
    }

    public static void unselectLikeButtonStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_like);
        text.setText(AppController.getInstance().getString(R.string.like));
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.dark_gray));
    }

    public static void selectLikeTipsStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_liked_tips);
        text.setText(likes+"");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
    }

    public static void unselectLikeTipsStyle(ImageView image, TextView text, int likes) {
        image.setImageResource(R.drawable.ic_like_tips);
        text.setText(likes+"");
        text.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
    }

    public static void selectFollowButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_following);
        button.setText(R.string.followings);
    }

    public static void unselectFollowButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_follow);
        button.setText(R.string.follow);
    }

    public static void selectFilterButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.white));
        button.setBackgroundResource(R.drawable.button_filter);
    }

    public static void unselectFilterButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.dark_gray));
        button.setBackgroundResource(R.drawable.button_filter_unselect);
    }

    public static void selectProfileFeedButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.pink));
        button.setBackgroundResource(R.drawable.button_profile_feed);
    }

    public static void unselectProfileFeedButtonStyle(Button button) {
        button.setTextColor(AppController.getInstance().getResources().getColor(R.color.gray));
        button.setBackgroundResource(R.drawable.button_profile_feed_unselect);
    }

    public static FeedItemPosition getFeedItemPosition(AbstractFeedViewFragment feedViewFragment, View feedItemView) {
        RecyclerView recyclerView = feedViewFragment.getFeedView();
        int pos = recyclerView.getChildAdapterPosition(feedItemView);
        if (feedViewFragment.hasHeader()) {
            if (pos == 0) {
                return FeedItemPosition.HEADER;
            }
            // real position
            pos = pos - 1;
        }

        pos = pos % 2;
        if (pos == 0) {
            return FeedItemPosition.LEFT_COLUMN;
        } else if (pos == 1) {
            return FeedItemPosition.RIGHT_COLUMN;
        }
        return FeedItemPosition.UNKNOWN;
    }

    public static void showTips(final SharedPreferencesUtil.Screen screen, final ViewGroup tipsLayout, final View dismissTipsButton) {
        if (SharedPreferencesUtil.getInstance().isScreenViewed(screen)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(screen);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }
    }

    public static void fullscreenImagePopup(Activity activity, String source) {
        try {
            //frameLayout.getForeground().setAlpha(20);
            //frameLayout.getForeground().setColorFilter(R.color.gray, PorterDuff.Mode.OVERLAY);

            //We need to get the instance of the LayoutInflater, use the context of this activity
            LayoutInflater inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //Inflate the view from a predefined XML layout
            View layout = inflater.inflate(R.layout.image_popup_window,(ViewGroup) activity.findViewById(R.id.popupElement));
            ImageView fullImage= (ImageView) layout.findViewById(R.id.fullImage);

            PopupWindow imagePopup = new PopupWindow(layout, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT,true);
            imagePopup.setOutsideTouchable(false);
            imagePopup.setBackgroundDrawable(new BitmapDrawable(activity.getResources(), ""));
            imagePopup.setFocusable(true);
            imagePopup.showAtLocation(layout, Gravity.CENTER, 0, 0);

            ImageUtil.displayImage(source, fullImage);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //
    // HTML
    //

    public static void setHtmlText(String text, TextView textView, Activity activity) {
        setHtmlText(text, textView, activity, false);
    }

    public static void setHtmlText(String text, TextView textView, Activity activity, boolean longClickSelectAll) {
        setHtmlText(text, textView, activity, longClickSelectAll, false);
    }

    public static void setHtmlText(String text, TextView textView, Activity activity, boolean longClickSelectAll, boolean linkMovement) {
        if (StringUtils.isEmpty(text)) {
            text = "";
        }

        MyImageGetter imageGetter = new MyImageGetter(activity);

        text = text.replace("\n", HTML_LINE_BREAK);

        imageGetter.setTextView(textView);
        Spanned spanned = Html.fromHtml(text, imageGetter, null);

        textView.setText(spanned);
        if (linkMovement) {
            setLinksClickable(textView);
        }

        if (longClickSelectAll) {
            setLongClickSelectAll(textView);
        }
    }

    //
    // Text
    //

    public static boolean copyToClipboard(String text) {
        return ClipboardUtil.copyToClipboard(AppController.getInstance(), text);
    }

    public static boolean copyToClipboard(TextView textView) {
        return ClipboardUtil.copyToClipboard(AppController.getInstance(), textView.getText().toString());
    }

    public static void readFromClipboardTo(TextView textView) {
        String text = ClipboardUtil.readFromClipboard(AppController.getInstance());
        if (!StringUtils.isEmpty(text)) {
            textView.setText(text);
        }
    }

    public static void setLinksClickable(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setLinksClickable(true);
        textView.setLinkTextColor(textView.getResources().getColor(R.color.link));
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    public static void setLongClickSelectAll(TextView textView) {
        textView.setTextIsSelectable(true);
        textView.setFocusable(true);
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (v instanceof TextView) {
                    TextView textView = (TextView) v;
                    //textView.setSelectAllOnFocus(true);
                    //textView.setSelected(true);
                    Selection.selectAll((Spannable) textView.getText());
                    textView.setCursorVisible(true);
                    textView.requestFocus();
                }
                return false;
            }
        });
    }

    public static String priceFormat(double value) {
        //return String.format("$%.2f", value);
        return String.format("$%.0f", value);
    }

    //
    // Popup soft keyboard
    //

    public static void popupInputMethodWindow(final Activity activity) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getApplicationContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 100);
    }

    public static void hideInputMethodWindow(final Activity activity, final View view) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }, 100);
    }

    //
    // Alert dialog
    //

    public static void alert(Context context, String message) {
        alert(context, null, message);
    }

    public static void alert(Context context, String title, String message) {
        alert(context, title, message,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialog != null) {
                            dialog.dismiss();
                        }
                    }
                });
    }

    public static void alert(Context context, String title, String message, DialogInterface.OnClickListener onClick) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context)
                .setMessage(message)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton(android.R.string.yes, onClick);
        if (!StringUtils.isEmpty(title)) {
            alertBuilder.setTitle(title);
        }
        alertBuilder.show();
    }

    public static Dialog alert(Context context, int dialogResourceId) {
        return alert(context, dialogResourceId, -1, null);
    }

    public static Dialog alert(Context context, int dialogResourceId, int buttonResourceId, final View.OnClickListener onClick) {
        LayoutInflater factory = LayoutInflater.from(context);
        final View dialogView = factory.inflate(dialogResourceId, null);
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.setContentView(dialogView);
        //dialog.setButton(AlertDialog.BUTTON_POSITIVE, context.getString(android.R.string.yes), onClick);
        if (buttonResourceId != -1) {
            Button button = (Button) dialogView.findViewById(buttonResourceId);
            button.setOnClickListener(new View.OnClickListener() {
                public void onClick(View view) {
                    if (onClick != null) {
                        onClick.onClick(view);
                    }
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            });
        }
        dialog.show();
        return dialog;
    }

    public static Dialog alertGameStatus(Context context, String desc) {
        return alertGameStatus(context, desc, -1, 3000);
    }

    public static Dialog alertGameStatus(Context context, String desc, int points) {
        return alertGameStatus(context, desc, points, 3000);
    }

    public static Dialog alertGameStatus(Context context, String desc, int points, long delayMillis) {
        final Dialog dialog = alert(context, R.layout.game_points_popup_window);
        ImageView mascot = (ImageView) dialog.findViewById(R.id.mascot);
        TextView descText = (TextView) dialog.findViewById(R.id.descText);
        TextView pointsText = (TextView) dialog.findViewById(R.id.pointsText);
        TextView endText = (TextView) dialog.findViewById(R.id.endText);

        descText.setText(desc);
        if (points == -1) {
            mascot.setVisibility(View.GONE);
            pointsText.setVisibility(View.GONE);
            endText.setVisibility(View.GONE);
        } else {
            pointsText.setText("+"+points);
        }

        ImageView dismissImage = (ImageView) dialog.findViewById(R.id.dismissImage);
        dismissImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                }
            }
        });

        // auto dismiss
        if (delayMillis != -1) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (dialog != null && dialog.isShowing()) {
                        dialog.dismiss();
                    }
                }
            }, delayMillis);
        }
        return dialog;
    }

    //
    // Retrofit
    //

    public static String getResponseBody(retrofit.client.Response response) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            reader = new BufferedReader(new InputStreamReader(response.getBody().in()));
            String line;
            try {
                while ((line = reader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sb.toString();
    }

    public static int getErrorStatusCode(RetrofitError error) {
        if (error.isNetworkError()) {
            return 550; // Use another code if you'd prefer
        }

        return error.getResponse().getStatus();
    }

    //
    // Post
    //

    public static boolean isNewPost(PostVM post) {
        return post.getNumComments() <= DefaultValues.NEW_POST_NOC &&
                DateTimeUtil.getDaysAgo(post.getCreatedDate()) <= DefaultValues.NEW_POST_DAYS_AGO;
    }

    public static boolean isHotPost(PostVM post) {
        return post.getNumComments() > DefaultValues.HOT_POST_NOC ||
                post.getNumLikes() > DefaultValues.HOT_POST_NOL ||
                post.getNumViews() > DefaultValues.HOT_POST_NOV;
    }

    //
    // Start Activities
    //

    public static void startLoginActivity(Activity activity) {
        activity.startActivity(new Intent(activity, LoginActivity.class));
        activity.finish();
    }

    public static void startSignupDetailActivity(Activity activity, String username) {
        Intent intent = new Intent(activity, SignupDetailActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ARG1, username);
        activity.startActivity(intent);
    }

    public static void startSplashActivity(Activity activity, String key) {
        Intent intent = new Intent(activity, SplashActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_LOGIN_KEY, key);
        activity.startActivity(intent);
    }

    public static void startCategoryActivity(Activity activity, Long catId, String source) {
        Intent intent = new Intent(activity, CategoryActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, catId);
        intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, source);
        activity.startActivity(intent);
    }

    public static void startProductActivity(Activity activity, Long postId, String source) {
        Intent intent = new Intent(activity, ProductActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, postId);
        intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, source);
        activity.startActivity(intent);
    }

    public static void startUserProfileActivity(Activity activity, Long userId, String source) {
        Intent intent = new Intent(activity, UserProfileActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, userId);
        intent.putExtra(ViewUtil.BUNDLE_KEY_SOURCE, source);
        activity.startActivity(intent);
    }

    public static void startProductCommentsActivity(Activity activity, Long postId) {
        Intent intent = new Intent(activity, ProductCommentsActivity.class);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, postId);
        activity.startActivity(intent);
    }
}
