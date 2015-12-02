package com.babybox.util;

import android.widget.TextView;

import org.parceler.apache.commons.lang.StringUtils;

import java.util.HashMap;
import java.util.Map;

import com.babybox.R;
import com.babybox.viewmodel.EmoticonVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class ImageMapping {

    public final static int EMOTICON_WIDTH = 18;
    public final static int EMOTICON_HEIGHT = 18;

    public final static Map<String, Integer> imageMap;

    static {
        imageMap = new HashMap<>();
        imageMap.put("/assets/app/images/category/cat_preg.jpg", R.drawable.cat_preg);
        imageMap.put("/assets/app/images/category/cat_beauty.jpg", R.drawable.cat_beauty);
        imageMap.put("/assets/app/images/category/cat_clothes.jpg", R.drawable.cat_clothes);
        imageMap.put("/assets/app/images/category/cat_toys.jpg", R.drawable.cat_toys);
        imageMap.put("/assets/app/images/category/cat_books.jpg", R.drawable.cat_books);
        imageMap.put("/assets/app/images/category/cat_food.jpg", R.drawable.cat_food);
        imageMap.put("/assets/app/images/category/cat_home.jpg", R.drawable.cat_home);
        imageMap.put("/assets/app/images/category/cat_utils.jpg", R.drawable.cat_utils);
        imageMap.put("/assets/app/images/category/cat_diaper.jpg", R.drawable.cat_diaper);
        imageMap.put("/assets/app/images/category/cat_other.jpg", R.drawable.cat_other);
        imageMap.put("/assets/app/images/game/badges/profile_photo.png", R.drawable.game_badge_profile_photo);
        imageMap.put("/assets/app/images/game/badges/profile_photo_off.png", R.drawable.game_badge_profile_photo_off);
        imageMap.put("/assets/app/images/game/badges/profile_info.png", R.drawable.game_badge_profile_info);
        imageMap.put("/assets/app/images/game/badges/profile_info_off.png", R.drawable.game_badge_profile_info_off);
        imageMap.put("/assets/app/images/game/badges/like_1.png", R.drawable.game_badge_like_1);
        imageMap.put("/assets/app/images/game/badges/like_1_off.png", R.drawable.game_badge_like_1_off);
        imageMap.put("/assets/app/images/game/badges/like_10.png", R.drawable.game_badge_like_10);
        imageMap.put("/assets/app/images/game/badges/like_10_off.png", R.drawable.game_badge_like_10_off);
        imageMap.put("/assets/app/images/game/badges/follow_1.png", R.drawable.game_badge_follow_1);
        imageMap.put("/assets/app/images/game/badges/follow_1_off.png", R.drawable.game_badge_follow_1_off);
        imageMap.put("/assets/app/images/game/badges/follow_10.png", R.drawable.game_badge_follow_10);
        imageMap.put("/assets/app/images/game/badges/follow_10_off.png", R.drawable.game_badge_follow_10_off);
        imageMap.put("/assets/app/images/game/badges/post_1.png", R.drawable.game_badge_post_1);
        imageMap.put("/assets/app/images/game/badges/post_1_off.png", R.drawable.game_badge_post_1_off);
        imageMap.put("/assets/app/images/game/badges/post_10.png", R.drawable.game_badge_post_10);
        imageMap.put("/assets/app/images/game/badges/post_10_off.png", R.drawable.game_badge_post_10_off);
    }

    private ImageMapping() {}

    public static int map(String url) {
        Integer icon = imageMap.get(url);
        if (icon == null)
            return -1;
        return icon;
    }

    public static void insertEmoticon(EmoticonVM emoticon, TextView textView) {
        String code = emoticon.getCode();
        String text = (textView.getText() == null)? "" : textView.getText().toString();
        if (!StringUtils.isEmpty(text) && !text.endsWith(" "))
            code = " " + code;
        code += " ";
        textView.append(code);
    }
}
