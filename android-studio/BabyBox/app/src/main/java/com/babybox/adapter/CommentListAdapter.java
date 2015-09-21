package com.babybox.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.activity.UserProfileActivity;
import com.babybox.app.AppController;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.CommentVM;

import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class CommentListAdapter extends BaseAdapter {
    private ImageView userImage;
    private TextView userNameText, commentText, timeText, deleteText;

    private Activity activity;
    private LayoutInflater inflater;

    private List<CommentVM> comments;

    public CommentListAdapter(Activity activity, List<CommentVM> comments) {
        this.activity = activity;
        this.comments = comments;
    }

    @Override
    public int getCount() {
        if (comments == null)
            return 0;
        return comments.size();
    }

    @Override
    public CommentVM getItem(int location) {
        if (comments == null || location > comments.size()-1)
            return null;
        return comments.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public synchronized View getView(int position, View convertView, final ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.comment_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        timeText = (TextView) convertView.findViewById(R.id.timeText);
        commentText = (TextView) convertView.findViewById(R.id.commentText);
        deleteText = (TextView) convertView.findViewById(R.id.deleteText);

        final CommentVM item = comments.get(position);

        // admin fields
        LinearLayout adminLayout = (LinearLayout) convertView.findViewById(R.id.adminLayout);
        if (AppController.isUserAdmin()) {
            ImageView androidIcon = (ImageView) convertView.findViewById(R.id.androidIcon);
            ImageView iosIcon = (ImageView) convertView.findViewById(R.id.iosIcon);
            ImageView mobileIcon = (ImageView) convertView.findViewById(R.id.mobileIcon);
            androidIcon.setVisibility(item.isAndroid()? View.VISIBLE : View.GONE);
            iosIcon.setVisibility(item.isIOS()? View.VISIBLE : View.GONE);
            mobileIcon.setVisibility(item.isMobile()? View.VISIBLE : View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        } else {
            adminLayout.setVisibility(View.GONE);
        }

        // delete
        if (item.isOwner() || (AppController.isUserAdmin())) {
            if (item.isOwner()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.gray));
            } else if (AppController.isUserAdmin()) {
                deleteText.setTextColor(this.activity.getResources().getColor(R.color.admin_green));
            }
            deleteText.setVisibility(View.VISIBLE);

            final int pos = position;
            deleteText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(inflater.getContext());
                    alertDialogBuilder.setMessage(CommentListAdapter.this.activity.getString(R.string.post_delete_confirm));
                    alertDialogBuilder.setPositiveButton(CommentListAdapter.this.activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            deleteComment(item.getId(), pos);
                        }
                    });
                    alertDialogBuilder.setNegativeButton(CommentListAdapter.this.activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    AlertDialog alertDialog = alertDialogBuilder.create();
                    alertDialog.show();
                }
            });
        } else {
            deleteText.setVisibility(View.GONE);
        }

        ViewUtil.setHtmlText(item.getDesc(), commentText, activity, true, true);

        userNameText.setText(item.getPostedBy());
        userNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getOwnerId(), "");
            }
        });

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getOwnerId(), "");
            }
        });

        timeText.setText(DateTimeUtil.getTimeAgo(item.getCreatedDate()));

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getOwnerId(), userImage);

        return convertView;
    }

    private void deleteComment(Long id, final int position) {
        AppController.getApiService().deleteComment(id, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                CommentListAdapter.this.comments.remove(position);
                CommentListAdapter.this.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(inflater.getContext(), CommentListAdapter.this.activity.getString(R.string.comment_delete_success), Toast.LENGTH_SHORT).show();
                error.printStackTrace();
                Log.e(CommentListAdapter.class.getSimpleName(), "deleteComment: failure", error);
            }
        });
    }
}