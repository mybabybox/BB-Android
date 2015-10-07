package com.babybox.view;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Fix ViewPager WRAP_CONTENT - http://stackoverflow.com/a/20784791
 * Get height of the biggest child
 */
public class AdaptiveViewPager extends ViewPager {

    private static final int DEFAULT_OFFSCREEN_PAGE_LIMIT = 1;

    public AdaptiveViewPager(Context context) {
        super(context);
        setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGE_LIMIT);
    }

    public AdaptiveViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOffscreenPageLimit(DEFAULT_OFFSCREEN_PAGE_LIMIT);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int height = 0;
        for(int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            child.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED));
            int h = child.getMeasuredHeight();
            if(h > height) height = h;
        }

        heightMeasureSpec = MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY);

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}