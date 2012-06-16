/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import org.apache.log4j.Logger;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.fenceit.Log4jConfiguration;
import com.fenceit.R;
import com.fenceit.alarm.locations.CellLocation;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.service.checkers.TriggerCheckerBroker;
import com.fenceit.ui.AlarmPanelActivity;

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

	/**
	 * The Constant SERVICE_EVENT_WIFIS_DETECTED used for defining the event related to
	 * {@link WifisDetectedLocation}.
	 */
	public static final int SERVICE_EVENT_WIFIS_DETECTED = 4;

	/**
	 * The Constant SERVICE_EVENT_WIFIS_CELL_NETWORK used for defining the event related to
	 * {@link CellLocation}.
	 */
	public static final int SERVICE_EVENT_CELL_NETWORK = 5;

	/**
	 * The Constant SERVICE_EVENT_WIFI_CONNECTED used for defining the event related to
	 * {@link WifiConnectedLocation}.
	 */
	public static final int SERVICE_EVENT_WIFI_CONNECTED = 3;

	/** The Constant SERVICE_EVENT_NONE. */
	public static final int SERVICE_EVENT_NONE = 2;

	/** The Constant SERVICE_EVENT_RESET_ALARMS. */
	public static final int SERVICE_EVENT_RESET_ALARMS = 0;

	/** The Constant SERVICE_EVENT_FIELD_NAME. */
	public static final String SERVICE_EVENT_FIELD_NAME = "event";

	/** The handler. */
	private BackgroundServiceHandler handler;

	/** The alarm dispatcher. */
	private SystemAlarmDispatcher alarmDispatcher;

	/** The notification manager. */
	private NotificationManager notificationManager;

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onCreate() */
	@Override
	public void onCreate() {
		super.onCreate();

		// Making sure the Log4J is configured, even if the main application process is not started
		new Log4jConfiguration();
		log.warn("Creating the Background Service.");

		// Setup the notification and start the service as foreground service
		Notification notification = prepareOngoingNotification();
		startForeground(ONGOING_NOTIFICATION, notification);

		// Setup other stuff
		handler = new BackgroundServiceHandler(this);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		alarmDispatcher = new SystemAlarmDispatcher(this.getApplicationContext());
		alarmDispatcher.dispatchAlarm(Utils.getTimeAfterInSecs(15).getTimeInMillis(), SERVICE_EVENT_WIFI_CONNECTED);
		alarmDispatcher.dispatchAlarm(Utils.getTimeAfterInSecs(20).getTimeInMillis(), SERVICE_EVENT_CELL_NETWORK);
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

	private void processEvent(int event) {
		log.info("Processing received event: " + event);

		// Run the trigger checker thread
		Thread thread = TriggerCheckerBroker.getTriggerCheckerThread(getApplicationContext(), handler, event);
		thread.start();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onDestroy() */
	@Override
	public void onDestroy() {
		super.onDestroy();

		Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();

		// TODO: Debug only - Stop any pending system alarms
		alarmDispatcher.cancelAlarm(SERVICE_EVENT_WIFI_CONNECTED);
		alarmDispatcher.cancelAlarm(SERVICE_EVENT_WIFIS_DETECTED);
		alarmDispatcher.cancelAlarm(SERVICE_EVENT_CELL_NETWORK);

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
		Intent notificationIntent = new Intent(getApplicationContext(), AlarmPanelActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0, notificationIntent, 0);
		notification.setLatestEventInfo(getApplicationContext(), "FenceIt",
				"The application is constantly searching for triggers.", pendingIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		return notification;
	}

	/**
	 * Publishes a notification.
	 * 
	 * @param title the title
	 * @param tickerText the ticker text
	 * @param message the message
	 */
	protected void publishNotification(String title, String tickerText, String message) {
		// On click, create a new FenceIt Activity. If the activity is started already, clear
		// everything above it and bring it back
		Intent notificationIntent = new Intent(this, AlarmPanelActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

		// Create the notification
		Notification notification = new Notification(R.drawable.ic_logo, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(this, title, message, pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(ALARM_TRIGGERED_NOTIFICATION, notification);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Service#onBind(android.content.Intent) */
	@Override
	public IBinder onBind(Intent intent) {
		// Not allowing binding
		return null;
	}

	/**
	 * Creates an alarm. This method will be used by the handler, called from checker threads.
	 * 
	 * @param when the when, as milliseconds since 1st of January 1970
	 * @param eventType the event type
	 */
	public void dispatchAlarm(long when, int eventType) {
		this.alarmDispatcher.dispatchAlarm(when, eventType);
	}

}
