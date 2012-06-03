/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import android.content.Context;
import android.os.Handler;

import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.service.BackgroundService;

/**
 * The TriggerCheckerBroker is used as a broker to add an abstraction layer between the background
 * service implementation and the different types of {@link AlarmLocation}.
 */
public class TriggerCheckerBroker {

	/**
	 * Gets the trigger checker thread corresponding to an event type.
	 * 
	 * @param context the context
	 * @param handler the handler
	 * @param eventType the event type
	 * @return the trigger checker thread
	 */
	public static TriggerCheckerThread getTriggerCheckerThread(Context context, Handler handler, int eventType) {
		switch (eventType) {
		case BackgroundService.SERVICE_EVENT_WIFI:
			return new WifiTriggerCheckerThread(context, handler);
		default:
			return null;
		}
	}
}
