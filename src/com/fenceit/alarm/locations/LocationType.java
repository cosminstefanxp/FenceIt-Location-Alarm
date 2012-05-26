/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

/**
 * The possible Location Types.
 */
public enum LocationType {

	/** The Coordinates location, based on geographical coordinates. */
	CoordinatesLocation,
	/** A location based on the connected Wifi. */
	WifiConnectedLocation,
	/** A location based on the visible Wifi(s). */
	WifiVisibleLocation,
	/** A location based on the visible Phone Tower Cells. */
	TowerCellLocation
}
