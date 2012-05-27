/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.adapters;

import android.content.Context;
import android.content.Intent;

import com.fenceit.alarm.locations.LocationType;
import com.fenceit.ui.WifiConnectedActivity;

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
		return new SingleChoiceAdapter<LocationType>(new LocationType[] { LocationType.CoordinatesLocation },
				new CharSequence[] { "Based on coordinates" });
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
		case CoordinatesLocation:
			intent = new Intent(context, WifiConnectedActivity.class);
			break;
		}
		return intent;
	}
}
