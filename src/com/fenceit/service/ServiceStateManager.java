/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.service;

import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;

import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.preference.PreferenceManager;

import com.fenceit.alarm.locations.LocationType;
import com.fenceit.db.DatabaseAccessor;

/**
 * The Class ServiceStateManager. A call to {@link ServiceStateManager#updateData() is necessary after
 * creating the object, in order to properly use the object.
 */
public class ServiceStateManager {

	/** The log. */
	private static Logger log = Logger.getLogger(ServiceStateManager.class);

	/** The wifi scan broadcast receiver enabled. */
	private boolean wifiScanBroadcastReceiverEnabled = false;

	/** The registered to wake lock. */
	private boolean registeredToWakeLock = false;

	/**
	 * An set stating whether there's a trigger containing this location type set in any enabled alarm.
	 */
	private HashSet<LocationType> locationTypesEnabled;

	/**
	 * Instantiates a new service state manager. A call to {@link ServiceStateManager#updateData() is
	 * necessary to properly use this.
	 */
	public ServiceStateManager() {
		// Set proper values
		wifiScanBroadcastReceiverEnabled = false;

		locationTypesEnabled = new HashSet<LocationType>();
	}

	/**
	 * Forces the ServiceStateManager to update its internal data, by querying the database.
	 * 
	 * @param context the context
	 */
	public void updateState(Context context) {

		log.info("Updating service state in ServiceStateManager...");

		// Update data regarding enabled location types in the system
		List<LocationType> enabledTypes = DatabaseAccessor.getLocationTypesEnabled(context);
		locationTypesEnabled.clear();
		locationTypesEnabled.addAll(enabledTypes);

		// If there is any WifisDetectedLocation enabled, register the BroadcastReceiver
		if (!wifiScanBroadcastReceiverEnabled && locationTypesEnabled.contains(LocationType.WifisDetectedLocation)) {
			log.info("Enabling WifiScanBroadcastReceiver...");
			ComponentName component = new ComponentName(context, WifiBroadcastReceiver.class);
			context.getPackageManager().setComponentEnabledSetting(component,
					PackageManager.COMPONENT_ENABLED_STATE_ENABLED, PackageManager.DONT_KILL_APP);
			wifiScanBroadcastReceiverEnabled = true;
		}

		// If there is no WifisDetectedLocation enabled, unregister the BroadcastReceiver
		if (wifiScanBroadcastReceiverEnabled && !locationTypesEnabled.contains(LocationType.WifisDetectedLocation)) {
			log.info("Disabling WifiScanBroadcastReceiver...");
			ComponentName component = new ComponentName(context, WifiBroadcastReceiver.class);
			context.getPackageManager().setComponentEnabledSetting(component,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
			wifiScanBroadcastReceiverEnabled = false;
		}
	}

	/**
	 * Checks if a location type is enabled.
	 * 
	 * @param type the type
	 * @return true, if is location type enabled
	 */
	public boolean isLocationTypeEnabled(LocationType type) {
		return locationTypesEnabled.contains(type);
	}

	/**
	 * Unregisters any Broadcast Receiver the application (service) might be registered to.
	 * 
	 * @param context the context
	 */
	public void unregisterReceivers(Context context) {

		if (wifiScanBroadcastReceiverEnabled) {
			log.info("Forcedly disabling WifiScanBroadcastReceiver...");
			ComponentName component = new ComponentName(context, WifiBroadcastReceiver.class);
			context.getPackageManager().setComponentEnabledSetting(component,
					PackageManager.COMPONENT_ENABLED_STATE_DISABLED, PackageManager.DONT_KILL_APP);
		}
	}

	/**
	 * Checks whether the service should run.
	 * 
	 * @param context the context
	 * @return true, if should run
	 */
	public boolean shouldServiceRun(Context context) {
		// Check if the background service is forcedly disabled
		SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
		if (sp.getBoolean("service_status", true) == false) {
			log.info("Background service is disabled, so it should not run...");
			return false;
		}

		// Check if there are any location types enabled
		if (this.locationTypesEnabled.isEmpty()) {
			log.info("No location types enabled, so Background Service should not run...");
			return false;
		}

		return true;
	}

	/**
	 * Checks if the service is registered as a client the wake lock.
	 * 
	 * @return true, if the service is registered as a client the wake lock.
	 */
	public boolean isRegisteredToWakeLock() {
		return registeredToWakeLock;
	}

	/**
	 * Sets if the service is registered as a client the wake lock.
	 * 
	 * @param registeredToWakeLock the new registered to wake lock value
	 */
	public void setRegisteredToWakeLock(boolean registeredToWakeLock) {
		this.registeredToWakeLock = registeredToWakeLock;
	}
}
