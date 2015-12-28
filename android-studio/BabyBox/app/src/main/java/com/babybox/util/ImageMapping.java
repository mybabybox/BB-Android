package com.babybox.util;

import java.util.HashMap;
import java.util.Map;

import com.babybox.R;

/**
 * Created by keithlei on 3/16/15.
 */
public class ImageMapping {

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
        imageMap.put("/assets/app/images/game/badges/like_3.png", R.drawable.game_badge_like_3);
        imageMap.put("/assets/app/images/game/badges/like_3_off.png", R.drawable.game_badge_like_3_off);
        imageMap.put("/assets/app/images/game/badges/like_10.png", R.drawable.game_badge_like_10);
        imageMap.put("/assets/app/images/game/badges/like_10_off.png", R.drawable.game_badge_like_10_off);
        imageMap.put("/assets/app/images/game/badges/follow_1.png", R.drawable.game_badge_follow_1);
        imageMap.put("/assets/app/images/game/badges/follow_1_off.png", R.drawable.game_badge_follow_1_off);
        imageMap.put("/assets/app/images/game/badges/follow_3.png", R.drawable.game_badge_follow_3);
        imageMap.put("/assets/app/images/game/badges/follow_3_off.png", R.drawable.game_badge_follow_3_off);
        imageMap.put("/assets/app/images/game/badges/follow_10.png", R.drawable.game_badge_follow_10);
        imageMap.put("/assets/app/images/game/badges/follow_10_off.png", R.drawable.game_badge_follow_10_off);
        imageMap.put("/assets/app/images/game/badges/post_1.png", R.drawable.game_badge_post_1);
        imageMap.put("/assets/app/images/game/badges/post_1_off.png", R.drawable.game_badge_post_1_off);
        imageMap.put("/assets/app/images/game/badges/post_10.png", R.drawable.game_badge_post_10);
        imageMap.put("/assets/app/images/game/badges/post_10_off.png", R.drawable.game_badge_post_10_off);
        imageMap.put("/assets/app/images/country/intl.png", R.drawable.ct_intl);
        imageMap.put("/assets/app/images/country/au.png", R.drawable.ct_au);
        imageMap.put("/assets/app/images/country/ca.png", R.drawable.ct_ca);
        imageMap.put("/assets/app/images/country/ch.png", R.drawable.ct_ch);
        imageMap.put("/assets/app/images/country/de.png", R.drawable.ct_de);
        imageMap.put("/assets/app/images/country/dk.png", R.drawable.ct_dk);
        imageMap.put("/assets/app/images/country/es.png", R.drawable.ct_es);
        imageMap.put("/assets/app/images/country/fr.png", R.drawable.ct_fr);
        imageMap.put("/assets/app/images/country/gb.png", R.drawable.ct_gb);
        imageMap.put("/assets/app/images/country/hk.png", R.drawable.ct_hk);
        imageMap.put("/assets/app/images/country/id.png", R.drawable.ct_id);
        imageMap.put("/assets/app/images/country/ie.png", R.drawable.ct_ie);
        imageMap.put("/assets/app/images/country/in.png", R.drawable.ct_in);
        imageMap.put("/assets/app/images/country/is.png", R.drawable.ct_is);
        imageMap.put("/assets/app/images/country/it.png", R.drawable.ct_it);
        imageMap.put("/assets/app/images/country/jp.png", R.drawable.ct_jp);
        imageMap.put("/assets/app/images/country/kr.png", R.drawable.ct_kr);
        imageMap.put("/assets/app/images/country/my.png", R.drawable.ct_my);
        imageMap.put("/assets/app/images/country/nl.png", R.drawable.ct_nl);
        imageMap.put("/assets/app/images/country/no.png", R.drawable.ct_no);
        imageMap.put("/assets/app/images/country/nz.png", R.drawable.ct_nz);
        imageMap.put("/assets/app/images/country/se.png", R.drawable.ct_se);
        imageMap.put("/assets/app/images/country/th.png", R.drawable.ct_th);
        imageMap.put("/assets/app/images/country/tw.png", R.drawable.ct_tw);
        imageMap.put("/assets/app/images/country/us.png", R.drawable.ct_us);
    }

    private ImageMapping() {}

    public static int map(String url) {
        Integer icon = imageMap.get(url);
        if (icon == null)
            return -1;
        return icon;
    }

}
