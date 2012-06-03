/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import org.apache.log4j.Logger;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

/**
 * The WifiDataProvider gathers information about the Wifi Networks.
 */
public class WifiDataProvider {

	/** The log. */
	private static Logger log = Logger.getLogger(WifiDataProvider.class);

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
		data.connectedWifiInfo = m.getConnectionInfo();
		data.scanResults = m.getScanResults();

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
