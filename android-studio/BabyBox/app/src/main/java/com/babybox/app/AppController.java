package com.babybox.app;

import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.multidex.MultiDex;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.babybox.util.ImageUtil;
import com.babybox.util.LocationUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.LocationVM;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.acra.ACRA;
import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.security.MessageDigest;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * ARCA config
 * http://www.acra.ch/
 * http://stackoverflow.com/questions/16747673/android-application-cant-compile-find-acra-library-after-import
 */
/*
@ReportsCrashes(
        mailTo = "mybabybox.hk@gmail.com",
        mode = ReportingInteractionMode.TOAST,
        resToastText = R.string.crash_toast_text)
        */
@ReportsCrashes(
        mailTo = "mybabybox.hk@gmail.com",
        mode = ReportingInteractionMode.DIALOG,
        customReportContent = {
                ReportField.BUILD, ReportField.USER_APP_START_DATE, ReportField.USER_CRASH_DATE,
                ReportField.USER_EMAIL, ReportField.APP_VERSION_NAME, ReportField.ANDROID_VERSION,
                ReportField.PHONE_MODEL, ReportField.CUSTOM_DATA, ReportField.STACK_TRACE,
                ReportField.LOGCAT,
        },
        resToastText = R.string.crash_toast_text,
        resDialogText = R.string.crash_dialog_text,
        resDialogIcon = android.R.drawable.ic_dialog_info,
        resDialogTitle = R.string.crash_dialog_title,
        resDialogCommentPrompt = R.string.crash_dialog_comment_prompt,
        resDialogOkToast = R.string.crash_dialog_ok_toast,
        logcatFilterByPid = true)
public class AppController extends Application {

    public static final String TAG = AppController.class.getName();

    public static String APP_NAME;

    public static String BASE_URL;

    private static AppController mInstance;

    private static int versionCode;

    private static String versionName;

    private static BabyBoxService apiService;

    private static boolean crashReportEnabled = false;

    public enum DeviceType {
        NA,
        ANDROID,
        IOS,
        WEB,
        WAP
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static int getVersionCode() {
        return versionCode;
    }

    public static String getVersionName() {
        return versionName;
    }

    public static synchronized BabyBoxService getApiService() {
        return apiService;
    }

    public static synchronized boolean isUserAdmin() {
        if (UserInfoCache.getUser() != null) {
            return UserInfoCache.getUser().isAdmin();
        }
        return false;
    }

    public static synchronized LocationVM getUserLocation() {
        if (UserInfoCache.getUser() != null) {
            return UserInfoCache.getUser().getLocation();
        }
        return null;
    }

    /**
     * http://stackoverflow.com/questions/29344481/why-did-this-happen-how-do-i-fix-this-android-unexpected-top-level-exception
     * https://developer.android.com/tools/building/multidex.html
     * https://developer.android.com/reference/android/support/multidex/MultiDexApplication.html
     *
     * @param base
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mInstance = this;

        init();

        //printKeyHashForFacebook();
    }

    public void saveSessionId(String sessionId) {
        SharedPreferencesUtil.getInstance().saveSessionId(sessionId);
    }

    public String getSessionId() {
        return SharedPreferencesUtil.getInstance().getSessionId();
    }

    public void saveLoginFailedCount(Long count) {
        SharedPreferencesUtil.getInstance().saveLoginFailedCount(count);
    }

    public Long getLoginFailedCount() {
        return SharedPreferencesUtil.getInstance().getLoginFailedCount();
    }

    public void init() {

        initVersion();

        initApiService();

        initStaticCaches();

        ImageUtil.init();

        //LocationUtil.getInstance().getLastKnownLocation(this);

        SharedPreferencesUtil.getInstance();

        if (crashReportEnabled) {
            ACRA.init(getInstance());
        }
    }

    private static String getBaseUrl() {
        if ("dev".equalsIgnoreCase(getInstance().getString(R.string.env))) {
            return getInstance().getString(R.string.base_url_dev);
        }
        return getInstance().getString(R.string.base_url);
    }

    public void initVersion() {
        try {
            PackageInfo packageInfo =
                    getInstance().getPackageManager().getPackageInfo(getInstance().getPackageName(), 0);
            versionCode = packageInfo.versionCode;
            versionName = packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            Log.e(TAG, "Failed to get app version", e);
            throw new RuntimeException(e);
        }
    }

    public void initApiService() {
        APP_NAME = getInstance().getString(R.string.app_name);
        BASE_URL = getBaseUrl();

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BASE_URL)
                .setRequestInterceptor(new RetrofitInterceptor())
                .setClient(new OkClient()).build();
        BabyBoxApi api = restAdapter.create(BabyBoxApi.class);
        apiService = new BabyBoxService(api);
    }

    public void initStaticCaches() {
        DistrictCache.refresh();
        CountryCache.refresh();
        CategoryCache.refresh();
    }

    public void initUserCaches() {
        NotificationCounter.refresh();
    }

    //getLastKnownLocation();

    /**
     * Exit app. Clear everything.
     */
    public void clearAll() {
        clearUserSession();
        clearPreferences();
    }

    public void clearPreferences() {
        SharedPreferencesUtil.getInstance().clearAll();
    }

    public void clearUserCaches() {
        NotificationCounter.clear();
        UserInfoCache.clear();
    }

    public void clearUserSession() {
        clearUserCaches();
        SharedPreferencesUtil.getInstance().clear(SharedPreferencesUtil.SESSION_ID);
    }

    public void logout() {
        Log.d(TAG, "logout");

        // clear session and exit
        clearAll();

        // log out from FB
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        /*
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        */

        if (MainActivity.getInstance() != null) {
            ViewUtil.startWelcomeActivity(MainActivity.getInstance());
        }
    }

    public static boolean isOnline() {
        ConnectivityManager conMgr = (ConnectivityManager) AppController.getInstance().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = conMgr.getActiveNetworkInfo();

        if(netInfo == null || !netInfo.isConnected() || !netInfo.isAvailable()){
            Toast.makeText(AppController.getInstance().getApplicationContext(), AppController.getInstance().getString(R.string.connection_timeout_message), Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void printKeyHashForFacebook() {
        try{
            PackageInfo info = getPackageManager().getPackageInfo("com.babybox.app", PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d(TAG, "KeyHash - " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            Log.e(TAG, "Failed to printKeyHashForFacebook", e);
        }
    }

    /**
     * Append header user-agent to rest api.
     */
    public static class RetrofitInterceptor implements RequestInterceptor {

        public final String USER_AGENT = String.format("Android App %s(%d)", getVersionName(), getVersionCode());

        @Override
        public void intercept(RequestFacade req) {
            //Log.d(TAG, "User-Agent = " + USER_AGENT);
            req.addHeader("User-Agent", USER_AGENT);
        }

    }
}