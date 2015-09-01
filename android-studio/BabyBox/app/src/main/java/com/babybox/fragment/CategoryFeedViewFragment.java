package com.babybox.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.activity.NewPostActivity;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.util.ImageMapping;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.SharingUtil;
import com.babybox.util.UrlUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CategoryVM;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryFeedViewFragment extends FeedViewFragment {

    private static final String TAG = CategoryFeedViewFragment.class.getName();

    private ImageView catImage;
    private TextView catNameText, catDescText;

    private CategoryVM category;
    private Long catId;

    private ImageView backImage, whatsappAction, linkCopyAction, newPostAction;

    private Button popularFilterButton, newestFilterButton, priceLowHighFilterButton, priceHighLowFilterButton;
    private Button newFilterButton, usedFilterButton, allFilterButton;

    private FrameLayout tipsLayout;
    private ImageView cancelTipsButton;

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
                SharingUtil.shareToWhatapp(category, getActivity());
            }
        });

        linkCopyAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ViewUtil.copyToClipboard(UrlUtil.createCategoryUrl(category))) {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.url_copy_success), Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getActivity(), getActivity().getString(R.string.url_copy_failed), Toast.LENGTH_SHORT).show();
                }
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

        // feed type filters
        popularFilterButton = (Button) headerView.findViewById(R.id.popularFilterButton);
        newestFilterButton = (Button) headerView.findViewById(R.id.newestFilterButton);
        priceLowHighFilterButton = (Button) headerView.findViewById(R.id.priceLowHighFilterButton);
        priceHighLowFilterButton = (Button) headerView.findViewById(R.id.priceHighLowFilterButton);

        newFilterButton = (Button) headerView.findViewById(R.id.newFilterButton);
        usedFilterButton = (Button) headerView.findViewById(R.id.usedFilterButton);
        allFilterButton = (Button) headerView.findViewById(R.id.allFilterButton);

        popularFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(ViewUtil.FeedType.CATEGORY_POPULAR);
            }
        });
        newestFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(ViewUtil.FeedType.CATEGORY_NEWEST);
            }
        });
        priceLowHighFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(ViewUtil.FeedType.CATEGORY_PRICE_LOW_HIGH);
            }
        });
        priceHighLowFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(ViewUtil.FeedType.CATEGORY_PRICE_HIGH_LOW);
            }
        });
        selectFeedFilter(ViewUtil.FeedType.CATEGORY_POPULAR, false);

        allFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(ViewUtil.FeedProductType.ALL);
            }
        });
        newFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(ViewUtil.FeedProductType.NEW);
            }
        });
        usedFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(ViewUtil.FeedProductType.USED);
            }
        });
        selectProductFilter(ViewUtil.FeedProductType.ALL, false);

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
        setCategory(catId);
        /*
        if (!getArguments().getString("flag").equals("FromDetailActivity")) {
            catId = getArguments().getLong("id");
            setCategory();
        } else {
            getCategory(getArguments().getLong("id"));
        }
        */

        return view;
    }

    private void selectFeedFilter(ViewUtil.FeedType feedType) {
        selectFeedFilter(feedType, true);
    }

    private void selectFeedFilter(ViewUtil.FeedType feedType, boolean loadFeed) {
        if (ViewUtil.FeedType.CATEGORY_POPULAR.equals(feedType)) {
            ViewUtil.selectButtonStyle(popularFilterButton);
            if (loadFeed) {
                reloadFeed(ViewUtil.FeedType.CATEGORY_POPULAR);
            }
        } else {
            ViewUtil.unselectButtonStyle(popularFilterButton);
        }

        if (ViewUtil.FeedType.CATEGORY_NEWEST.equals(feedType)) {
            ViewUtil.selectButtonStyle(newestFilterButton);
            if (loadFeed) {
                reloadFeed(ViewUtil.FeedType.CATEGORY_NEWEST);
            }
        } else {
            ViewUtil.unselectButtonStyle(newestFilterButton);
        }

        if (ViewUtil.FeedType.CATEGORY_PRICE_LOW_HIGH.equals(feedType)) {
            ViewUtil.selectButtonStyle(priceLowHighFilterButton);
            if (loadFeed) {
                reloadFeed(ViewUtil.FeedType.CATEGORY_PRICE_LOW_HIGH);
            }
        } else {
            ViewUtil.unselectButtonStyle(priceLowHighFilterButton);
        }

        if (ViewUtil.FeedType.CATEGORY_PRICE_HIGH_LOW.equals(feedType)) {
            ViewUtil.selectButtonStyle(priceHighLowFilterButton);
            if (loadFeed) {
                reloadFeed(ViewUtil.FeedType.CATEGORY_PRICE_HIGH_LOW);
            }
        } else {
            ViewUtil.unselectButtonStyle(priceHighLowFilterButton);
        }
    }

    private void selectProductFilter(ViewUtil.FeedProductType productType) {
        selectProductFilter(productType, true);
    }

    private void selectProductFilter(ViewUtil.FeedProductType productType, boolean loadFeed) {
        if (ViewUtil.FeedProductType.ALL.equals(productType)) {
            ViewUtil.selectButtonStyle(allFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectButtonStyle(allFilterButton);
        }

        if (ViewUtil.FeedProductType.NEW.equals(productType)) {
            ViewUtil.selectButtonStyle(newFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectButtonStyle(newFilterButton);
        }

        if (ViewUtil.FeedProductType.USED.equals(productType)) {
            ViewUtil.selectButtonStyle(usedFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectButtonStyle(usedFilterButton);
        }
    }

    private void setCategory(Long catId) {
        category = null;
        for (CategoryVM cat : CategoryCache.getCategories()) {
            if (cat.getId().equals(catId)) {
                Log.d(this.getClass().getSimpleName(), "onCreateView: set currentCategory [catId=" + catId + "] [category=" + cat.name + "|" + cat.getId() + "]");
                category = cat;
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
                setCategory(catId);
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(CategoryFeedViewFragment.class.getSimpleName(), "getCategory: failure", error);
            }
        });
    }
}