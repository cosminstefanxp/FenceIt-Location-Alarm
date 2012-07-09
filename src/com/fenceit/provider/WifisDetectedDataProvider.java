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
import android.content.SharedPreferences.Editor;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.preference.PreferenceManager;

/**
 * The WifisDetectedDataProvider gathers information about the Wifi Networks in range.
 */
public class WifisDetectedDataProvider {

	/** The Constant PREV_DETECTED_WIFIs_PREF. */
	private static final String PREV_DETECTED_WIFIS_PREF = "detected_bssids";

	/** The Constant SPLITTER. */
	private static final String SPLITTER = ";";

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
	 * Gets the wifis detected context data.
	 * 
	 * @param context the context
	 * @param storeLast whether to store the current context as "last" for previous queries.
	 * @return the wifi context data
	 */
	public static WifisDetectedContextData getWifiContextData(Context context, boolean storeLast) {
		WifiManager m = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
		WifisDetectedContextData data = new WifisDetectedContextData();

		// Get details regarding current conditions
		data.scanResults = m.getScanResults();

		// Get previous conditions
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		data.prevScanBSSIDs = sp.getString(PREV_DETECTED_WIFIS_PREF, "").split(SPLITTER);

		// Save current conditions for later
		if (storeLast) {
			Editor ed = sp.edit();
			StringBuilder out = new StringBuilder();
			for (ScanResult res : data.scanResults) {
				out.append(res.BSSID);
				out.append(SPLITTER);
			}
			ed.putString(PREV_DETECTED_WIFIS_PREF, out.toString());

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
