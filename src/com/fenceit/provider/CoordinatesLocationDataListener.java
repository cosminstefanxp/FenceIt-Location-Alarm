/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.location.Location;

/**
 * The listener interface for receiving events regarding a new Location with Geographical
 * Coordinates. The class that is interested in processing a coordinatesLocationData event
 * implements this interface, and the object created with that class is registered with a component
 * using the component's <code>addCoordinatesLocationDataListener<code> method. When
 * the coordinatesLocationData event occurs, that object's appropriate
 * method is invoked.
 * 
 * @see CoordinatesLocationDataEvent
 */
public interface CoordinatesLocationDataListener {


	/**
	 * On location update.
	 *
	 * @param location the location
	 */
	public void onLocationUpdate(Location location);
}
