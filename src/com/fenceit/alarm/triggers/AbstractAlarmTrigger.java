/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.IdField;
import org.androwrapee.db.ParentField;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.ContextInfo;

/**
 * The Class AbstractAlarmTrigger that is a basic abstract implementation for an AlarmTrigger.
 */
@DatabaseClass
public abstract class AbstractAlarmTrigger implements AlarmTrigger {
	
	/** The alarm. */
	@ParentField
	protected Alarm alarm;
	
	/** The id. */
	@IdField
	protected long id;

	/**
	 * Instantiates a new alarm trigger.
	 * 
	 * @param alarm the alarm
	 */
	public AbstractAlarmTrigger(Alarm alarm) {
		this.alarm = alarm;
	}

	/**
	 * Gets the id.
	 *
	 * @return the id
	 */
	final long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 *
	 * @param id the id to set
	 */
	final void setId(long id) {
		this.id = id;
	}
	
	
	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.AlarmTrigger#shouldTrigger(com.fenceit.alarm.EnvironmentData) */
	@Override
	public abstract boolean shouldTrigger(ContextInfo info);

}
