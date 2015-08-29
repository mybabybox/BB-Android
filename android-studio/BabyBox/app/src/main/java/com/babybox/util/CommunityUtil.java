package com.babybox.util;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import com.babybox.R;
import com.babybox.activity.CommunityActivity;
import com.babybox.app.AppController;
import com.babybox.app.LocalCommunityTabCache;
import com.babybox.viewmodel.CommunitiesWidgetChildVM;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommunityUtil {

    private List<CommunitiesWidgetChildVM> communities;

    private Activity activity;

    public CommunityUtil(Activity activity) {
        this(activity, null);
    }

    public CommunityUtil(Activity activity, List<CommunitiesWidgetChildVM> communities) {
        this.activity = activity;
        this.communities = communities;
    }

    public void setCommunityLayout(final int index, final View commLayout, final ImageView commImage, final TextView commName) {

        if (communities == null || index >= communities.size()) {
            commLayout.setVisibility(View.GONE);
            return;
        }

        // set layout

        final CommunitiesWidgetChildVM item = communities.get(index);

        commName.setText(item.getDn());

        int iconMapped = ImageMapping.map(item.gi);
        if (iconMapped != -1) {
            //Log.d(this.getClass().getSimpleName(), "setCommunityLayout: replace source with local comm icon - " + item.gi);
            commImage.setImageDrawable(activity.getResources().getDrawable(iconMapped));
        } else {
            Log.d(this.getClass().getSimpleName(), "setCommunityLayout: load comm icon from background - " + item.gi);
            ImageUtil.displayCircleImage(item.gi, commImage);
        }

        commLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startCommunityActivity(item);
            }
        });
    }

    public void joinCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinButton) {
        AppController.getApi().sendJoinRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(activity, activity.getString(R.string.community_join_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(true);
                joinButton.setImageResource(R.drawable.ic_check);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, activity.getString(R.string.community_join_failed), Toast.LENGTH_SHORT).show();
                Log.e(CommunityUtil.class.getSimpleName(), "joinCommunity: failure", error);
            }
        });
    }

    public void leaveCommunity(final CommunitiesWidgetChildVM communityVM, final ImageView joinImageView) {
        AppController.getApi().sendLeaveRequest(communityVM.id, AppController.getInstance().getSessionId(), new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(activity, activity.getString(R.string.community_leave_success), Toast.LENGTH_SHORT).show();
                communityVM.setIsM(false);
                joinImageView.setImageResource(R.drawable.ic_add);

                LocalCommunityTabCache.refreshMyCommunities();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, activity.getString(R.string.community_leave_failed), Toast.LENGTH_SHORT).show();
                Log.e(CommunityUtil.class.getSimpleName(), "leaveCommunity: failure", error);
            }
        });
    }

    public void startCommunityActivity(CommunitiesWidgetChildVM community) {
        if (community != null) {
            Log.d(this.getClass().getSimpleName(), "startCommunityActivity with commId - " + community.getId());
            Intent intent = new Intent(activity, CommunityActivity.class);
            intent.putExtra("id", community.getId());
            intent.putExtra("flag", "FromTopicFragment");
            activity.startActivity(intent);
        }
    }

    public List<CommunitiesWidgetChildVM> getCommunities() {
        return communities;
    }
}
