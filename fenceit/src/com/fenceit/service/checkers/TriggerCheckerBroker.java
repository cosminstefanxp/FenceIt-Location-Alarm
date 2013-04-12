/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import android.content.Context;

import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.service.BackgroundService;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The TriggerCheckerBroker is used as a broker to add an abstraction layer between the background service
 * implementation and the different types of {@link AlarmLocation}.
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
			return new WifiConnectedTriggerCheckerThread(context, handler, eventType);
		case BackgroundService.SERVICE_EVENT_WIFIS_DETECTED:
			return new WifisDetectedTriggerCheckerThread(context, handler, eventType);
		case BackgroundService.SERVICE_EVENT_CELL_NETWORK:
			return new CellTriggerCheckerThread(context, handler, eventType);
		case BackgroundService.SERVICE_EVENT_GEO_COORDINATES:
			return new GeoCoordinatesTriggerCheckerThread(context, handler, eventType);
		default:
			return null;
		}
	}

	/**
	 * Gets the service event that is required to start a check for a given location type.
	 * 
	 * @param type the location type
	 * @return the service event
	 */
	public static int getServiceEvent(LocationType type) {
		switch (type) {
		case WifiConnectedLocation:
			return BackgroundService.SERVICE_EVENT_WIFI_CONNECTED;
		case WifisDetectedLocation:
			return BackgroundService.SERVICE_EVENT_WIFIS_DETECTED;
		case CellNetworkLocation:
			return BackgroundService.SERVICE_EVENT_CELL_NETWORK;
		case GeoCoordinatesLocation:
			return BackgroundService.SERVICE_EVENT_GEO_COORDINATES;
		default:
			return BackgroundService.SERVICE_EVENT_NONE;
		}
	}

	/**
	 * Gets the location type that corresponds to a particular BackgroundService event.
	 * 
	 * @param serviceEvent the service event
	 * @return the location type, or null, if it doesn't correspond to a check for a particular location type.
	 */
	public static LocationType getLocationType(int serviceEvent) {
		switch (serviceEvent) {
		case BackgroundService.SERVICE_EVENT_WIFI_CONNECTED:
			return LocationType.WifiConnectedLocation;
		case BackgroundService.SERVICE_EVENT_WIFIS_DETECTED:
			return LocationType.WifisDetectedLocation;
		case BackgroundService.SERVICE_EVENT_CELL_NETWORK:
			return LocationType.CellNetworkLocation;
		case BackgroundService.SERVICE_EVENT_GEO_COORDINATES:
			return LocationType.GeoCoordinatesLocation;
		default:
			return null;
		}
	}
}
