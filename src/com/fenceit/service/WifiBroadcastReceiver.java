/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import org.apache.log4j.Logger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * The Class WifiBroadcastReceiver is the BroadcastReceiver that handles Wifi Related Broadcasts.
 */
public class WifiBroadcastReceiver extends BroadcastReceiver {

	/** The logger. */
	Logger log = Logger.getLogger(WifiBroadcastReceiver.class);

	/* (non-Javadoc)
	 * 
	 * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
	 * android.content.Intent) */
	@Override
	public void onReceive(Context context, Intent intent) {
		log.debug("Broadcast for Wifi Received");

		// Build the intent for the service
		Intent serviceIntent = new Intent(context, BackgroundService.class);
		serviceIntent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
				BackgroundService.SERVICE_EVENT_WIFIS_DETECTED);

		// Start the service
		context.startService(serviceIntent);
	}

}
