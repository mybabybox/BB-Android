package com.babybox.app;

import android.util.Log;

import com.babybox.util.SharedPreferencesUtil;
import com.babybox.viewmodel.CategoryVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CategoryCache {
    private static final String TAG = CategoryCache.class.getName();

    private static List<CategoryVM> categories = new ArrayList<>();

    private CategoryCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<CategoryVM>> callback) {
        Log.d(TAG, "refresh");

        AppController.getApiService().getCategories(new Callback<List<CategoryVM>>() {
            @Override
            public void success(List<CategoryVM> vms, Response response) {
                if (vms == null || vms.size() == 0)
                    return;

                categories = vms;
                SharedPreferencesUtil.getInstance().saveCategories(vms);
                if (callback != null) {
                    callback.success(vms, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(TAG, "refresh: failure", error);
            }
        });
    }

    public static List<CategoryVM> getCategories() {
        if (categories == null || categories.size() == 0)
            categories = SharedPreferencesUtil.getInstance().getCategories();
        return categories;
    }

    public static CategoryVM getCategory(Long id) {
        for (CategoryVM cat : CategoryCache.getCategories()) {
            if (cat.getId().equals(id)) {
                return cat;
            }
        }
        return null;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.CATEGORIES);
    }
}
