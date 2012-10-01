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
import android.util.Log;

/**
 * The WifiConnectedDataProvider gathers information about the Wifi Network currently connected to.
 */
public class WifiConnectedDataProvider {

	/** The Constant PREV_CONNECTED_WIFI_BSSID. */
	private static final String PREV_CONNECTED_WIFI_BSSID = "connected_wifi_bssid";

	/** The Constant PREV_CONNECTED_WIFI_SSID. */
	private static final String PREV_CONNECTED_WIFI_SSID = "connected_wifi_ssid";

	/** The Constant PREV_CONNECTED_WIFI_STATIC. */
	private static final String PREV_CONNECTED_WIFI_STATIC = "connected_wifi_static";

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
		data.prevConnectedBSSID = sp.getString(PREV_CONNECTED_WIFI_BSSID, null);
		data.prevConnectedSSID = sp.getString(PREV_CONNECTED_WIFI_SSID, null);

		// Save current conditions for later
		if (storeLast) {
			// Count how many times the device stayed in the same position (the same BSSID)
			int count = sp.getInt(PREV_CONNECTED_WIFI_STATIC, 0);
			if (data.connectedWifiInfo.getBSSID() != null
					&& data.connectedWifiInfo.getBSSID().equals(data.prevConnectedBSSID))
				count++;
			else
				count = 0;
			data.countStaticLocation = count;
			Log.d("WifiConnectedDataProvider", "In same position for: " + count);

			// Store
			Editor ed = sp.edit();
			ed.putString(PREV_CONNECTED_WIFI_BSSID, data.connectedWifiInfo.getBSSID());
			ed.putString(PREV_CONNECTED_WIFI_SSID, data.connectedWifiInfo.getSSID());
			ed.putInt(PREV_CONNECTED_WIFI_STATIC, count);
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
