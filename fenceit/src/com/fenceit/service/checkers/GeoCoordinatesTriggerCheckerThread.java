/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.List;

import android.content.Context;
import android.location.Location;

import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.CoordinatesContextData;
import com.fenceit.provider.CoordinatesDataProvider;
import com.fenceit.provider.CoordinatesLocationDataListener;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The GeoCoordinatesTriggerCheckerThread handles the check for conditions regarding the geographical global
 * position -GPS coordinates- of the device (the alarm locations of type {@link CoordinatesLocation}. If any
 * of the alarms should be triggered, it handles that.
 */
public class GeoCoordinatesTriggerCheckerThread extends TriggerCheckerThread implements CoordinatesLocationDataListener {

	/** The waiting time. */
	private int waitingTime;

	/** The scan for location is done. */
	private boolean isDone;

	/**
	 * Instantiates a new trigger checker thread.
	 * 
	 * @param context the context
	 * @param handler the handler for the main thread
	 */
	public GeoCoordinatesTriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super(context, handler, eventType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData()
	 */
	@Override
	protected List<? extends AlarmTrigger> fetchData() {
		return DatabaseAccessor.buildFullTriggersForEnabledLocationType(mContext, LocationType.GeoCoordinatesLocation);
	}


	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#acquireContextData()
	 */
	@Override
	protected ContextData acquireContextData() {

		// Prepare a scan
		waitingTime = 25000;
		isDone = false;

		// Start a scan for location
		CoordinatesDataProvider provider = new CoordinatesDataProvider();
		provider.addCoordinatesLocationDataListenerOnMainThread(this, mContext);

		// Wait for a given amount of time or until the conditions are satisfied
		while (!isDone && waitingTime > 0) {
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) { // Exit the loop
				break;
			}
			waitingTime -= 1000;
		}

		// Stop the scan and use the best result so far
		provider.removeCoordinatesLocationDataListenerOnMainThread(this);
		ContextData data = provider.getContextData(mContext, true);

		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid()
	 */
	@Override
	protected boolean isPreconditionValid() {

		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.checkers.TriggerCheckerThread#computeNextCheckTime()
	 */
	@Override
	protected Float computeDelayFactor(List<? extends AlarmTrigger> triggers, ContextData data) {

		if (triggers.isEmpty())
			return null;

		CoordinatesContextData cData = (CoordinatesContextData) data;
		// The GeoCoordinates has a larger factor from the beginning
		Float factor = 2.0f;
		if (cData == null)
			return factor;
		// If in the same position for a lot of time, increase the factor, but up to 500%
		if (cData.countStaticLocation > 30)
			factor += 3.0f;
		else
			factor += cData.countStaticLocation * 0.10f;
		log.debug("Computed factor: " + factor);

		return factor;
	}

	/**
	 * On location update.
	 * 
	 * @param location the location
	 */
	@Override
	public void onLocationUpdate(Location location) {
		// If the location has a good enough accuracy, stop the waiting.
		if (location.getAccuracy() < CoordinatesLocation.MIN_ACTIVATION_DISTANCE / 2)
			isDone = true;
	}
}
