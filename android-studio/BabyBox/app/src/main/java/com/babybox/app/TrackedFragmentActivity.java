package com.babybox.app;

import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.babybox.R;
import com.babybox.activity.MainActivity;
import com.google.analytics.tracking.android.EasyTracker;

public abstract class TrackedFragmentActivity extends FragmentActivity {

    protected EasyTracker tracker;

    protected boolean tracked = true;

    protected EasyTracker getTracker() {
        if (tracker == null)
            tracker = EasyTracker.getInstance(this);
        return tracker;
    }

    public void setTracked(boolean tracked) {
        this.tracked = tracked;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStart");
        if (tracked) {
            getTracker().activityStart(this);
        }
        if (MainActivity.getInstance() != null) {
            MainActivity.getInstance().resetControls();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(this.getClass().getSimpleName(), "[DEBUG] activityStop");
        if (tracked) {
            getTracker().activityStop(this);
        }
    }

    //
    // UI helper
    //

    protected void setActionBarTitle(String title) {
        TextView titleText = (TextView) findViewById(R.id.toolbarTitleText);
        if (titleText != null) {
            titleText.setText(title);
        }

        /*
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            View actionBarView = actionBar.getCustomView();
            TextView titleText = (TextView) actionBarView.findViewById(R.id.toolbarTitleText);
            if (titleText != null) {
                titleText.setText(title);
            }
        }
        */
    }

    protected void showActionBarTitle(boolean show) {
        TextView titleText = (TextView) findViewById(R.id.toolbarTitleText);
        if (titleText != null) {
            titleText.setVisibility(show? View.VISIBLE : View.GONE);
        }

    }
}
