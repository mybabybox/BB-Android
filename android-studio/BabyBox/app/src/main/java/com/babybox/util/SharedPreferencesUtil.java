package com.babybox.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.babybox.R;
import com.babybox.viewmodel.CategoryVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.babybox.app.AppController;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.UserVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharedPreferencesUtil {
    public static final String TAG = SharedPreferencesUtil.class.getName();
    public static final String PREFS = AppController.getInstance().getString(R.string.app_name)+"-prefs";

    public static final String FB_ACCESS_TOKEN = "access_token";
    public static final String FB_ACCESS_EXPIRES = "access_expires";
    public static final String LOGIN_FAILED_COUNT = "loginFailedCount";
    public static final String SESSION_ID = "sessionId";
    public static final String GCM_KEY = "gcmKey";
    public static final String USER_INFO = "userInfo";
    public static final String DISTRICTS = "districts";
    public static final String EMOTICONS = "emoticons";
    public static final String CATEGORIES = "categories";
    public static final String APP_VERSION = "appVersion";

    public enum Screen {
        HOME_TAB,
        SEARCH_TAB,
        PROFILE_TAB,

        HOME_EXPLORE_TIPS,
        HOME_TRENDING_TIPS,
        HOME_FOLLOWING_TIPS,
        CATEGORY_TIPS,
        MY_PROFILE_TIPS,
    }

    private static SharedPreferencesUtil instance = null;
    private SharedPreferences prefs;

    private SharedPreferencesUtil() {
        //this.prefs = AppController.getInstance().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
        this.prefs = PreferenceManager.getDefaultSharedPreferences(AppController.getInstance());
    }

    public static SharedPreferencesUtil getInstance() {
        if (instance == null && AppController.getInstance() != null)
            instance = new SharedPreferencesUtil();
        return instance;
    }

    //
    // Save
    //

    public void setScreenViewed(Screen screen) {
        saveBoolean(screen.name(), true);
    }

    public void saveSessionId(String sessionId) {
        this.saveString(SharedPreferencesUtil.SESSION_ID, sessionId);
        this.clear(SharedPreferencesUtil.LOGIN_FAILED_COUNT);
    }

    public void saveGCMKey(String gcmKey) {
        this.saveString(SharedPreferencesUtil.GCM_KEY, gcmKey);
    }

    public void saveAppVersion(Long appVersion) {
        this.saveLong(SharedPreferencesUtil.APP_VERSION, appVersion);
    }

    public void saveLoginFailedCount(Long count) {
        this.saveLong(SharedPreferencesUtil.LOGIN_FAILED_COUNT, count);
    }

    public void saveUserInfo(UserVM userInfo) {
        if (userInfo == null)
            return;
        this.saveObject(USER_INFO, userInfo);
    }

    public void saveDistricts(List<LocationVM> districts) {
        if (districts == null || districts.size() == 0)
            return;
        this.saveObject(DISTRICTS, districts);
    }

    public void saveEmoticons(List<EmoticonVM> emoticons) {
        if (emoticons == null || emoticons.size() == 0)
            return;
        this.saveObject(EMOTICONS, emoticons);
    }

    public void saveCategories(List<CategoryVM> categories) {
        if (categories == null || categories.size() == 0)
            return;
        this.saveObject(CATEGORIES, categories);
    }

    public void saveString(String key, String value) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString(key, value);
        editor.commit();
    }

    public void saveLong(String key, Long value) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putLong(key, value);
        editor.commit();
    }

    public void saveBoolean(String key, Boolean value) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    private void saveObject(String key, Object obj) {
        String json = new Gson().toJson(obj);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] saveObject: key="+key+" json="+json);
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.putString(key, json);
        editor.commit();
    }

    //
    // Get
    //

    public Boolean isScreenViewed(Screen screen) {
        return getBoolean(screen.name());
    }

    public String getSessionId() {
        return this.getString(SharedPreferencesUtil.SESSION_ID);
    }

    public String getGCMKey() {
        return this.getString(SharedPreferencesUtil.GCM_KEY);
    }

    public Long getAppVersion() {
        return this.getLong(SharedPreferencesUtil.APP_VERSION);
    }

    public Long getLoginFailedCount() {
        return this.getLong(SharedPreferencesUtil.LOGIN_FAILED_COUNT);
    }

    public UserVM getUserInfo() {
        String json = this.prefs.getString(USER_INFO, null);
        UserVM userInfo = new Gson().fromJson(json, UserVM.class);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getUserInfo: json="+json);
        return userInfo;
    }

    public List<LocationVM> getDistricts() {
        Type type = new TypeToken<List<LocationVM>>() {}.getType();
        String json = this.prefs.getString(DISTRICTS, null);
        List<LocationVM> districts = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getDistricts: size="+districts.size());
        return districts;
    }

    public List<EmoticonVM> getEmoticons() {
        Type type = new TypeToken<List<EmoticonVM>>() {}.getType();
        String json = this.prefs.getString(EMOTICONS, null);
        List<EmoticonVM> emoticons = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getEmoticons: size="+emoticons.size());
        return emoticons;
    }

    public List<CategoryVM> getCategories() {
        Type type = new TypeToken<List<CategoryVM>>() {}.getType();
        String json = this.prefs.getString(CATEGORIES, null);
        List<CategoryVM> categories = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getCategories: size="+categories.size());
        return categories;
    }

    public String getString(String key) {
        return this.prefs.getString(key, null);
    }

    public Long getLong(String key) {
        return this.prefs.getLong(key, 0L);
    }

    public Boolean getBoolean(String key) {
        return this.prefs.getBoolean(key, false);
    }

    public void clear(String key) {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.remove(key);
        editor.commit();
    }

    public void clearAll() {
        SharedPreferences.Editor editor = this.prefs.edit();
        editor.clear();
        editor.commit();
    }
}
