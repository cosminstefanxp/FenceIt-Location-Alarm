/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.alarm.locations;

import java.io.Serializable;

import org.androwrapee.db.DatabaseClass;
import org.androwrapee.db.DatabaseField;

import com.fenceit.provider.ContextData;

/**
 * The Class WifiConnectedLocation.
 */
@DatabaseClass
public class WifiConnectedLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4132473462124763216L;

	/** The bssid. */
	@DatabaseField
	private String bssid;

	/** The ssid. */
	@DatabaseField
	private String ssid;

	/** The Constant tableName. */
	public static final String tableName = "wificonn_locations";

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

	/**
	 * Gets the ssid.
	 * 
	 * @return the ssid
	 */
	public String getSsid() {
		return ssid;
	}

	/**
	 * Sets the ssid.
	 * 
	 * @param ssid the new ssid
	 */
	public void setSsid(String ssid) {
		this.ssid = ssid;
	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#isInside(com.fenceit.alarm.ContextInfo) */
	@Override
	public boolean isInside(ContextData info) {
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

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.alarm.locations.AlarmLocation#isComplete() */
	@Override
	public boolean isComplete() {
		if (bssid == null)
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "WifiConnectedLocation [bssid=" + bssid + ", ssid=" + ssid + "]";
	}

}
