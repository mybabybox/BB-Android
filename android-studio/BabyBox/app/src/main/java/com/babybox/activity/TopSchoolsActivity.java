package com.babybox.activity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.babybox.R;
import com.babybox.adapter.TopBookmarkedPNListAdapter;
import com.babybox.adapter.TopViewedPNListAdapter;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.AbstractSchoolsListFragment;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TopSchoolsActivity extends TrackedFragmentActivity {
    private ListView topViewedList,topBookmarkedList;
    private TopViewedPNListAdapter topViewedPNListAdapter;
    private TopBookmarkedPNListAdapter topBookmarkedPNListAdapter;
    private List<PreNurseryVM> topViewedPNs,topBookmarkedPNs;

    private ImageView backImage, scrollButton;
    private ScrollView scrollView;
    private boolean scrollUp = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.top_schools_activity);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.schools_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        TextView actionbarTitle = (TextView) findViewById(R.id.actionbarTitle);
        RelativeLayout topViewedLayout = (RelativeLayout) findViewById(R.id.topViewedLayout);
        RelativeLayout topBookmarkedLayout = (RelativeLayout) findViewById(R.id.topBookmarkedLayout);

        if (getIntent().getStringExtra("flag").equals(AbstractSchoolsListFragment.PN_INTENT_FLAG)) {
            getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));
            actionbarTitle.setText(getString(R.string.schools_top_pn_ranking));
            topViewedLayout.setBackgroundResource(R.drawable.schools_pn_edit_text_round);
            topBookmarkedLayout.setBackgroundResource(R.drawable.schools_pn_edit_text_round);
        }

        //topViewedLayout.setPadding(10,10,10,10);
        //topBookmarkedLayout.setPadding(10,10,10,10);

        topViewedPNs = new ArrayList<>();
        topBookmarkedPNs = new ArrayList<>();

        topViewedList = (ListView) findViewById(R.id.topViewedList);
        topBookmarkedList = (ListView) findViewById(R.id.topBookmarkedList);
        backImage = (ImageView) findViewById(R.id.backImage);
        scrollButton = (ImageView) findViewById(R.id.scrollButton);
        scrollView = (ScrollView) findViewById(R.id.scrollView);

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        if (getIntent().getStringExtra("flag").equals(AbstractSchoolsListFragment.PN_INTENT_FLAG)) {
            getTopViewPNs();
            getTopBookmarkPNs();
        }

        topViewedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getIntent().getStringExtra("flag").equals(AbstractSchoolsListFragment.PN_INTENT_FLAG)) {
                    PreNurseryVM vm = topViewedPNListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), PNCommunityActivity.class);
                }
            }
        });

        topBookmarkedList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (getIntent().getStringExtra("flag").equals(AbstractSchoolsListFragment.PN_INTENT_FLAG)) {
                    PreNurseryVM vm = topBookmarkedPNListAdapter.getItem(i);
                    startSchoolActivity(vm.getId(), vm.getCommId(), PNCommunityActivity.class);
                }
            }
        });

        scrollButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scrollUp) {
                    scrollView.fullScroll(View.FOCUS_DOWN);
                    scrollUp = false;
                }else {
                    scrollView.fullScroll(View.FOCUS_UP);
                    scrollUp = true;
                }
            }
        });
    }

    private void startSchoolActivity(Long id, Long commId, Class activityClass) {
        Intent intent = new Intent(TopSchoolsActivity.this, activityClass);
        intent.putExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, commId);
        intent.putExtra(ViewUtil.BUNDLE_KEY_ID, id);
        startActivity(intent);
    }

    private void getTopViewPNs(){
        AppController.getApi().getTopViewedPNs(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topViewedPNs.addAll(vms);
                topViewedPNListAdapter = new TopViewedPNListAdapter(TopSchoolsActivity.this, topViewedPNs);
                topViewedList.setAdapter(topViewedPNListAdapter);
                ViewUtil.setHeightBasedOnChildren(topViewedList);
                ViewUtil.scrollTop(scrollView);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getTopBookmarkPNs(){
        AppController.getApi().getTopBookmarkedPNs(AppController.getInstance().getSessionId(), new Callback<List<PreNurseryVM>>() {
            @Override
            public void success(List<PreNurseryVM> vms, Response response) {
                topBookmarkedPNs.addAll(vms);
                topBookmarkedPNListAdapter = new TopBookmarkedPNListAdapter(TopSchoolsActivity.this, topBookmarkedPNs);
                topBookmarkedList.setAdapter(topBookmarkedPNListAdapter);
                ViewUtil.setHeightBasedOnChildren(topBookmarkedList);
                ViewUtil.scrollTop(scrollView);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}