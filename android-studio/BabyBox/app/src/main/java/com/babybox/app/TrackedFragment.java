package com.babybox.app;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.Fields;
import com.google.analytics.tracking.android.MapBuilder;

public abstract class TrackedFragment extends Fragment {

    protected boolean tracked = false;

    protected boolean trackedOnce = false;

    protected EasyTracker tracker;

    public void setTracked() {
        this.tracked = true;
    }

    /**
     * Set in leaf fragment e.g. main activity->pager->fragment->list fragment (setTrackedOnce)
     */
    public void setTrackedOnce() {
        this.trackedOnce = true;
    }

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(getActivity());
        return tracker;
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // NOTE: init tracker here
        getTracker();
    }

    /**
     * https://developers.google.com/analytics/devguides/collection/android/v3/migration
     */
    @Override
    public void onResume() {
        super.onResume();

        if (tracked || trackedOnce) {
            trackFragmentShow();
            trackedOnce = false;
        }
    }

    protected void trackFragmentShow() {
        Log.d(this.getClass().getCanonicalName(), "[DEBUG] fragment show");
        getTracker().set(Fields.SCREEN_NAME, getClass().getCanonicalName());
        getTracker().send(MapBuilder.createAppView().build());
    }

    //
    // UI helper
    //

    protected void setActionBarTitle(String title) {
        if (getActivity() instanceof TrackedFragmentActivity) {
            TrackedFragmentActivity activity = (TrackedFragmentActivity) getActivity();
            activity.setActionBarTitle(title);
        }
    }

    /**
     * Checked by MainActivity
     *
     * @return
     */
    public boolean allowBackPressed() {
        return true;
    }
}
