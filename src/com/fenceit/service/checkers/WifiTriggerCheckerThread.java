/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifiDataProvider;

/**
 * The WifiServiceThread handles the check for conditions regarding the currently connected Wifi
 * network and the Wifi networks in range (the alarm locations of types.
 * 
 * {@link WifiConnectedLocation} and ). If any of the alarms should be triggered, it does that...
 */
public class WifiTriggerCheckerThread extends TriggerCheckerThread {

	/**
	 * Instantiates a new wifi service thread.
	 * 
	 * @param context the context
	 */
	public WifiTriggerCheckerThread(Context context) {
		super(context);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData() */
	@Override
	protected List<AlarmTrigger> fetchData() {
		List<Alarm> alarms = DatabaseAccessor.buildFullAlarms(mContext, "enabled='t'");
		List<AlarmTrigger> triggers = new LinkedList<AlarmTrigger>();
		// Prepare only the triggers that have locations of the required type
		for (Alarm a : alarms) {
			for (AlarmTrigger t : a.getTriggers())
				if (t.getLocation().getType() == LocationType.WifiConnectedLocation)
					triggers.add(t);
		}
		return triggers;
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * com.fenceit.service.TriggerCheckerThread#triggerAlarm(com.fenceit.alarm.triggers.AlarmTrigger
	 * ) */
	@Override
	protected void triggerAlarm(AlarmTrigger trigger) {
		log.warn("An alarm was triggered: " + trigger);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#acquireContextData() */
	@Override
	protected ContextData acquireContextData() {
		ContextData data = WifiDataProvider.getWifiContextData(mContext);
		return data;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid() */
	@Override
	protected boolean isPreconditionValid() {
		// Check for availability;
		if (!WifiDataProvider.isWifiAvailable(mContext)) {
			log.warn("Wifi is not enabled. Cannot check if the triggering conditions are met for locations requiring Wifi contextual data.");
			return false;
		}

		return true;
	}
}
