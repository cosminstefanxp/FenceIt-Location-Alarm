/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.actions;

import com.fenceit.alarm.Alarm;
import com.fenceit.alarm.AlarmAction;

/**
 * The Class AbstractAlarmAction that is a basic abstract implementation for an AlarmAction.
 */
public abstract class AbstractAlarmAction implements AlarmAction {

	/** The alarm. */
	@SuppressWarnings("unused")
	private Alarm alarm;
	
	/**
	 * Instantiates a new abstract alarm action.
	 *
	 * @param alarm the alarm
	 */
	public AbstractAlarmAction(Alarm alarm) {
		super();
		this.alarm = alarm;
	}

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.AlarmAction#execute()
	 */
	@Override
	public abstract void execute();
}
