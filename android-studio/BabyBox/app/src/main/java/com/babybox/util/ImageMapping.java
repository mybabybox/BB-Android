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

        imageMap.put("/assets/app/images/category/cat_beauty.png", R.drawable.cat_beauty);
        imageMap.put("/assets/app/images/category/cat_books.png", R.drawable.cat_books);
        imageMap.put("/assets/app/images/category/cat_clothes.png", R.drawable.cat_clothes);
        imageMap.put("/assets/app/images/category/cat_food.png", R.drawable.cat_food);
        imageMap.put("/assets/app/images/category/cat_home.png", R.drawable.cat_home);
        //imageMap.put("/assets/app/images/category/cat_other.png", R.drawable.cat_other);
        //imageMap.put("/assets/app/images/category/cat_preg.png", R.drawable.cat_preg);
        //imageMap.put("/assets/app/images/category/cat_toys.png", R.drawable.cat_toys);
        //imageMap.put("/assets/app/images/category/cat_utils.png", R.drawable.cat_utils);

        // Emoticons
        imageMap.put("/assets/app/images/emoticons/angel.png", R.drawable.emo_angel);
        imageMap.put("/assets/app/images/emoticons/bad.png", R.drawable.emo_bad);
        imageMap.put("/assets/app/images/emoticons/blush.png", R.drawable.emo_blush);
        imageMap.put("/assets/app/images/emoticons/cool.png", R.drawable.emo_cool);
        imageMap.put("/assets/app/images/emoticons/cry.png", R.drawable.emo_cry);
        imageMap.put("/assets/app/images/emoticons/dry.png", R.drawable.emo_dry);
        imageMap.put("/assets/app/images/emoticons/frown.png", R.drawable.emo_frown);
        imageMap.put("/assets/app/images/emoticons/gasp.png", R.drawable.emo_gasp);
        imageMap.put("/assets/app/images/emoticons/grin.png", R.drawable.emo_grin);
        imageMap.put("/assets/app/images/emoticons/happy.png", R.drawable.emo_happy);
        imageMap.put("/assets/app/images/emoticons/huh.png", R.drawable.emo_huh);
        imageMap.put("/assets/app/images/emoticons/laugh.png", R.drawable.emo_laugh);
        imageMap.put("/assets/app/images/emoticons/love.png", R.drawable.emo_love);
        imageMap.put("/assets/app/images/emoticons/mad.png", R.drawable.emo_mad);
        imageMap.put("/assets/app/images/emoticons/ohmy.png", R.drawable.emo_ohmy);
        imageMap.put("/assets/app/images/emoticons/ok.png", R.drawable.emo_ok);
        imageMap.put("/assets/app/images/emoticons/smile.png", R.drawable.emo_smile);
        imageMap.put("/assets/app/images/emoticons/teat.png", R.drawable.emo_teat);
        imageMap.put("/assets/app/images/emoticons/teeth.png", R.drawable.emo_teeth);
        imageMap.put("/assets/app/images/emoticons/tongue.png", R.drawable.emo_tongue);
        imageMap.put("/assets/app/images/emoticons/wacko.png", R.drawable.emo_wacko);
        imageMap.put("/assets/app/images/emoticons/wink.png", R.drawable.emo_wink);
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
