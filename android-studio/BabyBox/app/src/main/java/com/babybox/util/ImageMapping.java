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

        imageMap.put("/assets/app/images/general/icons/community/rainbow.png", R.drawable.cat_preg);
        imageMap.put("/assets/app/images/general/icons/community/stroller.png", R.drawable.cat_toys);
        imageMap.put("/assets/app/images/general/icons/community/beans.png", R.drawable.cat_clothes);
        imageMap.put("/assets/app/images/general/icons/community/mom.png", R.drawable.cat_books);
        imageMap.put("/assets/app/images/general/icons/community/grad_hat.png", R.drawable.cat_beauty);
        imageMap.put("/assets/app/images/general/icons/community/shirt.png", R.drawable.cat_other);

        // Community icons
        imageMap.put("/assets/app/images/general/icons/community/ball.png", R.drawable.ci_ball);
        imageMap.put("/assets/app/images/general/icons/community/balloons.png", R.drawable.ci_balloons);
        imageMap.put("/assets/app/images/general/icons/community/bath.png", R.drawable.ci_bath);
        imageMap.put("/assets/app/images/general/icons/community/bean_blue.png", R.drawable.ci_bean_blue);
        imageMap.put("/assets/app/images/general/icons/community/bean_green.png", R.drawable.ci_bean_green);
        imageMap.put("/assets/app/images/general/icons/community/bean_orange.png", R.drawable.ci_bean_orange);
        imageMap.put("/assets/app/images/general/icons/community/bean_red.png", R.drawable.ci_bean_red);
        imageMap.put("/assets/app/images/general/icons/community/bean_yellow.png", R.drawable.ci_bean_yellow);
        //imageMap.put("/assets/app/images/general/icons/community/beans.png", R.drawable.ci_beans);
        imageMap.put("/assets/app/images/general/icons/community/bed.png", R.drawable.ci_bed);
        imageMap.put("/assets/app/images/general/icons/community/book.png", R.drawable.ci_book);
        imageMap.put("/assets/app/images/general/icons/community/bottle.png", R.drawable.ci_bottle);
        imageMap.put("/assets/app/images/general/icons/community/boy.png", R.drawable.ci_boy);
        imageMap.put("/assets/app/images/general/icons/community/camera.png", R.drawable.ci_camera);
        imageMap.put("/assets/app/images/general/icons/community/cat.png", R.drawable.ci_cat);
        imageMap.put("/assets/app/images/general/icons/community/cloud.png", R.drawable.ci_cloud);
        imageMap.put("/assets/app/images/general/icons/community/dad.png", R.drawable.ci_dad);
        imageMap.put("/assets/app/images/general/icons/community/feedback.png", R.drawable.ci_feedback);
        imageMap.put("/assets/app/images/general/icons/community/gift_box.png", R.drawable.ci_gift_box);
        imageMap.put("/assets/app/images/general/icons/community/girl.png", R.drawable.ci_girl);
        //imageMap.put("/assets/app/images/general/icons/community/grad_hat.png", R.drawable.ci_grad_hat);
        imageMap.put("/assets/app/images/general/icons/community/helmet.png", R.drawable.ci_helmet);
        imageMap.put("/assets/app/images/general/icons/community/home.png", R.drawable.ci_home);
        imageMap.put("/assets/app/images/general/icons/community/icecream.png", R.drawable.ci_icecream);
        imageMap.put("/assets/app/images/general/icons/community/loc_area.png", R.drawable.ci_loc_area);
        imageMap.put("/assets/app/images/general/icons/community/loc_city.png", R.drawable.ci_loc_city);
        imageMap.put("/assets/app/images/general/icons/community/loc_district.png", R.drawable.ci_loc_district);
        //imageMap.put("/assets/app/images/general/icons/community/mom.png", R.drawable.ci_mom);
        imageMap.put("/assets/app/images/general/icons/community/music_note.png", R.drawable.ci_music_note);
        imageMap.put("/assets/app/images/general/icons/community/palette.png", R.drawable.ci_palette);
        imageMap.put("/assets/app/images/general/icons/community/plane.png", R.drawable.ci_plane);
        //imageMap.put("/assets/app/images/general/icons/community/rainbow.png", R.drawable.ci_rainbow);
        //imageMap.put("/assets/app/images/general/icons/community/shirt.png", R.drawable.ci_shirt);
        imageMap.put("/assets/app/images/general/icons/community/shopping_bag.png", R.drawable.ci_shopping_bag);
        imageMap.put("/assets/app/images/general/icons/community/spoon_fork.png", R.drawable.ci_spoon_fork);
        imageMap.put("/assets/app/images/general/icons/community/sports.png", R.drawable.ci_sports);
        //imageMap.put("/assets/app/images/general/icons/community/stroller.png", R.drawable.ci_stroller);
        imageMap.put("/assets/app/images/general/icons/community/sun.png", R.drawable.ci_sun);
        imageMap.put("/assets/app/images/general/icons/community/teddy.png", R.drawable.ci_teddy);
        imageMap.put("/assets/app/images/general/icons/zodiac/dog.png", R.drawable.ci_z_dog);
        imageMap.put("/assets/app/images/general/icons/zodiac/dragon.png", R.drawable.ci_z_dragon);
        imageMap.put("/assets/app/images/general/icons/zodiac/goat.png", R.drawable.ci_z_goat);
        imageMap.put("/assets/app/images/general/icons/zodiac/horse.png", R.drawable.ci_z_horse);
        imageMap.put("/assets/app/images/general/icons/zodiac/monkey.png", R.drawable.ci_z_monkey);
        imageMap.put("/assets/app/images/general/icons/zodiac/ox.png", R.drawable.ci_z_ox);
        imageMap.put("/assets/app/images/general/icons/zodiac/pig.png", R.drawable.ci_z_pig);
        imageMap.put("/assets/app/images/general/icons/zodiac/rabbit.png", R.drawable.ci_z_rabbit);
        imageMap.put("/assets/app/images/general/icons/zodiac/rat.png", R.drawable.ci_z_rat);
        imageMap.put("/assets/app/images/general/icons/zodiac/rooster.png", R.drawable.ci_z_rooster);
        imageMap.put("/assets/app/images/general/icons/zodiac/snake.png", R.drawable.ci_z_snake);
        imageMap.put("/assets/app/images/general/icons/zodiac/tiger.png", R.drawable.ci_z_tiger);

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

        // Game gift images
        imageMap.put("/assets/app/images/game/gifts/mannings.jpg", R.drawable.game_gift_mannings);
        imageMap.put("/assets/app/images/game/gifts/mannings-thumb.jpg", R.drawable.game_gift_mannings);
        imageMap.put("/assets/app/images/game/gifts/sogo.jpg", R.drawable.game_gift_sogo);
        imageMap.put("/assets/app/images/game/gifts/sogo-thumb.jpg", R.drawable.game_gift_sogo);
        imageMap.put("/assets/app/images/game/gifts/yata.jpg", R.drawable.game_gift_yata);
        imageMap.put("/assets/app/images/game/gifts/yata-thumb.jpg", R.drawable.game_gift_yata);
        imageMap.put("/assets/app/images/game/gifts/wellcome.jpg", R.drawable.game_gift_wellcome);
        imageMap.put("/assets/app/images/game/gifts/wellcome-thumb.jpg", R.drawable.game_gift_wellcome);
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
