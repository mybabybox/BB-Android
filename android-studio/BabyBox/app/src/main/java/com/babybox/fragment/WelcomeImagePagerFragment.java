package com.babybox.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.TrackedFragment;

public class WelcomeImagePagerFragment extends TrackedFragment {

    private ImageView image;

    private int src;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.welcome_image_pager_fragment, container, false);

        image = (ImageView) view.findViewById(R.id.image);
        image.setImageResource(src);

        return view;
    }

    public void setImageSource(int src) {
        this.src = src;
    }
}
