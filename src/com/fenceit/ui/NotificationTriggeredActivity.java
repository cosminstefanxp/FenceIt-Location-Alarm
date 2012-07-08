/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.actions.NotificationAction;

public class NotificationTriggeredActivity extends Activity {
	/** The logger. */
	private static final Logger log = Logger.getLogger(AlarmActivity.class);

	/** The alarm. */
	private NotificationAction action;
	private DefaultDAO<NotificationAction> dao;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.notification_triggered);

		// // Prepare database connection
		// if (dao == null)
		// dao = DatabaseManager.getDAOInstance(getApplicationContext(), NotificationAction.class,
		// NotificationAction.tableName);

		((TextView) findViewById(R.id.notification_TitleText)).setText("Home Alarm");
		((TextView) findViewById(R.id.notification_descriptionText)).setText("Call grandma to invite for dinner.");

		Uri alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
		if (alert == null) {
			// alert is null, using backup
			alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			if (alert == null) { // I can't see this ever being null (as always have a default
									// notification) but just in  case
				// alert backup is null, using 2nd backup
				alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
			}
		}
		if (alert != null) {
			Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), alert);
			//R-ul devine null
			r.play();
		}

	}
}
