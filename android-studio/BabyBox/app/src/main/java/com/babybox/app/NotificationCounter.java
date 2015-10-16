package com.babybox.app;

import android.util.Log;

import com.babybox.viewmodel.NotificationCounterVM;
import com.babybox.viewmodel.NotificationsParentVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationCounter {

    private static NotificationCounterVM counter;

    private NotificationCounter() {}

    static {
        init();
    }

    private static void init() {
    }

    public static void refresh() {
        refresh(null);
    }

    public static void refresh(final Callback<NotificationCounterVM> callback) {
        Log.d(NotificationCounter.class.getSimpleName(), "refresh");

        AppController.getApiService().getNotificationCounter(new Callback<NotificationCounterVM>() {
            @Override
            public void success(NotificationCounterVM vm, Response response) {
                if (counter == null)
                    return;

                Log.d(NotificationCounter.class.getSimpleName(), "refresh.success: activitiesCount=" + vm.activitiesCount + " conversationsCount=" + vm.conversationsCount);
                counter = vm;

                if (callback != null) {
                    callback.success(counter, response);
                }
            }

            @Override
            public void failure(RetrofitError error) {
                if (callback != null) {
                    callback.failure(error);
                }
                Log.e(NotificationCounter.class.getSimpleName(), "refresh: failure", error);
            }
        });
    }

    public static NotificationCounterVM getCounter() {
        return counter;
    }

    public static void clear() {
        counter = null;
    }
}
