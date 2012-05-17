/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.triggers;

import java.io.Serializable;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.IdField;
import org.androwrapee.db.ParentField;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.ContextInfo;

/**
 * The Class AbstractAlarmTrigger that is a basic abstract implementation for an AlarmTrigger.
 */
@DatabaseClass
public abstract class AbstractAlarmTrigger implements AlarmTrigger, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 8405685119043497423L;

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
	public long getId() {
		return id;
	}

	/**
	 * Sets the id.
	 * 
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.AlarmTrigger#shouldTrigger(com.fenceit.alarm.EnvironmentData) */
	@Override
	public abstract boolean shouldTrigger(ContextInfo info);
	

	/**
	 * Gets the alarm.
	 * 
	 * @return the alarm
	 */
	public Alarm getAlarm() {
		return alarm;
	}

	/**
	 * Sets the alarm.
	 * 
	 * @param alarm the alarm to set
	 */
	public void setAlarm(Alarm alarm) {
		this.alarm = alarm;
	}

}
