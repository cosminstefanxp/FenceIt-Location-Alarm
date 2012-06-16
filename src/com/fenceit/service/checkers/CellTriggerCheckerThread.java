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

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.CellLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;
import com.fenceit.provider.CellContextProvider;
import com.fenceit.provider.ContextData;
import com.fenceit.service.BackgroundServiceHandler;

/**
 * The CellTriggerCheckerThread handles the check for conditions regarding the currently connected
 * cell network (the alarm locations of type {@link CellLocation}. If any of the alarms should be
 * triggered, it handles that.
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
				if (t.getLocation().getType() == LocationType.CellLocation)
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
		ContextData data = CellContextProvider.getCellContextData(mContext);
		return data;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.service.TriggerCheckerThread#isPreconditionValid() */
	@Override
	protected boolean isPreconditionValid() {
		// Check for availability;
		if (!CellContextProvider.isCellNetworkConnected(mContext)) {
			log.warn("Not connected to Cell Network. Cannot check if the triggering conditions are met for locations requiring Cell contextual data.");
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
