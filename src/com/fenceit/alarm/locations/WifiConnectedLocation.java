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

import com.fenceit.R;
import com.fenceit.provider.ContextData;
import com.fenceit.provider.WifiConnectedContextData;

/**
 * The Class WifiConnectedLocation that stores information about a location defined on the currently connected
 * Wi-Fi network. By default, it uses the SSID of a network for matching, but can be configured to use a
 * networks BSSID, thus identifying a network more accurately.
 */
@DatabaseClass
public class WifiConnectedLocation extends AbstractAlarmLocation implements Serializable {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4132473462124763216L;

	/** The bssid. */
	@DatabaseField
	private String bssid;

	/**
	 * The ssid. By default this is used for matching, but behaviour can be changed with the
	 * {@link WifiConnectedLocation#matchWithBssid} field.
	 */
	@DatabaseField
	private String ssid;

	/** The Constant tableName. */
	public static final String tableName = "wificonn_locations";

	/**
	 * This field marks whether this location uses the BSSID of a network for identification instead of the
	 * SSID. Default value is false.
	 */
	@DatabaseField
	private boolean matchWithBssid = false;

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

	/**
	 * Checks if this location uses the BSSID of a network for identification instead of the SSID. Default
	 * value is false.
	 * 
	 * @return true, if is using BSSID for matching
	 */
	public boolean isMatchWithBssid() {
		return matchWithBssid;
	}

	/**
	 * Sets if this location uses the BSSID of a network for identification instead of the SSID. Default value
	 * is false.
	 * 
	 * @param matchWithBssid the new match with BSSID value
	 */
	public void setMatchWithBssid(boolean matchWithBssid) {
		this.matchWithBssid = matchWithBssid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#isInside(com.fenceit.alarm.ContextInfo)
	 */
	@Override
	public Status checkStatus(ContextData info) {
		WifiConnectedContextData data = (WifiConnectedContextData) info;
		if (data == null || data.connectedWifiInfo == null)
			return Status.UNKNOWN;
		// Check current status
		boolean isInside = false;
		if (matchWithBssid) {
			if (data.connectedWifiInfo.getBSSID() != null && data.connectedWifiInfo.getBSSID().equals(this.bssid))
				isInside = true;
		} else {
			if (data.connectedWifiInfo.getSSID() != null && data.connectedWifiInfo.getSSID().equals(this.ssid))
				isInside = true;
		}

		// Check previous status
		boolean wasInside = false;
		if (matchWithBssid) {
			if (data.prevConnectedBSSID != null && data.prevConnectedBSSID.equals(this.bssid))
				wasInside = true;
		} else {
			if (data.prevConnectedSSID != null && data.prevConnectedSSID.equals(this.ssid))
				wasInside = true;
		}

		// Compute the status
		if (isInside) {
			if (wasInside)
				return Status.STAYED_INSIDE;
			else
				return Status.ENTERED;
		} else {
			if (wasInside)
				return Status.LEFT;
			else
				return Status.STAYED_OUTSIDE;
		}
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getDescription()
	 */
	@Override
	public String getDescription() {
		if (matchWithBssid)
			return "BSSID: " + this.bssid;
		else
			return "SSID: " + this.ssid;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getTypeDescription()
	 */
	@Override
	public String getTypeDescription() {
		return "Connected Wifi";
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getType()
	 */
	@Override
	public LocationType getType() {
		return LocationType.WifiConnectedLocation;
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#isComplete()
	 */
	@Override
	public boolean isComplete() {
		if ((matchWithBssid && bssid == null) || (!matchWithBssid && ssid == null))
			return false;
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "WifiConnectedLocation [bssid=" + bssid + ", ssid=" + ssid + "]";
	}

	/*
	 * (non-Javadoc)
	 * @see com.fenceit.alarm.locations.AlarmLocation#getTypeImageResource()
	 */
	@Override
	public int getTypeImageResource() {
		return R.drawable.ic_location_wifi_connected;
	}

	@Override
	public AbstractAlarmLocation copy() {
		WifiConnectedLocation newLocation = new WifiConnectedLocation();
		newLocation.bssid=this.bssid;
		newLocation.matchWithBssid=this.matchWithBssid;
		newLocation.ssid=this.ssid;
		newLocation.name = this.name;
		newLocation.favorite = this.favorite;
		newLocation.id = this.id;
		return newLocation;
	}
}
