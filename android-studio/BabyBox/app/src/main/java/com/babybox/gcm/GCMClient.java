package com.babybox.gcm;

import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.Log;

import com.babybox.R;
import com.babybox.app.AppController;
import com.babybox.util.SharedPreferencesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;

import java.io.IOException;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class GCMClient {

	public static final String GOOGLE_PROJECT_ID = AppController.getInstance().getString(R.string.gcm_project_id);
	public static final String MESSAGE_KEY = "message";
	public static final String REG_ID = "regId";

	private GoogleCloudMessaging gcm;

	private String regId;

	private static GCMClient instance;

	public static GCMClient getInstance() {
		if (instance == null) {
			instance = new GCMClient();
		}
		return instance;
	}

	private GCMClient() {
	}

	public void registerGCM() {
		/*
		regId = getRegId();
		if (TextUtils.isEmpty(regId)) {
			registerInBackground();
		} else {
			// send old key to server (make sure server didn't miss it before)
			sendGCMKeyToServer(regId);
			Log.d(this.getClass().getSimpleName(), "registerGCM: regId="+regId+" in SharedPreference will be used");
		}
		*/

		// always register new gcm
		registerInBackground();

		// start gcm broadcast
		AppController.getInstance().sendBroadcast(new Intent(AppController.getInstance(), GCMBroadcastReceiver.class));
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(AppController.getInstance());
					}

					regId = gcm.register(GOOGLE_PROJECT_ID);
					Log.d(GCMClient.class.getSimpleName(), "registerInBackground: successfully registered with GCM server - regId="+regId);
					msg = "Device registered, regId="+regId;

					saveRegId(regId);

					// new gcm key, send to server
					sendGCMKeyToServer(regId);
				} catch (IOException e) {
					Log.e(GCMClient.class.getSimpleName(), "registerInBackground: Failed to register regId", e);
				}
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
			}
		}.execute(null, null, null);
	}

	public void saveRegId(String regId) {
		int appVersion = AppController.getVersionCode();
		Log.i(GCMClient.class.getName(), "Saving regId on app version " + appVersion);
		SharedPreferencesUtil.getInstance().saveGCMKey(regId);
		SharedPreferencesUtil.getInstance().saveAppVersion((long) appVersion);
	}

	public String getRegId() {
		String regId = SharedPreferencesUtil.getInstance().getGCMKey();
		if (TextUtils.isEmpty(regId)) {
			Log.d(GCMClient.class.getSimpleName(), "RegId not found.");
			return "";
		}

		int registeredVersion = SharedPreferencesUtil.getInstance().getAppVersion().intValue();
		int currentVersion = AppController.getVersionCode();
		if (registeredVersion != currentVersion) {
			Log.i(GCMClient.class.getSimpleName(), "App version changed.");
			return "";
		}

		return regId;
	}

	public void sendGCMKeyToServer(final String regId) {
		AppController.getApiService().saveGCMKey(regId, new Long(AppController.getVersionCode()), new Callback<Response>() {
			@Override
			public void success(Response responseObject, Response response) {
				Log.d(GCMClient.class.getSimpleName(), "sendGCMKeyToServer: successfully send GCM key to server - regId="+regId);
			}

			@Override
			public void failure(RetrofitError error) {
				Log.e(GCMClient.class.getSimpleName(), "sendGCMKeyToServer: failure", error);
			}
		});
	}
}
