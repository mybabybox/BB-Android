package com.babybox.util;

import android.app.Activity;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.List;

public class CategorySelectorViewUtil {
    private static final String TAG = CategorySelectorViewUtil.class.getName();

    public static final int DEFAULT_NUM_CATS_PER_ROW = 3;

    private int[] catsRowLayoutIds;
    private int[] catLayoutIds;
    private int[] imageIds;
    private int[] nameIds;

    private int numCatsPerRow;

    private View view;
    private Activity activity;
    private List<CategoryVM> categories;

    public CategorySelectorViewUtil(
            List<CategoryVM> categories,
            int[] catsRowLayoutIds, int[] catLayoutIds, int[] imageIds, int[] nameIds,
            View view, Activity activity) {
        this.view = view;
        this.activity = activity;
        this.categories = categories;
        this.catsRowLayoutIds = catsRowLayoutIds;
        this.catLayoutIds = catLayoutIds;
        this.imageIds = imageIds;
        this.nameIds = nameIds;
        setNumCategoriesPerRow(DEFAULT_NUM_CATS_PER_ROW);
    }

    public void setNumCategoriesPerRow(int numCatsPerRow) {
        this.numCatsPerRow = numCatsPerRow;
    }

    public void initLayout() {
        List<ViewGroup> catLayouts = new ArrayList<>();
        List<ImageView> images = new ArrayList<>();
        List<TextView> names = new ArrayList<>();

        // init views
        for (int i = 0; i < catLayoutIds.length; i++) {
            catLayouts.add((ViewGroup) view.findViewById(catLayoutIds[i]));
            images.add((ImageView) view.findViewById(imageIds[i]));
            names.add((TextView) view.findViewById(nameIds[i]));
        }

        // show/hide rows
        int maxRows = (int) Math.ceil((double)categories.size() / numCatsPerRow);
        for (int i = 0; i < catsRowLayoutIds.length; i++) {
            ViewGroup catsRowLayout = (ViewGroup) view.findViewById(catsRowLayoutIds[i]);
            Log.d(TAG, "cat.size="+categories.size()+" numCatsPerRow="+numCatsPerRow+" maxRows="+maxRows+" i="+i);
            if (maxRows > i) {
                catsRowLayout.setVisibility(View.VISIBLE);
            } else {
                catsRowLayout.setVisibility(View.GONE);
            }
        }

        // init cat layouts
        for (int i = 0; i < catLayoutIds.length; i++) {
            ViewGroup catLayout = catLayouts.get(i);
            ImageView image = images.get(i);
            TextView name = names.get(i);
            if (i < categories.size()) {
                final CategoryVM category = categories.get(i);
                initCategoryLayout(category, catLayout, image, name);
            } else {
                catLayout.setVisibility(View.INVISIBLE);
            }
        }
    }

    private void initCategoryLayout(
            final CategoryVM category, ViewGroup catLayout, ImageView catImage, TextView nameText) {

        nameText.setText(category.getName());
        ImageUtil.displayImage(category.getIcon(), catImage);

        catLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startCategoryActivity(activity, category.getId());
            }
        });
        catLayout.setVisibility(View.VISIBLE);
    }
}