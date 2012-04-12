/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.EnvironmentData;

/**
 * The Class CoordinatesTrigger.
 */
public class CoordinatesTrigger extends AbstractAlarmTrigger {

	/**
	 * Instantiates a new coordinates trigger.
	 *
	 * @param alarm the alarm
	 */
	public CoordinatesTrigger(Alarm alarm, EnvironmentData data) {
		super(alarm);
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.triggers.AbstractAlarmTrigger#shouldTrigger(com.fenceit.alarm.EnvironmentData)
	 */
	@Override
	public boolean shouldTrigger(EnvironmentData data) {
		// TODO Auto-generated method stub
		return false;
	}

}
