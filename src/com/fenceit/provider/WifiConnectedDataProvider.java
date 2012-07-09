/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * The WifiConnectedDataProvider gathers information about the Wifi Network currently connected to.
 */
public class WifiConnectedDataProvider {

	/** The Constant PREV_CONNECTED_WIFI_PREF. */
	private static final String PREV_CONNECTED_WIFI_PREF = "connected_wifi";

	/**
	 * Gets the currently connected wifi info.
	 * 
	 * @param context the context
	 * @return the currently connected wifi info
	 */
	public static WifiInfo getConnectionWifiInfo(Context context) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return m.getConnectionInfo();
	}

	/**
	 * Gets the wifi connected context data.
	 * 
	 * @param context the context
	 * @param storeLast whether to store the current context as "last" for previous queries.
	 * @return the wifi context data
	 */
	public static WifiConnectedContextData getWifiContextData(Context context, boolean storeLast) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiConnectedContextData data = new WifiConnectedContextData();

		// Get details regarding current conditions
		data.connectedWifiInfo = m.getConnectionInfo();

		// Get previous conditions
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		data.prevConnectedBSSID = sp.getString(PREV_CONNECTED_WIFI_PREF, null);

		// Save current conditions for later
		if (storeLast) {
			Editor ed = sp.edit();
			ed.putString(PREV_CONNECTED_WIFI_PREF, data.connectedWifiInfo.getBSSID());
			ed.commit();
		}

		return data;
	}

	/**
	 * Checks if is wifi available.
	 * 
	 * @param context the context
	 * @return true, if is wifi available
	 */
	public static boolean isWifiAvailable(Context context) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return m.isWifiEnabled();
	}
}
