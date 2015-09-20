package com.babybox.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.app.TrackedFragment;
import com.babybox.util.ImageMapping;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.List;

public class HomeCategoryPagerFragment extends TrackedFragment {

    private int[] catLayoutIds = { R.id.cat1, R.id.cat2, R.id.cat3, R.id.cat4, R.id.cat5, R.id.cat6, R.id.cat7, R.id.cat8, R.id.cat9 };
    private int[] imageIds = { R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5, R.id.image6, R.id.image7, R.id.image8, R.id.image9 };
    private int[] nameIds = { R.id.name1, R.id.name2, R.id.name3, R.id.name4, R.id.name5, R.id.name6, R.id.name7, R.id.name8, R.id.name9 };
    private List<RelativeLayout> catLayouts;
    private List<ImageView> images;
    private List<TextView> names;

    private List<CategoryVM> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_category_pager_fragment, container, false);

        catLayouts = new ArrayList<>();
        images = new ArrayList<>();
        names = new ArrayList<>();

        for (int i = 0; i < catLayoutIds.length; i++) {
            catLayouts.add((RelativeLayout) view.findViewById(catLayoutIds[i]));
            images.add((ImageView) view.findViewById(imageIds[i]));
            names.add((TextView) view.findViewById(nameIds[i]));
        }

        initLayout();

        return view;
    }

    private void initLayout() {
        for (int i = 0; i < catLayoutIds.length; i++) {
            RelativeLayout catLayout = catLayouts.get(i);
            ImageView image = images.get(i);
            TextView name = names.get(i);

            if (i < categories.size()) {
                final CategoryVM category = categories.get(i);
                name.setText(category.getName());
                int resId = ImageMapping.map(category.getIcon());
                if (resId != -1) {
                    image.setImageDrawable(getActivity().getResources().getDrawable(resId));
                }

                catLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ViewUtil.startCategoryActivity(getActivity(), category, "FromHomeCategoryPagerFragment");
                    }
                });
                catLayout.setVisibility(View.VISIBLE);
            } else {
                catLayout.setVisibility(View.GONE);
            }
        }
    }

    public List<CategoryVM> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryVM> categories) {
        this.categories = categories;
    }
}
