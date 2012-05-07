/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.fence;

import com.fenceit.alarm.ContextInfo;

/**
 * A Fence implementation that is defined using the geographical coordinates of a point on earth.
 */
public class SimpleCoordinatesFence implements Fence {

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

}
