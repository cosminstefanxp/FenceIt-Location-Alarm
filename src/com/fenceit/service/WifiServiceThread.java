/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.LinkedList;
import java.util.List;

import android.content.Context;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.triggers.AlarmTrigger;
import com.fenceit.db.DatabaseAccessor;

/**
 * The WifiServiceThread handles the check for conditions regarding the currently connected Wifi
 * network and the Wifi networks in range (the alarm locations of types
 * {@link WifiConnectedLocation} and ). If any of the alarms should be triggered, it does that...
 */
public class WifiServiceThread extends Thread {

	/** The triggers. */
	private List<AlarmTrigger> triggers;

	private Context context;

	private WifiServiceThread(Context context) {
		super();
		this.context = context;
	}

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Thread#run() */
	@Override
	public void run() {
		super.run();

		// Initialize the alarms - fetches them from the database
		fetchData();

		// TODO: Check for condition
	}

	/**
	 * Fetches the required data from the database
	 */
	private void fetchData() {
		List<Alarm> alarms = DatabaseAccessor.buildFullAlarms(context, null);
		triggers = new LinkedList<AlarmTrigger>();
		// Prepare only the triggers that have locations of the required type
		for (Alarm a : alarms) {
			for (AlarmTrigger t : a.getTriggers())
				if (t.getLocation().getType() == LocationType.WifiConnectedLocation)
					triggers.add(t);
		}
	}
}
