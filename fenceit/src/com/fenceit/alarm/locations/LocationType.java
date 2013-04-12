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

	/** The Geographical Coordinates location, based on geographical coordinates. */
	GeoCoordinatesLocation(0),
	/** A location based on the connected Wifi. */
	WifiConnectedLocation(1),
	/** A location based on the detected Wifi(s) network(s). */
	WifisDetectedLocation(2),
	/** A location based on the visible Cell Network Sites and the Cell Site connected to. */
	CellNetworkLocation(3), 
	/** A favorite existing location. */
	FavoriteExistingLocation(-1);

	/** The id. */
	private int id;

	/**
	 * Instantiates a new location type.
	 * 
	 * @param id the id
	 */
	private LocationType(int id) {
		this.id = id;
	}

	/**
	 * Gets the id of the enum.
	 * 
	 * @return the id
	 */
	public int getId() {
		return this.id;
	}

}
