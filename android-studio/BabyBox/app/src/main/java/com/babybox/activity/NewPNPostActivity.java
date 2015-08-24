package com.babybox.activity;

import android.os.Bundle;

import java.util.List;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.viewmodel.CommunitiesParentVM;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class NewPNPostActivity extends NewPostActivity {

    private List<CommunitiesWidgetChildVM> bookmarkedSchoolCommunities;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.actionbar_bg_green));

        selectCommunityText.setText(R.string.new_post_select_pn_community);

        getBookmarkedSchoolCommunities();
    }

    @Override
    protected List<CommunitiesWidgetChildVM> getMyCommunities() {
        return bookmarkedSchoolCommunities;
    }

    private void getBookmarkedSchoolCommunities() {
        AppController.getApi().getBookmarkedPNCommunities(AppController.getInstance().getSessionId(), new Callback<CommunitiesParentVM>() {
            @Override
            public void success(CommunitiesParentVM parent, Response response) {
                if (parent != null) {
                    bookmarkedSchoolCommunities = parent.getCommunities();
                }
            }

            @Override
            public void failure(RetrofitError error) {
                error.printStackTrace();
            }
        });
    }
}
