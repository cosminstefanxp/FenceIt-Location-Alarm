/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;

import com.fenceit.alarm.ContextInfo;

/**
 * The Class WifiConnectedLocation.
 */
@DatabaseClass
public class WifiConnectedLocation extends AbstractAlarmLocation {

	/** The bssid. */
	@DatabaseField
	private String bssid;

	/**
	 * Instantiates a new wifi connected location.
	 */
	public WifiConnectedLocation() {
		super();
	}

	/**
	 * Gets the bssid.
	 * 
	 * @return the bssid
	 */
	public String getBssid() {
		return bssid;
	}

	/**
	 * Sets the bssid.
	 * 
	 * @param bssid the new bssid
	 */
	public void setBssid(String bssid) {
		this.bssid = bssid;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#isInside(com.fenceit.alarm.ContextInfo) */
	@Override
	public boolean isInside(ContextInfo info) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getDescription() */
	@Override
	public String getDescription() {
		return "BSSID: " + this.bssid;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getTypeDescription() */
	@Override
	public String getTypeDescription() {
		return "Connected Wifi";
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#getType() */
	@Override
	public LocationType getType() {
		return LocationType.WifiConnectedLocation;
	}

}
