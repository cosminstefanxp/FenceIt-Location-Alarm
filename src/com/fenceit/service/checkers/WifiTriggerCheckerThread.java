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
import android.os.Message;

import com.fenceit.alarm.Wifi;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.alarm.triggers.BasicTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifiDataProvider;
import com.fenceit.service.BackgroundServiceHandler;

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
	 * @param handler the handler for the main thread
	 */
	public WifiTriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super(context, handler, eventType);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData() */
	@Override
	protected List<AlarmTrigger> fetchData() {
		List<Wifi> alarms = DatabaseAccessor.buildFullAlarms(mContext.getApplicationContext(), "enabled='t'");
		List<AlarmTrigger> triggers = new LinkedList<AlarmTrigger>();
		// Prepare only the triggers that have locations of the required type
		for (Wifi a : alarms) {
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
		BasicTrigger t = (BasicTrigger) trigger;
		Message m = mParentHandler.obtainMessage(BackgroundServiceHandler.HANDLER_NOTIFICATION, (int) t.getAlarm()
				.getId(), (int) t.getId());
		mParentHandler.sendMessage(m);
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

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.checkers.TriggerCheckerThread#computeNextCheckTime() */
	@Override
	protected Long computeNextCheckTime(List<AlarmTrigger> triggers) {

		if (triggers.isEmpty())
			return null;

		// TODO: Temporary, fixed check time
		return 30000L;
	}
}
