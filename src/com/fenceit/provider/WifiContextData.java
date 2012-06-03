/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;

/**
 * The WifiContextData contains info regarding the Wireless interface and the Wifi networks
 * (connected and in range).
 */
public class WifiContextData implements ContextData {

	/** The wifi info for the connected Wifi network. */
	public WifiInfo connectedWifiInfo;

	/** The scan results of the Wireless networks in range. */
	public List<ScanResult> scanResults;

	@Override
	public String toString() {
		return "WifiContextData [connectedWifiInfo=" + connectedWifiInfo + ", scanResults=" + scanResults + "]";
	}

}
