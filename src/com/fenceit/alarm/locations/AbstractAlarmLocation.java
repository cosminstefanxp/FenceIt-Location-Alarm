/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

/**
 * An abstract implementation of the AlarmLocation.
 */
public abstract class AbstractAlarmLocation implements AlarmLocation {

	/** The id. */
	protected long id;

	/* (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getId()
	 */
	public long getId() {
		return id;
	}
}
