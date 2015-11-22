package com.babybox.app;

import android.util.Log;

import com.babybox.activity.MainActivity;
import com.babybox.util.TimerUtil;
import com.babybox.viewmodel.NotificationCounterVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NotificationCounter implements TimerUtil.Task {

    public static final Long TIMER_INTERVAL = 10 * 60 * 1000L;  // 10 mins

    private static NotificationCounterVM counter;

    private static NotificationCounter mInstance;

    public static synchronized NotificationCounter getInstance() {
        if (mInstance == null)
            mInstance = new NotificationCounter();
        return mInstance;
    }

    private NotificationCounter() {}

    static {
        init();
    }

    private static void init() {
        TimerUtil.run(getInstance(), TIMER_INTERVAL);
    }

    public static void refresh() {
        refresh(new Callback<NotificationCounterVM>() {
            @Override
            public void success(NotificationCounterVM vm, Response response) {
                if (MainActivity.getInstance() != null) {
                    MainActivity.getInstance().refreshNotifications();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(NotificationCounter.class.getSimpleName(), "onStart: NotificationCounter.refresh: failure", error);
            }
        });
    }

    public static void refresh(final Callback<NotificationCounterVM> callback) {
        Log.d(NotificationCounter.class.getSimpleName(), "refresh");

        AppController.getApiService().getNotificationCounter(new Callback<NotificationCounterVM>() {
            @Override
            public void success(NotificationCounterVM vm, Response response) {
                if (vm == null || sameCounter(vm))
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

    public static void resetActivitiesCount() {
        if (counter != null) {
            counter.activitiesCount = 0L;
        }

        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().refreshNotifications();
        }
    }

    private static boolean sameCounter(NotificationCounterVM other) {
        if (counter == null || other == null) {
            return false;
        }
        return counter.userId == other.userId &&
                counter.activitiesCount == other.activitiesCount &&
                counter.conversationsCount == other.conversationsCount;
    }

    /**
     * Implements TimerUtil.Task
     */
    @Override
    public void run() {
        refresh();
    }
}
