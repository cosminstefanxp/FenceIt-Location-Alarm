/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;

/**
 * The Class SystemEventsReceiver handles broadcasts that arrive from the System and sends them accordingly to
 * the service.
 */
public class SystemEventsReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {

		boolean valid = false;
		List<Integer> events = new ArrayList<Integer>(2);

		// If the device has just booted
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
			Logger.getRootLogger().debug("Device booted up. Starting background service check...");
			valid = true;
			events.add(BackgroundService.SERVICE_EVENT_FORCE_RECHECK);
		} else
		// If the Wi-Fi interface has just been enabled
		if (intent.getAction().equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
			if (Logger.getRootLogger().isDebugEnabled())
				Logger.getRootLogger().debug(
						"Wifi State changed to " + intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1)
								+ ". If new state is enabled, starting background service check...");
			if (intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, -1) == WifiManager.WIFI_STATE_ENABLED) {
				valid = true;
				events.add(BackgroundService.SERVICE_EVENT_WIFI_CONNECTED);
				events.add(BackgroundService.SERVICE_EVENT_WIFIS_DETECTED);
			}
		}

		// If none of the events where valid ones, don't do anything
		if (!valid)
			return;

		// Initialize the WakeLock Manager and acquire a lock here, as the OS might pre-empt between the
		// return from this method and the actual start of the service and the device might go to sleep. The
		// device cannot go to sleep while this method is running, but the same thing is not true right after
		// it is finished.
		LightedGreenRoomWakeLockManager.setup(context);
		LightedGreenRoomWakeLockManager.acquireLock();

		for (Integer event : events) {
			// Build the intent for the service
			Intent serviceIntent = new Intent(context, BackgroundService.class);
			serviceIntent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME, event);

			// Start the service
			context.startService(serviceIntent);
		}
	}
}
