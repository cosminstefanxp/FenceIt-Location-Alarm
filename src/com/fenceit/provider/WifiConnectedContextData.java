/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.net.wifi.WifiInfo;

/**
 * The WifiConnectedContextData contains info regarding the currently connected Wifi Network.
 */
public class WifiConnectedContextData implements ContextData {

	/** The wifi info. */
	public WifiInfo wifiInfo;

	/**
	 * Instantiates a new wifi connected context data.
	 *
	 * @param wifiInfo the wifi info
	 */
	public WifiConnectedContextData(WifiInfo wifiInfo) {
		super();
		this.wifiInfo = wifiInfo;
	}
}
