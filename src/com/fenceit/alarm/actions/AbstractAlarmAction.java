/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import java.io.Serializable;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.IdField;
import org.androwrapee.db.ReferenceField;

import com.fenceit.alarm.Alarm;

/**
 * The Class AbstractAlarmAction that is a basic abstract implementation for an AlarmAction.
 */
@DatabaseClass
public abstract class AbstractAlarmAction implements AlarmAction, Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -6717518638709984739L;

	/** The alarm. */
	@ReferenceField
	protected Alarm alarm;

	/** The id. */
	@IdField
	protected long id;

	/**
	 * Instantiates a new abstract alarm action.
	 * 
	 * @param alarm the alarm
	 */
	public AbstractAlarmAction(Alarm alarm) {
		super();
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
