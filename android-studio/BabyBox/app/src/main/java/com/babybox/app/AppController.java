package com.babybox.app;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
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
import com.babybox.activity.LoginActivity;
import com.babybox.mock.MockApi;
import com.babybox.util.ImageUtil;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.viewmodel.LocationVM;
import com.babybox.viewmodel.MessageVM;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;

import org.acra.ReportField;
import org.acra.ReportingInteractionMode;
import org.acra.annotation.ReportsCrashes;

import java.security.MessageDigest;
import java.util.List;

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

    public enum DeviceType {
        NA,
        ANDROID,
        IOS,
        WEB,
        WAP
    }

    public static final String TAG = AppController.class.getSimpleName();
    public static String BASE_URL;

    private static AppController mInstance;
    private static BabyBoxService apiService;
    private static MockApi mockApi;

    private long conversationId;
    public List<MessageVM> messageVMList;

    public long getConversationId() {
        return conversationId;
    }

    public void setConversationId(long conversationId) {
        this.conversationId = conversationId;
    }

    public static synchronized AppController getInstance() {
        return mInstance;
    }

    public static synchronized MockApi getMockApi() {
        return mockApi;
    }

    public static synchronized BabyBoxService getApiService() {
        return apiService;
    }

    public static synchronized boolean isUserAdmin() {
        return UserInfoCache.getUser().isAdmin();
    }

    public static synchronized LocationVM getUserLocation() {
        return UserInfoCache.getUser().getLocation();
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

    public static void init() {
        BASE_URL = getInstance().getString(R.string.base_url);

        if (mockApi == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setClient(new OkClient()).build();
            mockApi = restAdapter.create(MockApi.class);
        }

        if (apiService == null) {
            RestAdapter restAdapter = new RestAdapter.Builder()
                    .setEndpoint(BASE_URL)
                    .setClient(new OkClient()).build();
            BabyBoxApi api = restAdapter.create(BabyBoxApi.class);
            apiService = new BabyBoxService(api);
        }

        ImageUtil.init();

        initStaticCaches();

        //ACRA.init(getInstance());
    }

    public static void initStaticCaches() {
        DistrictCache.refresh();
        CategoryCache.refresh();
        //EmoticonCache.refresh();
    }

    /**
     * Exit app. Clear everything.
     */
    public void clearAll() {
        Log.d(this.getClass().getSimpleName(), "clearAll");
        NotificationCache.clear();
        UserInfoCache.clear();
    }

    public void saveSessionId(String sessionId) {
        SharedPreferencesUtil.getInstance().saveString(SharedPreferencesUtil.SESSION_ID, sessionId);
    }

    public String getSessionId() {
        return SharedPreferencesUtil.getInstance().getString(SharedPreferencesUtil.SESSION_ID);
    }

    public void clearPreferences() {
        SharedPreferencesUtil.getInstance().clearAll();
    }

    public void logout() {
        Log.d(this.getClass().getSimpleName(), "logout");

        // clear session and exit
        AppController.getInstance().clearAll();
        AppController.getInstance().clearPreferences();

        // log out from FB
        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();
        /*
        if (AccessToken.getCurrentAccessToken() != null) {
            LoginManager.getInstance().logOut();
        }
        */

        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
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
                Log.d(this.getClass().getSimpleName(), "KeyHash - " + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}