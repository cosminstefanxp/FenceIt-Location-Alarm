/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.List;

import org.apache.log4j.Logger;

import com.fenceit.Log4jConfiguration;
import com.fenceit.alarm.Alarm;
import com.fenceit.db.DatabaseAccessor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

/**
 * The Class BackgroundService is the background service that is indefinitely runnning in the
 * background, scanning for any events that could trigger any of the alarms.
 */
public class BackgroundService extends Service {

	/** The Constant log. */
	private static final Logger log = Logger.getLogger(BackgroundService.class);

	/** The alarms in the database. */
	List<Alarm> alarms;

	@Override 
	public void onCreate() {
		super.onCreate();
		// Making sure the Log4J is configured, even if the main application process is not started
		new Log4jConfiguration();
		log.warn("The Background Service is created.");

		// Fetch the alarms (complete)
		alarms = DatabaseAccessor.buildFullAlarms(this, null);
		log.info("Fetched alarms from database: " + alarms);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		super.onStartCommand(intent, flags, startId);
		log.warn("The Background Service is started with the start id: " + startId);
		Toast.makeText(this, "Background Service Started...", Toast.LENGTH_SHORT).show();

		return START_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Toast.makeText(this, "Service stopping", Toast.LENGTH_SHORT).show();
		log.warn("The Background service is stopped.");
	}

	@Override
	public IBinder onBind(Intent intent) {
		// Not allowing binding
		return null;
	}

}
