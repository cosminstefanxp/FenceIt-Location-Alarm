/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import com.fenceit.provider.ContextData;

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



	@Override
	public String getDescription() {
		return String.format("Lat: %.4f, Long: %.4f", latitude, longitude);
	}

	@Override
	public String getTypeDescription() {
		return "Coordinates";
	}

	@Override
	public LocationType getType() {
		return LocationType.CoordinatesLocation;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isComplete() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Status checkStatus(ContextData info) {
		// TODO Auto-generated method stub
		return null;
	}

}
