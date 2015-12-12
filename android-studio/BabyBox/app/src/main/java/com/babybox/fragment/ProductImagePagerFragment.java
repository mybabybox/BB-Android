package com.babybox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.TrackedFragment;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;

public class ProductImagePagerFragment extends TrackedFragment {

    private ImageView image;
    private ImageView soldImage;

    private Long imageId;
    private boolean sold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.product_image_pager_fragment, container, false);

        image = (ImageView) view.findViewById(R.id.image);
        soldImage = (ImageView) view.findViewById(R.id.soldImage);

        ImageUtil.displayOriginalPostImage(imageId, image);

        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ViewUtil.fullscreenImagePopup(getActivity(), ImageUtil.ORIGINAL_POST_IMAGE_BY_ID_URL + imageId);
            }
        });

        if (sold) {
            soldImage.setVisibility(View.VISIBLE);
        } else {
            soldImage.setVisibility(View.INVISIBLE);
        }

        return view;
    }

    public void setImageId(Long imageId) {
        this.imageId = imageId;
    }

    public void setSold(boolean sold) {
        this.sold = sold;
    }
}
