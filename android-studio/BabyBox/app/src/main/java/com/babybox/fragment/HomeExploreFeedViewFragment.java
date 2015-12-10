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

    private AdaptiveViewPager catPager;
    private HomeCategoryPagerAdapter catPagerAdapter;
    private LinearLayout dotsLayout;
    private List<ImageView> dots = new ArrayList<>();

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        catPager = (AdaptiveViewPager) getHeaderView(inflater).findViewById(R.id.catPager);
        dotsLayout = (LinearLayout) getHeaderView(inflater).findViewById(R.id.dotsLayout);

        int pageMargin = ViewUtil.getRealDimension(0);
        catPager.setPageMargin(pageMargin);

        // init adapter
        catPagerAdapter = new HomeCategoryPagerAdapter(getChildFragmentManager());
        catPager.setAdapter(catPagerAdapter);

        // home slider
        homeSlider = (SliderLayout)getHeaderView(inflater).findViewById(R.id.homeSlider);
        initSlider();

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

        catPager.setCurrentItem(0);

        // pager indicator
        ViewUtil.addDots(getActivity(), catPagerAdapter.getCount(), dotsLayout, dots, catPager);

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

                        PagerIndicator indicator = homeSlider.getPagerIndicator();
                        indicator.setDefaultIndicatorColor(getResources().getColor(R.color.pink), getResources().getColor(R.color.light_gray_2));
                        homeSlider.setCustomIndicator(indicator);
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

    @Override
    protected void onScrollUp() {
        MainActivity.getInstance().showBottomMenuBar(true);
    }

    @Override
    protected void onScrollDown() {
        MainActivity.getInstance().showBottomMenuBar(false);
    }
}

class HomeCategoryPagerAdapter extends FragmentStatePagerAdapter {
    private static final String TAG = HomeCategoryPagerAdapter.class.getName();

    public static final int CATEGORIES_PER_PAGE = 9;

    private String title;

    public HomeCategoryPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return title;
    }

    @Override
    public int getCount() {
        int count = (int) Math.ceil((double) CategoryCache.getCategories().size() / (double) CATEGORIES_PER_PAGE);
        return count;
    }

    @Override
    public Fragment getItem(int position) {
        Log.d(TAG, "getItem: item - " + position);
        switch (position) {
            default: {
                HomeCategoryPagerFragment fragment = new HomeCategoryPagerFragment();
                //fragment.setTrackedOnce();
                fragment.setPage(position);
                return fragment;
            }
        }
    }

    /**
     * HACK... returns POSITION_NONE will refresh pager more frequent than needed... but works in this case
     * http://stackoverflow.com/questions/12510404/reorder-pages-in-fragmentstatepageradapter-using-getitempositionobject-object
     *
     * @param object
     * @return
     */
    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    public static List<CategoryVM> getCategoriesForPosition(int position) {
        int start = position * CATEGORIES_PER_PAGE;
        int end = start + CATEGORIES_PER_PAGE;

        List<CategoryVM> categories = CategoryCache.getCategories();
        if (start >= categories.size()) {
            Log.e(TAG, "getCategoriesForPage: position out of bound... position="+position+" categories.size="+categories.size());
            return null;
        }

        if (end >= categories.size()) {
            end = categories.size();
        }

        return categories.subList(start, end);
    }
}

