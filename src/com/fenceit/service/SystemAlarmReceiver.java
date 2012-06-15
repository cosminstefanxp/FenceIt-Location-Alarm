/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import org.apache.log4j.Logger;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.fenceit.provider.WifiDataProvider;

/**
 * The Class SystemAlarmReceiver handles broadcasts that arrive from the System {@link AlarmManager}
 * and sends them accordingly to the service.
 */
public class SystemAlarmReceiver extends BroadcastReceiver {

	/** The Constant logger. */
	private static final Logger log = Logger.getLogger(BroadcastReceiver.class);

	/* (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent) */
	@Override
	public void onReceive(Context context, Intent intent) {
		// Processs the event type
		int message = intent.getIntExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
				BackgroundService.SERVICE_EVENT_NONE);
		log.debug("Received alarm broadcast for event type: " + message);
		if (message == BackgroundService.SERVICE_EVENT_NONE)
			return;

		// If the event is SERVICE_EVENT_WIFIS_DETECTED, just start a Wifi Scan. When the scan a
		// finished, a broadcast will be received and will be processed by the
		// WifiBroadcastReceiver, which will start the Background service accordingly.
		if (message == BackgroundService.SERVICE_EVENT_WIFIS_DETECTED) {
			log.debug("Starting WifiScan");
			WifiDataProvider.startScan(context);
			return;
		}

		// Build the intent for the service
		Intent serviceIntent = new Intent(context, BackgroundService.class);
		serviceIntent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME, message);

		// Start the service
		context.startService(serviceIntent);
	}
}
