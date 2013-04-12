/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.List;

import android.content.Context;

import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifiConnectedContextData;
import com.fenceit.provider.WifiConnectedDataProvider;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The WifiServiceThread handles the check for conditions regarding the currently connected Wifi network (the
 * alarm locations of type {@link WifiConnectedLocation}. If any of the alarms should be triggered, it does
 * that...
 */
public class WifiConnectedTriggerCheckerThread extends TriggerCheckerThread {

	/**
	 * Instantiates a new wifi service thread.
	 * 
	 * @param context the context
	 * @param handler the handler for the main thread
	 */
	public WifiConnectedTriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super(context, handler, eventType);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData()
	 */
	@Override
	protected List<? extends AlarmTrigger> fetchData() {
		return DatabaseAccessor.buildFullTriggersForEnabledLocationType(mContext, LocationType.WifiConnectedLocation);
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#acquireContextData()
	 */
	@Override
	protected ContextData acquireContextData() {
		ContextData data = WifiConnectedDataProvider.getWifiContextData(mContext, true);
		return data;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid()
	 */
	@Override
	protected boolean isPreconditionValid() {

		// Check for availability;
		if (!WifiConnectedDataProvider.isWifiAvailable(mContext)) {
			log.warn("Wifi is not enabled. Cannot check if the triggering conditions are met for locations requiring Wifi contextual data.");
			return false;
		}

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

		WifiConnectedContextData cData = (WifiConnectedContextData) data;
		Float factor = 1.0f;
		// If in the same position for a lot of time, increase the factor, but up to 300%
		if (cData.countStaticLocation > 40)
			factor += 2.0f;
		else
			factor += cData.countStaticLocation * 0.05f;
		log.debug("Computed factor: " + factor);

		return factor;
	}
}
