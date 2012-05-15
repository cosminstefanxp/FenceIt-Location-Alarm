/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.ContextInfo;
import com.fenceit.db.ParentField;
import com.fenceit.db.Transient;

/**
 * The Class AbstractAlarmTrigger that is a basic abstract implementation for an AlarmTrigger.
 */
public abstract class AbstractAlarmTrigger implements AlarmTrigger {
	
	/** The alarm. */
	@ParentField
	protected Alarm alarm;

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
	public abstract boolean shouldTrigger(ContextInfo info);

}
