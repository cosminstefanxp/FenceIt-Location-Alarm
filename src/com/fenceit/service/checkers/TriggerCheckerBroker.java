/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import android.content.Context;

import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.service.BackgroundService;

/**
 * The TriggerCheckerBroker is used as a broker to add an abstraction layer between the background
 * service implementation and the different types of {@link AlarmLocation}.
 */
public class TriggerCheckerBroker {

	public static TriggerCheckerThread getTriggerCheckerThread(Context context, int eventType) {
		switch (eventType) {
		case BackgroundService.SERVICE_EVENT_WIFI:
			return new WifiTriggerCheckerThread(context);
		default:
			return null;
		}
	}
}
