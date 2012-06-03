package com.fenceit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class SystemAlarmReceiver extends BroadcastReceiver {
	private static final String tag = "TestReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.d(tag, "intent=" + intent);
		String message = intent.getStringExtra("message");

		Toast.makeText(context, "Tada", Toast.LENGTH_SHORT);
		Log.d(tag, message);

		Intent serviceIntent = new Intent(context, BackgroundService.class);
		context.startService(serviceIntent);

	}
}
