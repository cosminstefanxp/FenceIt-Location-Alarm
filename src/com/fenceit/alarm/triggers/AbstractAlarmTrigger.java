/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.AlarmTrigger;
import com.fenceit.alarm.EnvironmentData;

/**
 * The Class AbstractAlarmTrigger that is a basic abstract implementation for an AlarmTrigger.
 */
public abstract class AbstractAlarmTrigger implements AlarmTrigger {
	
	/** The alarm. */
	@SuppressWarnings("unused")
	private Alarm alarm;

	/**
	 * Instantiates a new alarm trigger.
	 * 
	 * @param alarm the alarm
	 */
	public AbstractAlarmTrigger(Alarm alarm) {
		this.alarm = alarm;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.AlarmTrigger#shouldTrigger(com.fenceit.alarm.EnvironmentData) */
	@Override
	public abstract boolean shouldTrigger(EnvironmentData data);

}
