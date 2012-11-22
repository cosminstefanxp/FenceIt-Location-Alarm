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
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import com.fenceit.Log4jConfiguration;
import com.fenceit.R;
import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.alarm.triggers.BasicTrigger.TriggerType;
import com.fenceit.db.AlarmLocationBroker;
import com.fenceit.provider.CoordinatesDataProvider;
import com.fenceit.service.checkers.TriggerCheckerBroker;
import com.fenceit.ui.AlarmPanelActivity;

/**
 * The Class BackgroundService is the background service that is indefinitely runnning in the background,
 * scanning for any events that could trigger any of the enabled alarms.
 */
public class BackgroundService extends Service {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(BackgroundService.class);

	/** The Constant ONGOING_NOTIFICATION used for identifying notifications. */
	private static final int ONGOING_NOTIFICATION = 101;

	/** The Constant ALARM_TRIGGERED_NOTIFICATION used for identifying notifications. */
	private static final int ALARM_TRIGGERED_NOTIFICATION = 102;

	/** The Constant used to define a non-existing event. */
	public static final int SERVICE_EVENT_NONE = 0;

	/** The Constant used to notify the service that service should shut down. */
	public static final int SERVICE_EVENT_SHUTDOWN = 1;

	/**
	 * The Constant to define an event which appears when a check should be done for all location types.
	 */
	public static final int SERVICE_EVENT_FORCE_RECHECK = 2;

	/**
	 * This Constant is used to define an event which initiates a check for triggers related to a
	 * {@link WifiConnectedLocation}.
	 */
	public static final int SERVICE_EVENT_WIFI_CONNECTED = 4;
	/**
	 * This Constant is used to define an event which initiates a check for triggers related to a
	 * {@link WifisDetectedLocation}.
	 */
	public static final int SERVICE_EVENT_WIFIS_DETECTED = 5;

	/**
	 * This Constant is used to define an event which initiates a check for triggers related to a
	 * {@link CellNetworkLocation}.
	 */
	public static final int SERVICE_EVENT_CELL_NETWORK = 6;

	/**
	 * This Constant is used to define an event which initiates a check for triggers related to a
	 * {@link CoordinatesLocation}..
	 */
	public static final int SERVICE_EVENT_GEO_COORDINATES = 7;

	/** This Constant is used to name the field used to store the event in the intents. */
	public static final String SERVICE_EVENT_FIELD_NAME = "event";

	/** The handler. */
	private BackgroundServiceHandler handler;

	/** The alarm dispatcher. */
	private SystemAlarmDispatcher alarmDispatcher;

	/** The notification manager. */
	private NotificationManager notificationManager;

	/** The service state manager. */
	private ServiceStateManager serviceStateManager;

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onCreate()
	 */
	@Override
	public void onCreate() {
		super.onCreate();

		// Making sure the Log4J is configured, even if the main application process is not started
		new Log4jConfiguration();
		log.warn("Creating a new instance of the Background Service...");

		// Initialize the Service state manager
		serviceStateManager = new ServiceStateManager();
		serviceStateManager.updateState(this);

		// Check whether it should start
		if (!serviceStateManager.shouldServiceRun(this)) {
			stopSelf();
			return;
		}

		// Setup the notification and start the service as foreground service
		Notification notification = prepareOngoingNotification();
		startForeground(ONGOING_NOTIFICATION, notification);

		// Setup other stuff
		handler = new BackgroundServiceHandler(this);
		notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		alarmDispatcher = new SystemAlarmDispatcher(this.getApplicationContext());
		CoordinatesDataProvider.mHandler = new Handler();

		// Manage wakeLocks - Set up the green room - The setup is capable of getting called multiple times.
		LightedGreenRoomWakeLockManager.setup(this.getApplicationContext());
		// Register the service as a client to allow safe release of wake locks when destroying the service
		LightedGreenRoomWakeLockManager.registerClient();
		serviceStateManager.setRegisteredToWakeLock(true);

		// Force a full check for enabled alarms
		forceFullCheck();
	}

	/**
	 * Force a full scan for all types of locations. The ServiceStateManager should be updated before calling
	 * this method.
	 */
	private void forceFullCheck() {

		// Force a scan of triggers for the locations that are associated to an enabled alarm
		short delay = 1;
		for (LocationType type : AlarmLocationBroker.getLocationTypes()) {
			if (serviceStateManager.isLocationTypeEnabled(type)) {
				if (log.isDebugEnabled())
					log.debug("Forcing a check for triggers for location type: " + type);
				alarmDispatcher.dispatchAlarm(Utils.getTimeAfterInSecs(delay).getTimeInMillis(),
						TriggerCheckerBroker.getServiceEvent(type));
				delay += 1;
			}
		}
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);

		// Check if the service should be running...
		if (!serviceStateManager.shouldServiceRun(this)) {
			log.warn("Received intent, but service should not run: "
					+ intent.getIntExtra(SERVICE_EVENT_FIELD_NAME, SERVICE_EVENT_NONE));
			return START_NOT_STICKY;
		}

		if (log.isInfoEnabled())
			log.info("Starting Background Service with intent: " + intent);
		Toast.makeText(this, "Background Service Started...", Toast.LENGTH_SHORT).show();

		// Process the extras
		int event = intent.getIntExtra(SERVICE_EVENT_FIELD_NAME, SERVICE_EVENT_NONE);
		if (event != SERVICE_EVENT_NONE)
			processEvent(event);

		return START_NOT_STICKY;
	}

	/**
	 * Process a service event.
	 * 
	 * @param event the event
	 */
	private void processEvent(int event) {
		log.info("Processing received event: " + event);

		// If the service should shutdown
		if (event == SERVICE_EVENT_SHUTDOWN) {
			stopSelf();
			return;
		}

		// If a scan with all the locations types should be scheduled or the service should be shutdown
		if (event == SERVICE_EVENT_FORCE_RECHECK) {
			// Update the state of the ServiceStateManager
			serviceStateManager.updateState(this);
			// Check whether we should stop the service
			if (!serviceStateManager.shouldServiceRun(this)) {
				stopSelf();
			} else
				forceFullCheck();
			return;
		}

		// Run the trigger checker thread, if enabled for the given location type
		LocationType type = TriggerCheckerBroker.getLocationType(event);
		if (serviceStateManager.isLocationTypeEnabled(type)) {
			Thread thread = TriggerCheckerBroker.getTriggerCheckerThread(getApplicationContext(), handler, event);
			if (thread != null)
				thread.start();
		} else {
			// This point can be reached as, when a location type becomes disabled, for optimization reasons,
			// pending system alarms are not disabled
			log.warn("Request to start trigger checker thread, but location type is disabled: " + type);
		}
	}

	/**
	 * Shutdown the service.
	 */
	protected void shutdown() {
		log.info("Shutting down service definitively.");
		// Stop any pending system alarms
		if (alarmDispatcher != null) {
			for (LocationType type : AlarmLocationBroker.getLocationTypes())
				alarmDispatcher.cancelAlarm(TriggerCheckerBroker.getServiceEvent(type));
		}

		// Unregister for intents
		serviceStateManager.unregisterReceivers(this);

	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onDestroy()
	 */
	@Override
	public void onDestroy() {
		super.onDestroy();

		log.warn("Destroying background service...");

		// Clear all details and cancel all pending alarms
		shutdown();

		// If somehow the WakeLock is still locked, release it
		if (serviceStateManager.isRegisteredToWakeLock())
			LightedGreenRoomWakeLockManager.unRegisterClient();
	}

	/**
	 * Prepares the ongoing notification that is showing in the notification area while the service is
	 * running.
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
		notification.setLatestEventInfo(getApplicationContext(), "FenceIt - Location-based Alarms",
				"Constantly searching for triggering conditions...", pendingIntent);
		notification.flags |= Notification.FLAG_NO_CLEAR;
		return notification;
	}

	/**
	 * Publishes a notification.
	 * 
	 * @param title the title
	 * @param requestCode the request code for the notification
	 * @param tickerText the ticker text
	 * @param message the message
	 */
	protected void publishNotification(String title, int requestCode, String tickerText, String message) {
		// On click, create a new FenceIt Activity. If the activity is started already, clear
		// everything above it and bring it back
		Intent notificationIntent = new Intent(this, AlarmPanelActivity.class);
		notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, requestCode, notificationIntent, 0);

		// Create the notification
		Notification notification = new Notification(R.drawable.ic_logo, tickerText, System.currentTimeMillis());
		notification.setLatestEventInfo(this, title, message, pendingIntent);
		notification.defaults = Notification.DEFAULT_ALL;
		notification.flags |= Notification.FLAG_SHOW_LIGHTS | Notification.FLAG_AUTO_CANCEL;
		notificationManager.notify(ALARM_TRIGGERED_NOTIFICATION, notification);
	}

	/*
	 * (non-Javadoc)
	 * @see android.app.Service#onBind(android.content.Intent)
	 */
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
