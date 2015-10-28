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

        imageMap.put("/assets/app/images/category/cat_beauty.jpg", R.drawable.cat_beauty);
        imageMap.put("/assets/app/images/category/cat_books.jpg", R.drawable.cat_books);
        imageMap.put("/assets/app/images/category/cat_clothes.jpg", R.drawable.cat_clothes);
        imageMap.put("/assets/app/images/category/cat_food.jpg", R.drawable.cat_food);
        imageMap.put("/assets/app/images/category/cat_home.jpg", R.drawable.cat_home);
        imageMap.put("/assets/app/images/category/cat_other.jpg", R.drawable.cat_other);
        imageMap.put("/assets/app/images/category/cat_preg.jpg", R.drawable.cat_preg);
        imageMap.put("/assets/app/images/category/cat_toys.jpg", R.drawable.cat_toys);
        imageMap.put("/assets/app/images/category/cat_utils.jpg", R.drawable.cat_utils);
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
