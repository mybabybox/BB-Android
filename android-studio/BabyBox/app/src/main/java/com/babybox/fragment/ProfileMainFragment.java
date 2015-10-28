package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.babybox.app.UserInfoCache;
import com.babybox.util.DefaultValues;
import com.babybox.util.ViewUtil;

import java.lang.reflect.Field;

import com.babybox.R;
import com.babybox.app.TrackedFragment;

public class ProfileMainFragment extends TrackedFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        View view = inflater.inflate(R.layout.child_layout_view, container, false);

        Bundle bundle = new Bundle();
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_TYPE, DefaultValues.DEFAULT_USER_FEED_TYPE.name());
        bundle.putString(ViewUtil.BUNDLE_KEY_FEED_PRODUCT_TYPE, DefaultValues.DEFAULT_FEED_PRODUCT_TYPE.name());
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, UserInfoCache.getUser().getId());

        TrackedFragment fragment = new MyProfileFeedViewFragment();
        fragment.setArguments(bundle);
        fragment.setTrackedOnce();
        FragmentTransaction transaction = getChildFragmentManager().beginTransaction();
        transaction.replace(R.id.childLayout, fragment, "profile").commit();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        // TODO: add back notification code
        /*
        NotificationCache.refresh(new Callback<NotificationsParentVM>() {
            @Override
            public void success(NotificationsParentVM notificationsParentVM, Response response) {
                // update notification count
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
        */
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (getChildFragmentManager() != null && getChildFragmentManager().getFragments() != null) {
            for (Fragment fragment : getChildFragmentManager().getFragments()) {
                if (fragment != null)
                    fragment.onActivityResult(requestCode, resultCode, data);
            }
        }
    }
}
