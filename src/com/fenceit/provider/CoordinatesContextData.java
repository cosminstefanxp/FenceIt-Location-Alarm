/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.location.Location;

/**
 * The Class CoordinatesContextData.
 */
public class CoordinatesContextData implements ContextData {

	/** The location. */
	public Location location;

	/** The prev latitude. */
	public double prevLatitude;

	/** The prev longitude. */
	public double prevLongitude;

	@Override
	public String toString() {
		return "CoordinatesContextData [location=" + location + "]";
	}
}
