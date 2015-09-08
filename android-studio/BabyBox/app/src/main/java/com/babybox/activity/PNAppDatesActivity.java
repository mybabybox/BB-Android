package com.babybox.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import com.babybox.R;
import com.babybox.adapter.PNBookmarkListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNAppDatesActivity extends TrackedFragmentActivity {

    private static final String TAG = PNAppDatesActivity.class.getName();

    private ListView appDateList;
    private PNBookmarkListAdapter appDateListAdapter;
    private List<PreNurseryVM> allSchoolVMList;
    private List<PreNurseryVM> hkSchoolVMList;
    private List<PreNurseryVM> klSchoolVMList;
    private List<PreNurseryVM> ntSchoolVMList;

    private int selected = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.schools_app_dates_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.schools_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.MATCH_PARENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );
        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));

        TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitle);
        actionbarTitle.setText(getString(R.string.schools_application_date));

        appDateList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                PreNurseryVM vm = appDateListAdapter.getItem(i);
                Intent intent = new Intent(PNAppDatesActivity.this, PNCommunityActivity.class);
                intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID,vm.getCommId());
                intent.putExtra(ViewUtil.BUNDLE_KEY_ID, vm.getId());
                startActivity(intent);
            }
        });

        ImageView backImage = (ImageView) findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        getSchoolAppDates();
    }

    private void getSchoolAppDates(){
        AppController.getApi().getPNAppDates(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                Log.d(PNAppDatesActivity.this.getClass().getSimpleName(), "getSchoolAppDates.api.getPNAppDates: "+vms.size());
                allSchoolVMList = vms;
                appDateListAdapter = new PNBookmarkListAdapter(PNAppDatesActivity.this, allSchoolVMList);
                appDateList.setAdapter(appDateListAdapter);
                appDateListAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Log.e(PNAppDatesActivity.class.getSimpleName(), "getSchoolAppDates: failure", error);
            }
        });
    }
}

