package com.babybox.adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.DateTimeUtil;
import com.babybox.util.ImageUtil;
import com.babybox.util.ViewUtil;
import com.babybox.viewmodel.UserVMLite;

import java.util.Date;
import java.util.List;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class AdminUserListAdapter extends BaseAdapter {
    private static final String TAG = AdminUserListAdapter.class.getName();

    private ImageView userImage;
    private TextView userDisplayNameText, userNameText, userEmailText, userIdText, createdDateText, lastActiveText;
    private Button deleteButton;

    private Activity activity;
    private LayoutInflater inflater;

    private List<UserVMLite> users;

    public AdminUserListAdapter(Activity activity, List<UserVMLite> users) {
        this.activity = activity;
        this.users = users;
    }

    @Override
    public int getCount() {
        if (users == null)
            return 0;
        return users.size();
    }

    @Override
    public UserVMLite getItem(int location) {
        if (users == null || location > users.size()-1)
            return null;
        return users.get(location);
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
            convertView = inflater.inflate(R.layout.admin_user_list_item, null);

        userImage = (ImageView) convertView.findViewById(R.id.userImage);
        userDisplayNameText = (TextView) convertView.findViewById(R.id.userDisplayNameText);
        userNameText = (TextView) convertView.findViewById(R.id.userNameText);
        userEmailText = (TextView) convertView.findViewById(R.id.userEmailText);
        deleteButton = (Button) convertView.findViewById(R.id.deleteButton);
        userIdText = (TextView) convertView.findViewById(R.id.userIdText);
        createdDateText = (TextView) convertView.findViewById(R.id.createdDateText);
        lastActiveText = (TextView) convertView.findViewById(R.id.lastActiveText);

        final UserVMLite item = users.get(position);

        // profile pic
        ImageUtil.displayThumbnailProfileImage(item.getId(), userImage);

        userImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        userDisplayNameText.setText(item.getDisplayName());
        userDisplayNameText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewUtil.startUserProfileActivity(activity, item.getId());
            }
        });

        userNameText.setText(item.getFirstName()+" "+item.getLastName());
        userEmailText.setText(item.getEmail());

        // delete account
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
                alertDialogBuilder.setMessage(activity.getString(R.string.admin_delete_account_confirm));
                alertDialogBuilder.setPositiveButton(activity.getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteAccount(item.getId());
                    }
                });
                alertDialogBuilder.setNegativeButton(activity.getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();
            }
        });

        // details
        userIdText.setText("ID: "+item.id);
        createdDateText.setText("Signup: "+DateTimeUtil.getTimeAgo(item.getCreatedDate()));
        lastActiveText.setText("Active: "+DateTimeUtil.getTimeAgo(item.getLastLogin()));

        // active after signup date
        lastActiveText.setTextColor(activity.getResources().getColor(R.color.gray));
        if (!DateTimeUtil.withinADay(item.getCreatedDate(), item.getLastLogin())) {
            // active within this week and signup more than a week ago
            if (DateTimeUtil.withinAWeek(item.getLastLogin(), new Date().getTime()) &&
                    !DateTimeUtil.withinAWeek(item.getCreatedDate(), new Date().getTime())) {
                lastActiveText.setTextColor(activity.getResources().getColor(R.color.admin_green));
            } else {
                lastActiveText.setTextColor(activity.getResources().getColor(R.color.orange));
            }
        }

        return convertView;
    }

    private void deleteAccount(Long id) {
        AppController.getApiService().deleteAccount(id, new Callback<Response>() {
            @Override
            public void success(Response responseObject, Response response) {
                Toast.makeText(activity, activity.getString(R.string.admin_delete_account_success), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void failure(RetrofitError error) {
                Toast.makeText(activity, activity.getString(R.string.post_delete_failed), Toast.LENGTH_SHORT).show();
                Log.e(TAG, "deleteAccount: failure", error);
            }
        });
    }
}