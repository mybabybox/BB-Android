package com.babybox.util;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.AppController;

/**
 * Created by keithlei on 3/16/15.
 */
public class AnimationUtil {

    public static final Animation rotate = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate);
    public static final Animation rotateBackForth = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate_back_forth);
    public static final Animation rotateBackForthOnce = AnimationUtils.loadAnimation(AppController.getInstance(), R.anim.image_rotate_back_forth_once);

    private static Interpolator accelerator = new AccelerateInterpolator();
    private static Interpolator decelerator = new DecelerateInterpolator();

    private AnimationUtil() {}

    public static void show(View view) {
        view.setVisibility(View.VISIBLE);
        view.bringToFront();
    }

    public static void rotate(View view) {
        show(view);
        view.startAnimation(rotate);
    }

    public static void rotateBackForth(View view) {
        rotateBackForth(view, false);
    }

    public static void rotateBackForthOnce(View view) {
        rotateBackForth(view, true);
    }

    private static void rotateBackForth(View view, boolean once) {
        show(view);
        if (once)
            view.startAnimation(rotateBackForthOnce);
        else
            view.startAnimation(rotateBackForth);
    }

    public static void cancel(View view) {
        if (view.getAnimation() != null) {
            view.getAnimation().cancel();
            view.clearAnimation();
        }
        view.setVisibility(View.GONE);
    }

    public static void flipImage(ImageView fromImage, ImageView toImage) {
        final ImageView visibleList;
        final ImageView invisibleList;

        if (fromImage.getVisibility() == View.GONE) {
            visibleList = toImage;
            invisibleList = fromImage;
        } else {
            invisibleList = toImage;
            visibleList = fromImage;
        }

        ObjectAnimator visToInvis = ObjectAnimator.ofFloat(visibleList, "rotationY", 0f, 90f);
        visToInvis.setDuration(150);
        visToInvis.setInterpolator(accelerator);
        final ObjectAnimator invisToVis = ObjectAnimator.ofFloat(invisibleList, "rotationY", -90f, 0f);

        invisToVis.setDuration(500);
        invisToVis.setInterpolator(decelerator);
        visToInvis.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator anim) {
                visibleList.setVisibility(View.GONE);
                invisToVis.start();
                invisibleList.setVisibility(View.VISIBLE);
            }
        });

        visToInvis.start();
    }
}
