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
import com.fenceit.service.BackgroundServiceHandler;

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
	public static TriggerCheckerThread getTriggerCheckerThread(Context context, BackgroundServiceHandler handler,
			int eventType) {
		switch (eventType) {
		case BackgroundService.SERVICE_EVENT_WIFI_CONNECTED:
			return new WifiConnectedTriggerCheckerThread(context, handler,
					BackgroundService.SERVICE_EVENT_WIFI_CONNECTED);
		case BackgroundService.SERVICE_EVENT_WIFIS_DETECTED:
			return new WifisDetectedTriggerCheckerThread(context, handler,
					BackgroundService.SERVICE_EVENT_WIFIS_DETECTED);
		case BackgroundService.SERVICE_EVENT_CELL_NETWORK:
			return new CellTriggerCheckerThread(context, handler, BackgroundService.SERVICE_EVENT_CELL_NETWORK);
		default:
			return null;
		}
	}
}
