package com.babybox.gcm;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
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

import java.util.List;

public class GCMNotificationIntentService extends IntentService {

	public static final String TAG = GCMNotificationIntentService.class.getName();

	public static final int NOTIFICATION_ID = 1;

	private NotificationManager mNotificationManager;
	private NotificationCompat.Builder builder;
	public NotificationType notificationType;

	public static enum NotificationType{
		MESSAGE,
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
			if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
					.equals(messageType)) {
				sendNotification("Send error: " + extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
					.equals(messageType)) {
				sendNotification("Deleted messages on server: "
						+ extras.toString());
			} else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
					.equals(messageType)) {

				for (int i = 0; i < 3; i++) {
					Log.i(TAG,
							"Working... " + (i + 1) + "/5 @ "
									+ SystemClock.elapsedRealtime());
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
					}

				}
				Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

				sendNotification(extras.get(GCMClient.MESSAGE_KEY).toString());
				Log.i(TAG, "Received: " + extras.toString());
			}
		}

		GCMBroadcastReceiver.completeWakefulIntent(intent);
	}

	private void sendNotification(String msg) {
		Log.d(TAG, "Preparing to send notification...: " + msg);
		try {
			JSONObject jObject = new JSONObject(msg);
			if(jObject.get("messageType").equals("comment")){
				List<String> messages = SharedPreferencesUtil.getInstance().getCommentNotifs();
				messages.add(jObject.get("message").toString());
				SharedPreferencesUtil.getInstance().saveCommentNotifs(messages);
				updateOrSendNotification(this, NotificationType.COMMENT, messages);
			} else if(jObject.get("messageType").equals("conversation")) {
				List<String> messages = SharedPreferencesUtil.getInstance().getMessageNotifs();
				messages.add(jObject.get("message").toString());
				SharedPreferencesUtil.getInstance().saveMessageNotifs(messages);
				updateOrSendNotification(this, NotificationType.MESSAGE, messages);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		Log.d(TAG, "Notification sent successfully.");
	}

	public static void updateOrSendNotification(Context context, NotificationType notificationType, List<String> messages) {
		NotificationManager notificationManager = (NotificationManager)
				context.getSystemService(Context.NOTIFICATION_SERVICE);

		int count = messages.size();
		if (messages.size() == 0) {
			notificationManager.cancel(NOTIFICATION_ID);
			return;
		}

		Intent intent = null;
		PendingIntent contentIntent = null;
		String ticker = AppController.APP_NAME;
		NotificationCompat.Builder mBuilder = null;
		if(notificationType == NotificationType.MESSAGE){
			intent = new Intent(Intent.ACTION_VIEW,
					null,
					context, ConversationListActivity.class);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			intent.putExtra(ViewUtil.GCM_LAUNCH_TARGET, true);
			int requestID = (int) System.currentTimeMillis();
			contentIntent = PendingIntent.getActivity(context, requestID,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			mBuilder = new NotificationCompat.Builder(context);


		} else {
			//Intent to be launched on notification click
			intent = new Intent(Intent.ACTION_VIEW,
					null,
					context, MainActivity.class);
			intent.putExtra(ViewUtil.GCM_LAUNCH_TARGET, true);
			intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
			int requestID = (int) System.currentTimeMillis();
			contentIntent = PendingIntent.getActivity(context, requestID,
					intent, PendingIntent.FLAG_UPDATE_CURRENT);

			 mBuilder = new NotificationCompat.Builder(context);

		}

		String contentTitle = AppController.APP_NAME;
		mBuilder.setSmallIcon(R.drawable.ic_launcher)
				.setTicker(ticker)                      // the thicker is the message that appears on the status bar when the notification first appears
				.setDefaults(Notification.DEFAULT_ALL)  // use defaults for various notification settings
				.setContentIntent(contentIntent)        // intent used on click
				.setAutoCancel(true)                    // if you want the notification to be dismissed when clicked
				.setVibrate(new long[]{100, 250, 100, 250, 100, 250 })
				.setOnlyAlertOnce(true); // don't play any sound or flash light if since we're updating

		NotificationCompat.Style style;
		if (count > 1) {
			NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
			style = inboxStyle;

			mBuilder.setContentTitle(contentTitle);

			for (String r : messages) {
				inboxStyle.addLine(r);
			}
		} else {
			NotificationCompat.BigTextStyle bigTextStyle = new NotificationCompat.BigTextStyle();
			style = bigTextStyle;

			bigTextStyle.setBigContentTitle(messages.get(0).substring(0, 10).concat(" ..."));
			bigTextStyle.bigText(messages.get(0));
		}

		mBuilder.setStyle(style);

		if(notificationType == NotificationType.MESSAGE) {
			notificationManager.notify(NOTIFICATION_ID, mBuilder.build());
		} else {
			notificationManager.notify(NOTIFICATION_ID + 1, mBuilder.build());
		}
	}

}
