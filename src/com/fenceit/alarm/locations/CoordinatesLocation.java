/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import com.fenceit.alarm.ContextInfo;

/**
 * An AlarmLocation implementation that is defined using the geographical coordinates of a point on
 * earth.
 */
public class CoordinatesLocation extends AbstractAlarmLocation {

	/** The longitude. */
	double longitude;

	/** The latitude. */
	double latitude;

	/** The activation distance. */
	double activationDistance;

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.Location#isInside(com.fenceit.alarm.ContextInfo) */
	@Override
	public boolean isInside(ContextInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public String getDescription() {
		return String.format("Lat: %.4f, Long: %.4f", latitude, longitude);
	}

	@Override
	public String getTypeDescription() {
		return "Coordinates";
	}
	

}
