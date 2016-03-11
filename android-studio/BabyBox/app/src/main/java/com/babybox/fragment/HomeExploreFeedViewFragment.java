package com.babybox.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.babybox.app.AppController;
import com.babybox.app.CategoryCache;
import com.babybox.util.CategorySelectorViewUtil;
import com.babybox.util.DefaultValues;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.view.AdaptiveViewPager;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.FeaturedItemVM;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Transformers.BaseTransformer;
import com.daimajia.slider.library.Transformers.DefaultTransformer;
import com.daimajia.slider.library.Tricks.ViewPagerEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.Transformer;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class HomeExploreFeedViewFragment extends FeedViewFragment {

    private static final String TAG = HomeExploreFeedViewFragment.class.getName();

    private int[] catsRowLayoutIds = { R.id.catsRow1Layout, R.id.catsRow2Layout, R.id.catsRow3Layout, R.id.catsRow4Layout };
    private int[] catLayoutIds = { R.id.cat1, R.id.cat2, R.id.cat3, R.id.cat4, R.id.cat5, R.id.cat6, R.id.cat7, R.id.cat8, R.id.cat9, R.id.cat10, R.id.cat11, R.id.cat12 };
    private int[] imageIds = { R.id.image1, R.id.image2, R.id.image3, R.id.image4, R.id.image5, R.id.image6, R.id.image7, R.id.image8, R.id.image9, R.id.image10, R.id.image11, R.id.image12 };
    private int[] nameIds = { R.id.name1, R.id.name2, R.id.name3, R.id.name4, R.id.name5, R.id.name6, R.id.name7, R.id.name8, R.id.name9, R.id.name10, R.id.name11, R.id.name12 };

    private CategorySelectorViewUtil catSelectorViewUtil;

    private FrameLayout tipsLayout;
    private ImageView dismissTipsButton;

    private SliderLayout homeSlider;

    @Override
    protected View getHeaderView(LayoutInflater inflater) {
        if (headerView == null) {
            headerView = inflater.inflate(R.layout.home_explore_feed_view_header, null);
        }
        return headerView;
    }

    @Override
    public void onResume() {
        super.onResume();

        // home slider
        initSlider();

        // category selector
        initCategorySelector();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        homeSlider = (SliderLayout)getHeaderView(inflater).findViewById(R.id.homeSlider);

        // tips
        tipsLayout = (FrameLayout) headerView.findViewById(R.id.tipsLayout);
        if (SharedPreferencesUtil.getInstance().isScreenViewed(SharedPreferencesUtil.Screen.HOME_EXPLORE_TIPS)) {
            tipsLayout.setVisibility(View.GONE);
        } else {
            tipsLayout.setVisibility(View.VISIBLE);

            dismissTipsButton = (ImageView) headerView.findViewById(R.id.dismissTipsButton);
            dismissTipsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferencesUtil.getInstance().setScreenViewed(SharedPreferencesUtil.Screen.HOME_EXPLORE_TIPS);
                    tipsLayout.setVisibility(View.GONE);
                }
            });
        }

        return view;
    }

    private void initSlider() {
        AppController.getApiService().getHomeSliderFeaturedItems(new Callback<List<FeaturedItemVM>>() {
            @Override
            public void success(List<FeaturedItemVM> featuredItems, Response response) {
                Log.d(TAG, "initSlider: returned " + (featuredItems == null? "null" : featuredItems.size()+" featuredItems"));
                if (!featuredItems.isEmpty()) {
                    homeSlider.setVisibility(View.VISIBLE);

                    for (final FeaturedItemVM featuredItem : featuredItems) {
                        Log.d(TAG, "initSlider: name="+featuredItem.getName()+" image="+featuredItem.getImage());
                        DefaultSliderView sliderView = new DefaultSliderView(getActivity());
                        sliderView
                                .image(featuredItem.getImage())
                                .setScaleType(BaseSliderView.ScaleType.CenterCrop)
                                .setOnSliderClickListener(new BaseSliderView.OnSliderClickListener() {
                                    @Override
                                    public void onSliderClick(BaseSliderView slider) {
                                        ViewUtil.handleFeaturedItemAction(getActivity(), featuredItem);
                                    }
                                });

                        // add your extra information
                        sliderView.bundle(new Bundle());
                        sliderView.getBundle().putString(ViewUtil.BUNDLE_KEY_ACTION_TYPE, featuredItem.destinationType);
                        sliderView.getBundle().putLong(ViewUtil.BUNDLE_KEY_ID, featuredItem.destinationObjId);
                        sliderView.getBundle().putString(ViewUtil.BUNDLE_KEY_NAME, featuredItem.destinationObjName);
                        homeSlider.addSlider(sliderView);
                    }

                    // rotate if more than 1 item
                    if (featuredItems.size() > 1) {
                        homeSlider.setPresetTransformer(SliderLayout.Transformer.Default);
                        homeSlider.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
                        homeSlider.setDuration(DefaultValues.DEFAULT_SLIDER_DURATION);
                        homeSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Visible);

                        // getResources() only if fragment is attached
                        if (isAdded() && getActivity() != null) {
                            PagerIndicator indicator = homeSlider.getPagerIndicator();
                            indicator.setDefaultIndicatorColor(getResources().getColor(R.color.pink), getResources().getColor(R.color.light_gray_2));
                            homeSlider.setCustomIndicator(indicator);
                        }
                    } else {
                        homeSlider.setPagerTransformer(true, new BaseTransformer() {
                            @Override
                            protected void onTransform(View view, float position) {
                                return;
                            }
                        });
                        homeSlider.stopAutoCycle();
                        homeSlider.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);
                    }
                } else {
                    homeSlider.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(TAG, "initSlider: failure", error);
            }
        });
    }

    private void initCategorySelector() {
        List<CategoryVM> categories = CategoryCache.getCategories();
        catSelectorViewUtil = new CategorySelectorViewUtil(
                categories, catsRowLayoutIds, catLayoutIds, imageIds, nameIds, headerView, getActivity());
        catSelectorViewUtil.setNumCategoriesPerRow(3);
        catSelectorViewUtil.initLayout();
    }

    @Override
    protected void onRefreshView() {
        super.onRefreshView();

        initSlider();
        initCategorySelector();
    }

    @Override
    protected void onScrollUp() {
        MainActivity.getInstance().showBottomMenuBar(true);
    }

    @Override
    protected void onScrollDown() {
        MainActivity.getInstance().showBottomMenuBar(false);
    }
}