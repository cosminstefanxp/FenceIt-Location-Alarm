/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.ContextInfo;
import com.fenceit.alarm.fence.Fence;

/**
 * A basic implementation of the trigger.
 */
public class BasicTrigger extends AbstractAlarmTrigger {

	/** The location. */
	Fence fence;

	/**
	 * The Enum TriggerType.
	 */
	public enum TriggerType {
		/** The triggering occurs when entering the Location. */
		ON_ENTER,
		/** The triggering occurs when exiting the Location. */
		ON_EXIT,
		/** The triggering occurs when inside the Location. */
		INSIDE
	};

	/**
	 * Instantiates a new coordinates trigger.
	 * 
	 * @param alarm the alarm
	 * @param data the data
	 */
	public BasicTrigger(Alarm alarm, Fence fence) {
		super(alarm);
		this.fence=fence;
		
	}

	/* (non-Javadoc)
	 * 
	 * @see
	 * com.fenceit.alarm.triggers.AbstractAlarmTrigger#shouldTrigger(com.fenceit.alarm.EnvironmentData
	 * ) */
	@Override
	public boolean shouldTrigger(ContextInfo data) {
		// TODO Auto-generated method stub
		return false;
	}

}
