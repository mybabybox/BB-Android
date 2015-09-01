package com.babybox.fragment;

import android.os.Bundle;
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

import java.util.List;

public class HomeCategoryPagerFragment extends TrackedFragment {

    private RelativeLayout cat1, cat2, cat3, cat4, cat5, cat6, cat7, cat8;
    private ImageView image1, image2, image3, image4, image5, image6, image7, image8;
    private TextView name1, name2, name3, name4, name5, name6, name7, name8;

    private List<CategoryVM> categories;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.home_category_pager_fragment, container, false);

        cat1 = (RelativeLayout) view.findViewById(R.id.cat1);
        image1 = (ImageView) view.findViewById(R.id.image1);
        name1 = (TextView) view.findViewById(R.id.name1);

        cat2 = (RelativeLayout) view.findViewById(R.id.cat2);
        image2 = (ImageView) view.findViewById(R.id.image2);
        name2 = (TextView) view.findViewById(R.id.name2);

        cat3 = (RelativeLayout) view.findViewById(R.id.cat3);
        image3 = (ImageView) view.findViewById(R.id.image3);
        name3 = (TextView) view.findViewById(R.id.name3);

        cat4 = (RelativeLayout) view.findViewById(R.id.cat4);
        image4 = (ImageView) view.findViewById(R.id.image4);
        name4 = (TextView) view.findViewById(R.id.name4);

        cat5 = (RelativeLayout) view.findViewById(R.id.cat5);
        image5 = (ImageView) view.findViewById(R.id.image5);
        name5 = (TextView) view.findViewById(R.id.name5);

        cat6 = (RelativeLayout) view.findViewById(R.id.cat6);
        image6 = (ImageView) view.findViewById(R.id.image6);
        name6 = (TextView) view.findViewById(R.id.name6);

        /*
        cat7 = (RelativeLayout) view.findViewById(R.id.cat7);
        image7 = (ImageView) view.findViewById(R.id.image7);
        name7 = (TextView) view.findViewById(R.id.name7);

        cat8 = (RelativeLayout) view.findViewById(R.id.cat8);
        image8 = (ImageView) view.findViewById(R.id.image8);
        name8 = (TextView) view.findViewById(R.id.name8);
        */

        initLayout(0, cat1, image1, name1);
        initLayout(1, cat2, image2, name2);
        initLayout(2, cat3, image3, name3);
        initLayout(3, cat4, image4, name4);
        initLayout(4, cat5, image5, name5);
        initLayout(5, cat6, image6, name6);
        //initLayout(6, cat7, image7, name7);
        //initLayout(7, cat8, image8, name8);

        return view;
    }

    public void initLayout(final int index, final View catLayout, final ImageView image, final TextView name) {

        if (categories == null || index >= categories.size()) {
            catLayout.setVisibility(View.GONE);
            return;
        }

        // set layout

        final CategoryVM item = categories.get(index);

        name.setText(item.getName());
        image.setImageDrawable(getActivity().getResources().getDrawable(ImageMapping.map(item.getIcon())));

        catLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startCategoryActivity(getActivity(), item, "FromHomeCategoryPagerFragment");
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public List<CategoryVM> getCategories() {
        return categories;
    }

    public void setCategories(List<CategoryVM> categories) {
        this.categories = categories;
    }
}
