/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import org.androwrapee.db.DefaultDAO;

import android.content.Context;
import android.content.Intent;

import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.ui.WifiConnectedActivity;
import com.fenceit.ui.WifisDetectedActivity;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The AlarmLocationBroker is a class that is aware of the implemented activities corresponding to
 * location types. It is used to mediate communication between activities that use AlarmLocation and
 * the effective implementations of Activities corresponding to each Alarm Location type.
 */
public class AlarmLocationBroker {

	/**
	 * Gets the location types adapter.
	 * 
	 * @return the location types adapter
	 */
	public static SingleChoiceAdapter<LocationType> getLocationTypesAdapter() {
		return new SingleChoiceAdapter<LocationType>(new LocationType[] { LocationType.WifiConnectedLocation,
				LocationType.WifisDetectedLocation }, new CharSequence[] { "Based on the connected Wifi",
				"Based on the detected Wifis" });
	}

	/**
	 * Gets the activity intent.
	 * 
	 * @param context the context
	 * @param type the type of location
	 * @return the activity intent
	 */
	public static Intent getActivityIntent(Context context, LocationType type) {

		Intent intent = null;
		switch (type) {
		case WifiConnectedLocation:
			intent = new Intent(context, WifiConnectedActivity.class);
			break;
		case WifisDetectedLocation:
			intent = new Intent(context, WifisDetectedActivity.class);
			break;
		}
		return intent;
	}

	/**
	 * Fetches a location from the database, given the location id.
	 * 
	 * @param context the context
	 * @param id the id
	 * @return the alarm location, or null if no location found
	 */
	public static AlarmLocation fetchLocation(Context context, long id) {

		AlarmLocation location = null;
		// Fetch WifiConnectedLocation
		DefaultDAO<WifiConnectedLocation> daoWC = DatabaseManager.getDAOInstance(context, WifiConnectedLocation.class,
				WifiConnectedLocation.tableName);
		daoWC.open();
		location = daoWC.fetch(id);
		daoWC.close();
		if (location != null)
			return location;

		return location;

	}
}
