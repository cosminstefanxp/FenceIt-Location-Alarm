/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service.checkers;

import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;

import android.content.Context;
import android.os.Message;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifiDataProvider;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The WifisDetectedTriggerCheckerThread handles the check for conditions regarding the Wifi
 * networks in range (detected) - the alarm locations of types {@link WifisDetectedLocation}. If any
 * of the alarms should be triggered, it also handles the triggering.
 */
public class WifisDetectedTriggerCheckerThread extends TriggerCheckerThread {

	/**
	 * The time when this thread was last run, in milliseconds since 1970 (as returned by
	 * Calendar.getTimeInMillis(). It is used to control the frequency of running this checker.
	 */
	private static long lastRun = 0;

	/**
	 * The Constant minDelay that defines the minimum time between two runs of this Checker (in
	 * milliseconds).
	 */
	private static final int MIN_DELAY_BETWEEN_CHECKS = 20000;

	/**
	 * Instantiates a new wifi service checker thread.
	 * 
	 * @param context the context
	 * @param handler the handler for the main thread
	 */
	public WifisDetectedTriggerCheckerThread(Context context, BackgroundServiceHandler handler, int eventType) {
		super(context, handler, eventType);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#fetchData() */
	@Override
	protected List<AlarmTrigger> fetchData() {
		List<Alarm> alarms = DatabaseAccessor.buildFullAlarms(mContext.getApplicationContext(), "enabled='t'");
		List<AlarmTrigger> triggers = new LinkedList<AlarmTrigger>();
		// Prepare only the triggers that have locations of the required type
		for (Alarm a : alarms) {
			for (AlarmTrigger t : a.getTriggers())
				if (t.getLocation().getType() == LocationType.WifisDetectedLocation)
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
		log.warn("An alarm was triggered because of: " + trigger);
		Message m = mParentHandler.obtainMessage(BackgroundServiceHandler.HANDLER_NOTIFICATION);
		m.obj = "The alarm '" + trigger.getAlarm().getName() + "' was triggered because of trigger " + trigger.getId();
		mParentHandler.sendMessage(m);
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#acquireContextData() */
	@Override
	protected ContextData acquireContextData() {
		ContextData data = WifiDataProvider.getWifiContextData(mContext, true);
		return data;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid() */
	@Override
	protected boolean isPreconditionValid() {
		// Check if it is not run too often
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if (currentTime - lastRun < MIN_DELAY_BETWEEN_CHECKS) {
			log.warn("Last Wifis Detected Checker run was only " + (currentTime - lastRun)
					+ " ms ago, so this run is skipped.");
			return false;
		}

		// Check for availability;
		if (!WifiDataProvider.isWifiAvailable(mContext)) {
			log.warn("Wifi is not enabled. Cannot check if the triggering conditions are met for locations requiring Wifi contextual data.");
			return false;
		}

		lastRun = currentTime;
		return true;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.checkers.TriggerCheckerThread#computeNextCheckTime() */
	@Override
	protected Long computeNextCheckTime(List<AlarmTrigger> triggers) {

		// If no triggers of this type, no rescheduling
		if (triggers == null || triggers.isEmpty())
			return null;

		return 30000L;
	}
}
