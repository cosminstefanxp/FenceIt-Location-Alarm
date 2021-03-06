/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.List;

import android.content.Context;

import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.CellContextData;
import com.fenceit.provider.CellDataProvider;
import com.fenceit.provider.ContextData;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The CellTriggerCheckerThread handles the check for conditions regarding the
 * currently connected cell network (the alarm locations of type
 * {@link CellNetworkLocation}. If any of the alarms should be triggered, it
 * handles that.
 */
public class CellTriggerCheckerThread extends TriggerCheckerThread {

	/**
	 * Instantiates a new trigger checker thread.
	 * 
	 * @param context the context
	 * @param handler the handler for the main thread
	 */
	public CellTriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super(context, handler, eventType);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData()
	 */
	@Override
	protected List<? extends AlarmTrigger> fetchData() {
		return DatabaseAccessor.buildFullTriggersForEnabledLocationType(mContext,
				LocationType.CellNetworkLocation);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#acquireContextData()
	 */
	@Override
	protected ContextData acquireContextData() {
		ContextData data = CellDataProvider.getCellContextData(mContext, true);
		return data;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid()
	 */
	@Override
	protected boolean isPreconditionValid() {
		// Check for availability;
		if (!CellDataProvider.isCellNetworkConnected(mContext)) {
			log.warn("Not connected to Cell Network. Cannot check if the triggering conditions are met for locations requiring Cell contextual data.");
			return false;
		}

		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fenceit.service.checkers.TriggerCheckerThread#computeNextCheckTime()
	 */
	@Override
	protected Float computeDelayFactor(List<? extends AlarmTrigger> triggers, ContextData data) {

		if (triggers.isEmpty())
			return null;

		CellContextData cData = (CellContextData) data;
		Float factor = 1.0f;
		// If in the same position for a lot of time, increase the factor, but
		// up to 300%
		if (cData.countStaticLocation > 40)
			factor += 2.0f;
		else
			factor += cData.countStaticLocation * 0.05f;
		log.debug("Computed factor: " + factor);

		return factor;
	}
}
