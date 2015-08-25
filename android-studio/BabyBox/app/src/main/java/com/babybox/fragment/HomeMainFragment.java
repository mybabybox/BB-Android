package com.babybox.fragment;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.GameActivity;
import com.babybox.activity.MainActivity;
import com.babybox.activity.NewPostActivity;
import com.babybox.app.TrackedFragment;
import com.babybox.app.UserInfoCache;
import com.babybox.util.ImageUtil;

public class HomeMainFragment extends TrackedFragment {

    private RelativeLayout profileLayout;
    private ImageView profileImage;
    private TextView usernameText;
    private ImageView signInImage;
    private ImageView newPostIcon;
    private View actionBarView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.home_main_fragement, container, false);

        actionBarView = inflater.inflate(R.layout.home_main_actionbar, null);

        ActionBar.LayoutParams lp = new ActionBar.LayoutParams(ActionBar.LayoutParams.MATCH_PARENT, ActionBar.LayoutParams.MATCH_PARENT);
        getActivity().getActionBar().setCustomView(actionBarView, lp);
        getActivity().getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActivity().getActionBar().show();

        setHasOptionsMenu(true);

        profileLayout = (RelativeLayout) actionBarView.findViewById(R.id.profileLayout);
        profileImage = (ImageView) actionBarView.findViewById(R.id.profileImage);
        usernameText = (TextView) actionBarView.findViewById(R.id.usernameText);

        newPostIcon = (ImageView) actionBarView.findViewById(R.id.newPostIcon);
        signInImage = (ImageView) actionBarView.findViewById(R.id.signInImage);

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                AnimationUtil.rotateBackForthOnce(mascotIcon);
            }
        }, 2000);
        */

        init();

        // profile
        ImageUtil.displayThumbnailProfileImage(UserInfoCache.getUser().getId(), profileImage);
        usernameText.setText(UserInfoCache.getUser().getDisplayName());

        profileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = MainActivity.getInstance();
                if (mainActivity != null) {
                    mainActivity.pressProfileTab();
                }
            }
        });

        signInImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch game
                Intent intent = new Intent(HomeMainFragment.this.getActivity(), GameActivity.class);
                startActivity(intent);
            }
        });

        newPostIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // launch new post page with no comm id, user will select
                Intent intent = new Intent(HomeMainFragment.this.getActivity(), NewPostActivity.class);
                intent.putExtra("id",0L);
                intent.putExtra("flag","FromCommActivity");
                startActivity(intent);
            }
        });

        return view;
    }

    private void init() {
        Bundle bundle = new Bundle();
        bundle.putString("key","feed");
        MyCommunityNewsfeedListFragement fragment = new MyCommunityNewsfeedListFragement();
        fragment.setTrackedOnce();
        FragmentManager fragmentManager = getChildFragmentManager();
        fragment.setArguments(bundle);
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        //fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.children_fragement, fragment);
        fragmentTransaction.commit();
    }
}

