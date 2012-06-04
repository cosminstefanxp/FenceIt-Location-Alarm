/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import org.apache.log4j.Logger;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * The WifiDataProvider gathers information about the Wifi Networks.
 */
public class WifiDataProvider {

	/** The log. */
	private static Logger log = Logger.getLogger(WifiDataProvider.class);

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
	 * Gets the wifi context data.
	 * 
	 * @param context the context
	 * @return the wifi context data
	 */
	public static WifiContextData getWifiContextData(Context context) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiContextData data = new WifiContextData();

		// Get details regarding current conditions
		data.connectedWifiInfo = m.getConnectionInfo();
		data.scanResults = m.getScanResults();

		// Get previous conditions
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		data.prevConnectedBSSID = sp.getString(PREV_CONNECTED_WIFI_PREF, null);

		// Save current conditions for later
		sp.edit().putString(PREV_CONNECTED_WIFI_PREF, data.connectedWifiInfo.getBSSID()).commit();

		return data;
	}

	// /**
	// * Gets the network info.
	// *
	// * @param context the context
	// * @return the network info
	// */
	// public static NetworkInfo getNetworkInfo(Context context) {
	// ConnectivityManager connectivityManager = (ConnectivityManager) context
	// .getSystemService(Context.CONNECTIVITY_SERVICE);
	// NetworkInfo wifiInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	//
	// return wifiInfo;
	// }

	/**
	 * Checks if is wifi available.
	 * 
	 * @param context the context
	 * @return true, if is wifi available
	 */
	public static boolean isWifiAvailable(Context context) {
		// NetworkInfo ni = getNetworkInfo(context);
		// log.debug("Network info: " + ni);
		// return ni.isAvailable();
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return m.isWifiEnabled();
	}
}
