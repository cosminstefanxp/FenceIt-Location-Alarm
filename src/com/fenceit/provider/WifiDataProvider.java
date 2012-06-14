/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * The WifiDataProvider gathers information about the Wifi Networks.
 */
public class WifiDataProvider {

	/** The Constant PREV_CONNECTED_WIFI_PREF. */
	private static final String PREV_CONNECTED_WIFI_PREF = "connected_wifi";

	/** The Constant PREV_DETECTED_WIFIs_PREF. */
	private static final String PREV_DETECTED_WIFIS_PREF = "detected_bssids";

	/** The Constant SPLITTER. */
	private static final String SPLITTER = ";";

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
	 * Start a scan.
	 * 
	 * @param context the context
	 */
	public static void startScan(Context context) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		m.startScan();
	}

	/**
	 * Gets the scan results.
	 * 
	 * @param context the context
	 * @return the scan results
	 */
	public static List<ScanResult> getScanResults(Context context) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		return m.getScanResults();
	}

	/**
	 * Gets the wifi context data.
	 * 
	 * @param context the context
	 * @param includeScanResults if should include scan results
	 * @return the wifi context data
	 */
	public static WifiContextData getWifiContextData(Context context, boolean includeScanResults) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifiContextData data = new WifiContextData();

		// Get details regarding current conditions
		data.connectedWifiInfo = m.getConnectionInfo();
		if (includeScanResults)
			data.scanResults = m.getScanResults();

		// Get previous conditions
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		data.prevConnectedBSSID = sp.getString(PREV_CONNECTED_WIFI_PREF, null);
		data.prevScanBSSIDs = sp.getString(PREV_DETECTED_WIFIS_PREF, "").split(SPLITTER);

		// Save current conditions for later
		sp.edit().putString(PREV_CONNECTED_WIFI_PREF, data.connectedWifiInfo.getBSSID()).commit();
		StringBuilder out = new StringBuilder();
		for (ScanResult res : data.scanResults) {
			out.append(res.BSSID);
			out.append(SPLITTER);
		}
		sp.edit().putString(PREV_DETECTED_WIFIS_PREF, out.toString()).commit();

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
