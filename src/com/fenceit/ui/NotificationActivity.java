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
import android.os.Bundle;
import android.widget.TextView;

import com.fenceit.R;
import com.fenceit.alarm.actions.NotificationAction;
import com.fenceit.db.DatabaseManager;

public class NotificationActivity extends Activity {
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
		setContentView(R.layout.notification);

//		// Prepare database connection
//		if (dao == null)
//			dao = DatabaseManager.getDAOInstance(getApplicationContext(), NotificationAction.class,
//					NotificationAction.tableName);

		((TextView) findViewById(R.id.notification_TitleText)).setText("Home Alarm");
		((TextView) findViewById(R.id.notification_descriptionText)).setText("Call grandma to invite for dinner.");
	}
}
