package com.babybox.app;

import android.util.Log;

import com.babybox.util.SharedPreferencesUtil;
import com.babybox.viewmodel.SettingVM;
import com.babybox.viewmodel.UserVM;
import retrofit.Callback;
import retrofit.RetrofitError;

public class UserInfoCache {

    private static UserVM userInfo;

    private static boolean skippedAndroidUpgrade = false;

    private UserInfoCache() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<UserVM> userCallback) {
        refresh(AppController.getInstance().getSessionId(), userCallback);
    }

    /**
     * For login screen
     * @param sessionId
     * @param userCallback
     */
    public static void refresh(final String sessionId, final Callback<UserVM> userCallback) {
        Log.d(UserInfoCache.class.getSimpleName(), "refresh");

        AppController.getApiService().getUser(sessionId, new Callback<UserVM>() {
            @Override
            public void success(UserVM userVM, retrofit.client.Response response) {
                userInfo = userVM;
                SharedPreferencesUtil.getInstance().saveUserInfo(userVM);
                if (userCallback != null) {
                    userCallback.success(userVM, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (userCallback != null) {
                    userCallback.failure(error);
                }
                Log.e(UserInfoCache.class.getSimpleName(), "refresh.api.getUserInfo: failure", error);
            }
        });
    }

    public static UserVM getUser() {
        if (userInfo == null)
            userInfo = SharedPreferencesUtil.getInstance().getUserInfo();
        return userInfo;
    }

    public static void skipAndroidUpgrade() {
        skippedAndroidUpgrade = true;
    }

    public static boolean requestAndroidUpgrade() {
        if (skippedAndroidUpgrade) {
            return false;
        }

        SettingVM setting = getUser().setting;
        if (setting == null) {
            return false;
        }

        Log.d(UserInfoCache.class.getSimpleName(), "requestAndroidUpgrade: client version="+AppController.getVersionCode());
        Log.d(UserInfoCache.class.getSimpleName(), "requestAndroidUpgrade: system version="+setting.systemAndroidVersion);

        try {
            int systemAndroidVersion = Integer.valueOf(setting.systemAndroidVersion);
            if (AppController.getVersionCode() < systemAndroidVersion) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    public static void clear() {
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.USER_INFO);
    }
}
