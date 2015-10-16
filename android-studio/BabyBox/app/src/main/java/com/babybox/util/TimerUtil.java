package com.babybox.util;

import android.os.Handler;

/**
 * Created by keithlei on 3/16/15.
 */
public class TimerUtil {

    /**
     * Call handler.postDelayed in finally to fire another call.
     *
     * @param task
     * @param interval
     */
    public static void run(final Task task, final Long interval) {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {

            @Override
            public void run() {
                try {
                    task.run();
                } catch (Exception e) {

                } finally {
                    handler.postDelayed(this, interval);
                }
            }
        };
        handler.postDelayed(runnable, interval);
    }

    /**
     * Timer task to be executed.
     */
    public interface Task {
        void run();
    }
}
