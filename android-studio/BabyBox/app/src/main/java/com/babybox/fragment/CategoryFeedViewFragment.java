package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.NewPostActivity;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.util.ImageMapping;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.SharingUtil;
import com.babybox.viewmodel.CategoryVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryFeedViewFragment extends FeedViewFragment {

    private static final String TAG = CategoryFeedViewFragment.class.getName();

    private ImageView catImage;
    private TextView catNameText, catDescText;
    private CategoryVM currentCategory;
    private Long catId;

    private ImageView backImage, whatsappAction, linkCopyAction, newPostAction;

    private FrameLayout tipsLayout;
    private ImageView cancelTipsButton;

    private View headerView;

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.category_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        // action bar
        View actionBarView = getActivity().getActionBar().getCustomView();
        backImage = (ImageView) actionBarView.findViewById(R.id.backImage);
        whatsappAction = (ImageView) actionBarView.findViewById(R.id.whatsappAction);
        linkCopyAction = (ImageView) actionBarView.findViewById(R.id.linkCopyAction);
        newPostAction = (ImageView) actionBarView.findViewById(R.id.newPostAction);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().onBackPressed();
            }
        });

        whatsappAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtil.shareToWhatapp(currentCategory, getActivity());
            }
        });

        linkCopyAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //ViewUtil.copyToClipboard();
            }
        });

        newPostAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewPostActivity.class);
                intent.putExtra("id", catId);
                startActivity(intent);
            }
        });

        // tips
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.CATEGORY_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            cancelTipsButton = (ImageView) headerView.findViewById(R.id.cancelTipsButton);
            cancelTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.CATEGORY_TIPS);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }

        // header
        catImage = (ImageView) getHeaderView(inflater).findViewById(R.id.catImage);
        catNameText = (TextView) getHeaderView(inflater).findViewById(R.id.catNameText);
        catDescText = (TextView) getHeaderView(inflater).findViewById(R.id.catDescText);

        // init
        catId = getArguments().getLong("id");
        setCurrentCategory(catId);
        /*
        if (!getArguments().getString("flag").equals("FromDetailActivity")) {
            catId = getArguments().getLong("id");
            setCurrentCategory();
        } else {
            getCategory(getArguments().getLong("id"));
        }
        */

        return view;
    }

    private void setCurrentCategory(Long catId) {
        currentCategory = null;
        for (CategoryVM category : CategoryCache.getCategories()) {
            if (category.getId().equals(catId)) {
                Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCategory [catId=" + catId + "] [category=" + category.name + "|" + category.getId() + "]");
                currentCategory = category;

                catNameText.setText(category.name);
                catDescText.setText(category.desc);
                catImage.setImageDrawable(getResources().getDrawable(ImageMapping.map(category.icon)));
                break;
            }
        }
    }

    private void getCategory(Long id) {
        AppController.getApiService().getCategory(id, new Callback<CategoryVM>() {
            @Override
            public void success(CategoryVM categoryVM, Response response) {
                catId = categoryVM.getId();
                setCurrentCategory(catId);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CategoryFeedViewFragment.class.getSimpleName(), "getCategory: failure", error);
            }
        });
    }
}