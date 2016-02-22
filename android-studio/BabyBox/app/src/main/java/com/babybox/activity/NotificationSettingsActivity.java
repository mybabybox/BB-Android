package com.babybox.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.babybox.R;
import com.babybox.app.TrackedFragmentActivity;
import com.babybox.app.UserInfoCache;
import com.babybox.viewmodel.SettingsVM;

public class NotificationSettingsActivity extends TrackedFragmentActivity {
    private static final String TAG = NotificationSettingsActivity.class.getName();

    private RelativeLayout emailNewPostLayout, emailNewChatLayout, emailNewCommentLayout, emailNewPromoLayout;
    private RelativeLayout pushNewChatLayout, pushNewCommentLayout, pushNewFollowLayout, pushNewFeedbackLayout, pushNewPromoLayout;
    private CheckBox emailNewPostCheckBox, emailNewChatCheckBox, emailNewCommentCheckBox, emailNewPromoCheckBox;
    private CheckBox pushNewChatCheckBox, pushNewCommentCheckBox, pushNewFollowCheckBox, pushNewFeedbackCheckBox, pushNewPromoCheckBox;
    private ImageView backImage;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.notification_settings_activity);

        setActionBarTitle(getString(R.string.notifications));

        emailNewPostLayout = (RelativeLayout) this.findViewById(R.id.emailNewPostLayout);
        emailNewChatLayout = (RelativeLayout) this.findViewById(R.id.emailNewChatLayout);
        emailNewCommentLayout = (RelativeLayout) this.findViewById(R.id.emailNewCommentLayout);
        emailNewPromoLayout = (RelativeLayout) this.findViewById(R.id.emailNewPromoLayout);
        pushNewChatLayout = (RelativeLayout) this.findViewById(R.id.pushNewChatLayout);
        pushNewCommentLayout = (RelativeLayout) this.findViewById(R.id.pushNewCommentLayout);
        pushNewFollowLayout = (RelativeLayout) this.findViewById(R.id.pushNewFollowLayout);
        pushNewFeedbackLayout = (RelativeLayout) this.findViewById(R.id.pushNewFeedbackLayout);
        pushNewPromoLayout = (RelativeLayout) this.findViewById(R.id.pushNewPromoLayout);

        emailNewPostCheckBox = (CheckBox) this.findViewById(R.id.emailNewPostCheckBox);
        emailNewChatCheckBox = (CheckBox) this.findViewById(R.id.emailNewChatCheckBox);
        emailNewCommentCheckBox = (CheckBox) this.findViewById(R.id.emailNewCommentCheckBox);
        emailNewPromoCheckBox = (CheckBox) this.findViewById(R.id.emailNewPromoCheckBox);
        pushNewChatCheckBox = (CheckBox) this.findViewById(R.id.pushNewChatCheckBox);
        pushNewCommentCheckBox = (CheckBox) this.findViewById(R.id.pushNewCommentCheckBox);
        pushNewFollowCheckBox = (CheckBox) this.findViewById(R.id.pushNewFollowCheckBox);
        pushNewFeedbackCheckBox = (CheckBox) this.findViewById(R.id.pushNewFeedbackCheckBox);
        pushNewPromoCheckBox = (CheckBox) this.findViewById(R.id.pushNewPromoCheckBox);

        emailNewPostLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailNewPostCheckBox.performClick();
            }
        });
        emailNewChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailNewChatCheckBox.performClick();
            }
        });
        emailNewCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailNewCommentCheckBox.performClick();
            }
        });
        emailNewPromoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emailNewPromoCheckBox.performClick();
            }
        });
        pushNewChatLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewChatCheckBox.performClick();
            }
        });
        pushNewCommentLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewCommentCheckBox.performClick();
            }
        });
        pushNewFollowLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewFollowCheckBox.performClick();
            }
        });
        pushNewFeedbackLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewFeedbackCheckBox.performClick();
            }
        });
        pushNewPromoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pushNewPromoCheckBox.performClick();
            }
        });

        SettingsVM settings = UserInfoCache.getUser().settings;
        emailNewPostCheckBox.setChecked(settings.emailNewPost);
        emailNewChatCheckBox.setChecked(settings.emailNewConversation);
        emailNewCommentCheckBox.setChecked(settings.emailNewComment);
        emailNewPromoCheckBox.setChecked(settings.emailNewPromotions);
        pushNewChatCheckBox.setChecked(settings.pushNewConversation);
        pushNewCommentCheckBox.setChecked(settings.pushNewComment);
        pushNewFollowCheckBox.setChecked(settings.pushNewFollow);
        pushNewFeedbackCheckBox.setChecked(settings.pushNewFeedback);
        pushNewPromoCheckBox.setChecked(settings.pushNewPromotions);

        backImage = (ImageView) this.findViewById(R.id.backImage);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}


