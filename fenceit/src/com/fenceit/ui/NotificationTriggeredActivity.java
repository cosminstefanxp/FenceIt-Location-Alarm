/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.apache.log4j.Logger;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.fenceit.R;

public class NotificationTriggeredActivity extends Activity {
	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The ringtone. */
	Ringtone ringtone;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_triggered);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

		((TextView) findViewById(R.id.notification_titleText)).setText("Alarm Triggered");
		((TextView) findViewById(R.id.notification_descriptionText)).setText(getIntent().getExtras().getString(
				"message"));

		// Get a valid alert and set up a ringtone. Try backups if necessary
		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
		if (ringtone == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
			if (ringtone == null) {
				// alert backup is null, using 2nd backup
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
				ringtone = RingtoneManager.getRingtone(getApplicationContext(), alert);
			}
		}
		log.info("Using uri for alert: " + alert);

		// Check if a ringtone was initialized and play it
		if (ringtone != null) {
			ringtone.play();
		} else
			log.warn("No valid ringtone initialized. Not playing a sound alarm.");
	}

	/**
	 * Triggered when the user clicks anywhere on the screen.
	 * 
	 * @param v the view
	 */
	public void onClickScreen(View v) {
		if (ringtone != null)
			ringtone.stop();
		finish();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy() */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (ringtone != null)
			ringtone.stop();
	}
}
