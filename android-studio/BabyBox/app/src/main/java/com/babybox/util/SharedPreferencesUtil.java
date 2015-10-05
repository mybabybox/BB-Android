package com.babybox.util;

import android.content.Context;
import android.content.SharedPreferences;

import com.babybox.viewmodel.CategoryVM;
import com.babybox.viewmodel.ConversationVM;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

import com.babybox.app.AppController;
import com.babybox.viewmodel.EmoticonVM;
import com.babybox.viewmodel.GameAccountVM;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.UserVM;

/**
 * Created by keithlei on 3/16/15.
 */
public class SharedPreferencesUtil {
    public static final String TAG = SharedPreferencesUtil.class.getSimpleName();
    public static final String PREFS = "prefs";

    public static final String FB_ACCESS_TOKEN = "access_token";
    public static final String FB_ACCESS_EXPIRES = "access_expires";
    public static final String SESSION_ID = "sessionID";
    public static final String USER_INFO = "userInfo";
    public static final String GAME_ACCOUNT = "gameAccount";
    public static final String DISTRICTS = "districts";
    public static final String EMOTICONS = "emoticons";
    public static final String CATEGORIES = "categories";
    public static final String CONVERSATIONS = "conversations";

    public enum Screen {
        HOME_TAB,
        SEARCH_TAB,
        PROFILE_TAB,

        HOME_EXPLORE_TIPS,
        HOME_TRENDING_TIPS,
        HOME_FOLLOWING_TIPS,
        CATEGORY_TIPS,
        MY_PROFILE_TIPS,

        // OBSOLETE...
        MY_NEWSFEED_TIPS
    }

    private static SharedPreferencesUtil instance = null;
    private SharedPreferences prefs;

    private SharedPreferencesUtil() {
        this.prefs = AppController.getInstance().getSharedPreferences(PREFS, Context.MODE_PRIVATE);
    }

    public static SharedPreferencesUtil getInstance() {
        if(instance == null)
            instance = new SharedPreferencesUtil();
        return instance;
    }

    //
    // Save
    //

    public void setScreenViewed(Screen screen) {
        saveBoolean(screen.name(), true);
    }

    public void saveUserInfo(UserVM userInfo) {
        this.saveObject(USER_INFO, userInfo);
    }

    public void saveGameAccount(GameAccountVM gameAccount) {
        this.saveObject(GAME_ACCOUNT, gameAccount);
    }

    public void saveDistricts(List<LocationVM> districts) {
        this.saveObject(DISTRICTS, districts);
    }

    public void saveEmoticons(List<EmoticonVM> emoticons) {
        this.saveObject(EMOTICONS, emoticons);
    }

    public void saveCategories(List<CategoryVM> categories) {
        this.saveObject(CATEGORIES, categories);
    }

    public void saveConversations(List<ConversationVM> conversations) {
        this.saveObject(CONVERSATIONS, conversations);
    }

    public void saveString(String key, String value) {
        this.prefs.edit().putString(key, value).commit();
    }

    public void saveLong(String key, Long value) {
        this.prefs.edit().putLong(key, value).commit();
    }

    public void saveBoolean(String key, Boolean value) {
        this.prefs.edit().putBoolean(key, value).commit();
    }

    private void saveObject(String key, Object obj) {
        String json = new Gson().toJson(obj);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] saveObject: key="+key+" json="+json);
        this.prefs.edit().putString(key, json).commit();
    }

    //
    // Get
    //

    public Boolean isScreenViewed(Screen screen) {
        return getBoolean(screen.name());
    }

    public UserVM getUserInfo() {
        String json = this.prefs.getString(USER_INFO, null);
        UserVM userInfo = new Gson().fromJson(json, UserVM.class);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getUserInfo: json="+json);
        return userInfo;
    }

    public GameAccountVM getGameAccount() {
        String json = this.prefs.getString(GAME_ACCOUNT, null);
        GameAccountVM gameAccount = new Gson().fromJson(json, GameAccountVM.class);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getGameAccount: json="+json);
        return gameAccount;
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

    public List<ConversationVM> getConversations() {
        Type type = new TypeToken<List<ConversationVM>>() {}.getType();
        String json = this.prefs.getString(CONVERSATIONS, null);
        List<ConversationVM> conversations = new Gson().fromJson(json, type);
        //Log.d(this.getClass().getSimpleName(), "[DEBUG] getConversations: size="+conversations.size());
        return conversations;
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
        this.prefs.edit().remove(key).commit();
    }

    public void clearAll() {
        this.prefs.edit().clear().commit();
    }
}
