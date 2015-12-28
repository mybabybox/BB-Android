package com.babybox.app;

import android.util.Log;

import com.babybox.util.SharedPreferencesUtil;
import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.CountryVM;

import java.util.ArrayList;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CountryCache {
    private static final String TAG = CountryCache.class.getName();

    public static final String COUNTRY_CODE_NA = "NA";

    private static List<CountryVM> countries = new ArrayList<>();

    private CountryCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<List<CountryVM>> callback) {
        Log.d(TAG, "refresh");

        AppController.getApiService().getCountries(new Callback<List<CountryVM>>() {
            @Override
            public void success(List<CountryVM> vms, Response response) {
                if (vms == null || vms.size() == 0)
                    return;

                countries = vms;
                SharedPreferencesUtil.getInstance().saveCountries(vms);
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

    public static List<CountryVM> getCountries() {
        if (countries == null || countries.size() == 0)
            countries = SharedPreferencesUtil.getInstance().getCountries();
        return countries;
    }

    public static CountryVM getCountryWithName(String name) {
        for (CountryVM country : CountryCache.getCountries()) {
            if (country.name.equals(name)) {
                return country;
            }
        }
        return null;
    }

    public static CountryVM getCountryWithCode(String code) {
        for (CountryVM country : CountryCache.getCountries()) {
            if (country.code.equals(code)) {
                return country;
            }
        }
        return null;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.COUNTRIES);
    }
}
