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
import com.fenceit.db.Transient;

/**
 * A basic implementation of the trigger.
 */
public class BasicTrigger extends AbstractAlarmTrigger {

	/** The Constant tableName. */
	@Transient
	public static final String tableName = "triggers";
	
	/** The id. */
	private long id;

	/** The location. */
	@Transient
	private Fence fence;

	/** The type of the triggering. */
	private TriggerType type;

	/**
	 * The Enum TriggerType.
	 */
	public enum TriggerType {
		/** The triggering occurs when entering the Location. */
		ON_ENTER,
		/** The triggering occurs when exiting the Location. */
		ON_EXIT,
	};

	/**
	 * Instantiates a new coordinates trigger.
	 * 
	 * @param alarm the alarm
	 */
	public BasicTrigger(Alarm alarm) {
		super(alarm);
		this.fence = null;
		this.type = TriggerType.ON_ENTER;
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

	/**
	 * Gets the fence.
	 * 
	 * @return the fence
	 */
	public Fence getFence() {
		return fence;
	}

	/**
	 * Sets the fence.
	 * 
	 * @param fence the fence to set
	 */
	public void setFence(Fence fence) {
		this.fence = fence;
	}

	/**
	 * Gets the type.
	 * 
	 * @return the type
	 */
	public TriggerType getType() {
		return type;
	}

	/**
	 * Sets the type.
	 * 
	 * @param type the type to set
	 */
	public void setType(TriggerType type) {
		this.type = type;
	}

}
