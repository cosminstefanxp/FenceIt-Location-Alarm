/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.List;

import org.apache.log4j.Logger;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.fenceit.Log4jConfiguration;
import com.fenceit.R;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.ui.FenceItActivity;

/**
 * The Class BackgroundService is the background service that is indefinitely runnning in the
 * background, scanning for any events that could trigger any of the alarms.
 */
public class BackgroundService extends Service {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(BackgroundService.class);

	/** The Constant ONGOING_NOTIFICATION used for identifying notifications. */
	private static final int ONGOING_NOTIFICATION = 2;

	/** The Constant ALARM_TRIGGERED_NOTIFICATION used for identifying notifications. */
	private static final int ALARM_TRIGGERED_NOTIFICATION = 1;

	public static final int SERVICE_EVENT_WIFI = 3;

	public static final int SERVICE_EVENT_NONE = 2;

	public static final String SERVICE_EVENT_FIELD_NAME = "event";

	/** The alarms in the database. */
	List<Alarm> alarms;

	/** The alarm dispatcher. */
	private SystemAlarmDispatcher alarmDispatcher;

	@Override
	public void onCreate() {
		super.onCreate();

		// Making sure the Log4J is configured, even if the main application process is not started
		new Log4jConfiguration();
		log.warn("Creating the Background Service.");

		// Setup the notification and start the service as foreground service
		Notification notification = prepareOngoingNotification();
		startForeground(ONGOING_NOTIFICATION, notification);

		alarmDispatcher = new SystemAlarmDispatcher(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		log.warn("Starting Background Service with intent: " + intent);
		Toast.makeText(this, "Background Service Started...", Toast.LENGTH_SHORT).show();

		// Process the extras
		int event = intent.getIntExtra(SERVICE_EVENT_FIELD_NAME, SERVICE_EVENT_NONE);
		if (event != SERVICE_EVENT_NONE)
			processEvent(event);

		return START_NOT_STICKY;
	}

	/**
	 * Processes an event received by the service.
	 * 
	 * @param event the event
	 */
	private void processEvent(int event) {
		log.info("Processing received event: " + event);
	}

	private List<Alarm> fetchFullAlarms() {
		// Fetch the complete alarms (using a separate thread)
		new Thread(new Runnable() {
			@Override
			public void run() {
				alarms = DatabaseAccessor.buildFullAlarms(getApplicationContext(), null);
				log.info("Fetched alarms from database: " + alarms);
			}
		}).start();
		return alarms;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

		// If somehow the wakelock is still locked, release it
		WakeLockManager.releaseWakeLock();

		log.warn("Stopping background service...");
	}

	/**
	 * Prepares the ongoing notification that is showing in the notification area while the service
	 * is running.
	 * 
	 * @return the notification
	 */
	private Notification prepareOngoingNotification() {
		Notification notification = new Notification(R.drawable.ic_logo, "FenceIt service started...",
				System.currentTimeMillis());

		// On click, create a new FenceIt Activity. If the activity is started already, clear
		// everything above it and bring it back
		Intent notificationIntent = new Intent(this, FenceItActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
		notification.setLatestEventInfo(this, "FenceIt", "The application is constantly searching for triggers.",
				pendingIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		return notification;
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Not allowing binding
		return null;
	}

}
