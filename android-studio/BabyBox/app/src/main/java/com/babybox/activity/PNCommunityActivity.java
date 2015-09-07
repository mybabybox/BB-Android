package com.babybox.activity;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.fragment.PNCommunityFragment;
import com.babybox.util.SharingUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.PreNurseryVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class PNCommunityActivity extends TrackedFragmentActivity {
    private ImageView whatsappAction,bookmarkAction,newPostAction,backImage;
    private Boolean isBookmarked;
    private PreNurseryVM schoolVM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.child_layout_view);

        getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getActionBar().setCustomView(getLayoutInflater().inflate(R.layout.pn_community_actionbar, null),
                new ActionBar.LayoutParams(
                        ActionBar.LayoutParams.WRAP_CONTENT,
                        ActionBar.LayoutParams.MATCH_PARENT,
                        Gravity.CENTER
                )
        );

        whatsappAction = (ImageView) findViewById(R.id.whatsappAction);
        bookmarkAction = (ImageView) findViewById(R.id.bookmarkAction);
        newPostAction = (ImageView) findViewById(R.id.newPostIcon);
        backImage = (ImageView) findViewById(R.id.backImage);

        getSchoolInfo(getIntent().getLongExtra("id", 0L));

        // actionbar actions...
        whatsappAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharingUtil.shareToWhatapp(schoolVM, PNCommunityActivity.this);
            }
        });

        bookmarkAction.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isBookmarked) {
                    bookmark(schoolVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                    isBookmarked=true;
                }else {
                    unbookmark(schoolVM.getId());
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                    isBookmarked=false;
                }
            }
        });

        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void bookmark(Long id) {
        AppController.getApi().bookmarkPN(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void unbookmark(Long id) {
        AppController.getApi().unbookmarkPN(id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                bookmarkAction.setImageResource(R.drawable.ic_bookmark);
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }

    private void getSchoolInfo(Long id) {
        ViewUtil.showSpinner(this);
        AppController.getApi().getPNInfo(id, AppController.getInstance().getSessionId(), new Callback<PreNurseryVM>() {
            @Override
            public void success(PreNurseryVM vm, Response response) {
                schoolVM = vm;
                isBookmarked = schoolVM.isBookmarked();
                if (isBookmarked) {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmarked);
                } else {
                    bookmarkAction.setImageResource(R.drawable.ic_bookmark);
                }

                initFragment();

                ViewUtil.stopSpinner(PNCommunityActivity.this);
            }

            @Override
            public void failure(RetrofitError error) {
                ViewUtil.stopSpinner(PNCommunityActivity.this);
                Log.e(PNCommunityActivity.class.getSimpleName(), "getSchoolInfo: failure", error);
            }
        });
    }

    private void initFragment() {
        Bundle bundle = new Bundle();
        bundle.putLong(ViewUtil.BUNDLE_KEY_CATEGORY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_CATEGORY_ID, 0L));
        bundle.putLong(ViewUtil.BUNDLE_KEY_ID, getIntent().getLongExtra(ViewUtil.BUNDLE_KEY_ID, 0L));
        bundle.putString(ViewUtil.BUNDLE_KEY_SOURCE, getIntent().getStringExtra(ViewUtil.BUNDLE_KEY_SOURCE));

        PNCommunityFragment fragment = new PNCommunityFragment();
        fragment.setSchool(schoolVM);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragment.setArguments(bundle);
        fragmentTransaction.replace(R.id.childLayout, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}