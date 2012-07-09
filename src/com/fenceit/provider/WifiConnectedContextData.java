/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.net.wifi.WifiInfo;

/**
 * The WifiConnectedContextData contains info regarding the Wireless interface and the Wifi network
 * connected to.
 */
public class WifiConnectedContextData implements ContextData {

	/** The wifi info for the connected Wifi network. */
	public WifiInfo connectedWifiInfo;

	/** The last connected bssid. */
	public String prevConnectedBSSID;
	
	/** The count static location. */
	public int countStaticLocation;

	/* (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString() */
	@Override
	public String toString() {
		return "WifiConnectedContextData [connectedWifiInfo=" + connectedWifiInfo + ", prevConnectedBSSID="
				+ prevConnectedBSSID + "]";
	}

}
