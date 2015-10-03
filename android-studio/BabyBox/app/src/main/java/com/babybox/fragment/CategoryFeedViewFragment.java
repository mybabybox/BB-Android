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
import com.babybox.util.FeedFilter;
import com.babybox.util.ImageMapping;
import com.babybox.util.ImageUtil;
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

    private FeedFilter.FeedType feedType;
    private FeedFilter.FeedProductType productType;

    private ImageView backImage, whatsappAction, copyLinkAction, newPostAction;

    private Button popularFilterButton, newestFilterButton, priceLowHighFilterButton, priceHighLowFilterButton;
    private Button newFilterButton, usedFilterButton, allFilterButton;

    private FrameLayout tipsLayout;
    private ImageView dismissTipsButton;

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
        copyLinkAction = (ImageView) actionBarView.findViewById(R.id.copyLinkAction);
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

        copyLinkAction.setOnClickListener(new View.OnClickListener() {
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
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, catId);
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
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_POPULAR);
            }
        });
        newestFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_NEWEST);
            }
        });
        priceLowHighFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_PRICE_LOW_HIGH);
            }
        });
        priceHighLowFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectFeedFilter(FeedFilter.FeedType.CATEGORY_PRICE_HIGH_LOW);
            }
        });
        selectFeedFilter(FeedFilter.FeedType.CATEGORY_POPULAR, false);

        allFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.FeedProductType.ALL);
            }
        });
        newFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.FeedProductType.NEW);
            }
        });
        usedFilterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectProductFilter(FeedFilter.FeedProductType.USED);
            }
        });
        selectProductFilter(FeedFilter.FeedProductType.ALL, false);

        // tips
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.CATEGORY_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
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
        catId = getArguments().getLong(ViewUtil.BUNDLE_KEY_ID, -1L);
        setCategory(catId);
        /*
        if (!getArguments().getString(ViewUtil.BUNDLE_KEY_SOURCE).equals("FromDetailActivity")) {
            catId = getArguments().getLong(ViewUtil.BUNDLE_KEY_ID, -1L);
            setCategory();
        } else {
            getCategory(getArguments().getLong(ViewUtil.BUNDLE_KEY_ID, -1L));
        }
        */

        return view;
    }

    private void selectFeedFilter(FeedFilter.FeedType feedType) {
        selectFeedFilter(feedType, true);
    }

    private void selectFeedFilter(FeedFilter.FeedType feedType, boolean loadFeed) {
        if (FeedFilter.FeedType.CATEGORY_POPULAR.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(popularFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(popularFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_NEWEST.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(newestFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(newestFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_PRICE_LOW_HIGH.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(priceLowHighFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(priceLowHighFilterButton);
        }

        if (FeedFilter.FeedType.CATEGORY_PRICE_HIGH_LOW.equals(feedType)) {
            ViewUtil.selectFilterButtonStyle(priceHighLowFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(priceHighLowFilterButton);
        }

        setFeedType(feedType);

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    getFeedType(),
                    getProductType(),
                    catId));
        }
    }

    private void selectProductFilter(FeedFilter.FeedProductType productType) {
        selectProductFilter(productType, true);
    }

    private void selectProductFilter(FeedFilter.FeedProductType productType, boolean loadFeed) {
        if (FeedFilter.FeedProductType.ALL.equals(productType)) {
            ViewUtil.selectFilterButtonStyle(allFilterButton);
        } else {
            ViewUtil.unselectFilterButtonStyle(allFilterButton);
        }

        if (FeedFilter.FeedProductType.NEW.equals(productType)) {
            ViewUtil.selectFilterButtonStyle(newFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectFilterButtonStyle(newFilterButton);
        }

        if (FeedFilter.FeedProductType.USED.equals(productType)) {
            ViewUtil.selectFilterButtonStyle(usedFilterButton);
            if (loadFeed) {

            }
        } else {
            ViewUtil.unselectFilterButtonStyle(usedFilterButton);
        }

        setProductType(productType);

        if (loadFeed) {
            reloadFeed(new FeedFilter(
                    getFeedType(),
                    getProductType(),
                    catId));
        }
    }

    private void setCategory(Long catId) {
        category = CategoryCache.getCategory(catId);
        catNameText.setText(category.name);
        catDescText.setText(category.description);
        int resId = ImageMapping.map(category.getIcon());
        if (resId != -1) {
            catImage.setImageDrawable(getResources().getDrawable(resId));
        } else {
            Log.d(this.getClass().getSimpleName(), "initLayout: cat=" + category.getName() + " load image from background - " + category.getIcon());
            ImageUtil.displayImage(category.getIcon(), catImage);
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

    public FeedFilter.FeedType getFeedType() {
        return feedType;
    }

    public void setFeedType(FeedFilter.FeedType feedType) {
        this.feedType = feedType;
    }

    public FeedFilter.FeedProductType getProductType() {
        return productType;
    }

    public void setProductType(FeedFilter.FeedProductType productType) {
        this.productType = productType;
    }
}