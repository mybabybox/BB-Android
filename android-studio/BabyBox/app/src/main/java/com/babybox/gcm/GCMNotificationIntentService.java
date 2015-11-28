package com.babybox.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.babybox.R;
import com.babybox.activity.ConversationListActivity;
import com.babybox.activity.MainActivity;
import com.babybox.app.AppController;
import com.babybox.util.SharedPreferencesUtil;
import com.babybox.util.ViewUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONException;
import org.json.JSONObject;
import org.parceler.apache.commons.lang.StringUtils;

import java.util.List;

public class GCMNotificationIntentService extends IntentService {

	public static final String TAG = GCMNotificationIntentService.class.getSimpleName();

	public static enum NotificationType {
        CONVERSATION,
		COMMENT
	}

	public GCMNotificationIntentService() {
		super(GCMNotificationIntentService.class.getSimpleName());
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		Bundle extras = intent.getExtras();
		GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);

		String messageType = gcm.getMessageType(intent);

		if (!extras.isEmpty()) {
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED.equals(messageType)) {
				sendNotification("Deleted messages on server: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE.equals(messageType)) {
                sendNotification(extras.get(GCMClient.MESSAGE_KEY).toString());
			}
		}

		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private String getMessage(String messageType, String actor, String message) {
		String gcmMessage = message;
        if (!StringUtils.isEmpty(actor)) {
            gcmMessage = actor + ": " + message;
        }

		if (messageType.equals(NotificationType.COMMENT.name())) {
			if (MainActivity.getInstance() != null) {
				gcmMessage = actor + MainActivity.getInstance().getString(R.string.activity_commented) + "\n" + message;
			}
		}
		return gcmMessage;
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		try {
			JSONObject jObject = new JSONObject(msg);
			String messageType = jObject.getString("messageType");
			String actor = jObject.getString("actor");
			String message = jObject.getString("message");
			if (messageType.equals(NotificationType.COMMENT.name())) {
				List<String> messages = SharedPreferencesUtil.getInstance().getGcmCommentNotifs();
				messages.add(getMessage(messageType, actor, message));
				SharedPreferencesUtil.getInstance().saveGcmCommentNotifs(messages);
				updateOrSendNotification(this, NotificationType.COMMENT, messages);
			} else if(messageType.equals(NotificationType.CONVERSATION.name())) {
				List<String> messages = SharedPreferencesUtil.getInstance().getGcmConversationNotifs();
				messages.add(getMessage(messageType, actor, message));
				SharedPreferencesUtil.getInstance().saveGcmConversationNotifs(messages);
				updateOrSendNotification(this, NotificationType.CONVERSATION, messages);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "Notification sent successfully.");
	}

	public static void updateOrSendNotification(Context context, NotificationType notificationType, List<String> messages) {
        if (notificationType == null) {
            return;
        }

		NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

		int count = messages.size();
		if (messages.size() == 0) {
            if (notificationType == NotificationType.CONVERSATION) {
                notificationManager.cancel(NotificationType.CONVERSATION.ordinal());
            } else if (notificationType == NotificationType.COMMENT){
                notificationManager.cancel(NotificationType.COMMENT.ordinal());
            }
			return;
		}

		Intent intent = null;
		PendingIntent contentIntent = null;
		String ticker = AppController.APP_NAME;
		NotificationCompat.Builder mBuilder = null;
        String contentTitle = AppController.APP_NAME;

        Log.d(TAG, "updateOrSendNotification: notificationType=" + notificationType);
        int requestId = (int) System.currentTimeMillis();
		if(notificationType == NotificationType.CONVERSATION) {
			intent = new Intent(Intent.ACTION_VIEW,
					null,
					context,
                    ConversationListActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
                    Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ViewUtil.GCM_LAUNCH_TARGET, "true");
			contentIntent = PendingIntent.getActivity(
                    context,
                    requestId,
					intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder = new NotificationCompat.Builder(context);
            contentTitle = context.getString(R.string.gcm_new_conversations, count);
		} else if (notificationType == NotificationType.COMMENT) {
			//Intent to be launched on notification click
			intent = new Intent(Intent.ACTION_VIEW,
					null,
					context,
                    MainActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK |
					Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra(ViewUtil.GCM_LAUNCH_TARGET, "true");
			contentIntent = PendingIntent.getActivity(
                    context,
                    requestId,
					intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

			 mBuilder = new NotificationCompat.Builder(context);
            contentTitle = context.getString(R.string.gcm_new_comments, count);
		}

		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(ticker)                          // the thicker is the message that appears on the status bar when the notification first appears
				.setDefaults(Notification.DEFAULT_ALL|
                        Notification.DEFAULT_SOUND|
                        Notification.DEFAULT_LIGHTS|
                        Notification.DEFAULT_VIBRATE)       // use defaults for various notification settings
				.setContentIntent(contentIntent)            // intent used on click
                .setContentTitle(contentTitle)
				.setAutoCancel(true)
                .setPriority(Notification.PRIORITY_MAX);   // if you want the notification to be dismissed when clicked
				//.setVibrate(new long[]{100, 250, 100, 250, 100, 250 })
				//.setOnlyAlertOnce(true);                  // don't play any sound or flash light if since we're updating

        // style
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        //inboxStyle.setBigContentTitle(contentTitle);
        String contextText = "";
        for (String message : messages) {
            inboxStyle.addLine(message);
            contextText += message + "\n";
        }
		mBuilder.setStyle(inboxStyle);
        mBuilder.setContentText(contextText);

		if (notificationType == NotificationType.CONVERSATION) {
			notificationManager.notify(NotificationType.CONVERSATION.ordinal(), mBuilder.build());
		} else if (notificationType == NotificationType.COMMENT) {
			notificationManager.notify(NotificationType.COMMENT.ordinal(), mBuilder.build());
		}
	}
}
