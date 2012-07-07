/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.ArrayList;
import java.util.List;

import org.androwrapee.db.DefaultDAO;

import android.content.Context;
import android.content.Intent;

import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.ui.CellNetworkActivity;
import com.fenceit.ui.CoordinatesActivity;
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
				LocationType.WifisDetectedLocation, LocationType.CellNetworkLocation,
				LocationType.GeoCoordinatesLocation }, new CharSequence[] { "Based on the connected Wifi",
				"Based on the detected Wifis", "Based on Cell Network", "Based on Geographical Coordinates" });
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
		case CellNetworkLocation:
			intent = new Intent(context, CellNetworkActivity.class);
			break;
		case GeoCoordinatesLocation:
			intent = new Intent(context, CoordinatesActivity.class);
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

		// Fetch WifisDetectedLocation
		DefaultDAO<WifisDetectedLocation> daoWD = DatabaseManager.getDAOInstance(context, WifisDetectedLocation.class,
				WifisDetectedLocation.tableName);
		daoWD.open();
		location = daoWD.fetch(id);
		daoWD.close();
		if (location != null)
			return location;

		// Fetch CellLocation
		DefaultDAO<CellNetworkLocation> daoC = DatabaseManager.getDAOInstance(context, CellNetworkLocation.class,
				CellNetworkLocation.tableName);
		daoC.open();
		location = daoC.fetch(id);
		daoC.close();
		if (location != null)
			return location;

		// Fetch CoordinatesLocation
		DefaultDAO<CoordinatesLocation> daoCo = DatabaseManager.getDAOInstance(context, CoordinatesLocation.class,
				CoordinatesLocation.tableName);
		daoCo.open();
		location = daoCo.fetch(id);
		daoCo.close();
		if (location != null)
			return location;

		return location;
	}

	/**
	 * Fetches all the locations that match a particular where clause from the database.
	 * 
	 * @param context the context
	 * @param where the where clause
	 * @return the list of locations
	 */
	public static List<AlarmLocation> fetchAllLocation(Context context, String where) {
		List<AlarmLocation> locations = new ArrayList<AlarmLocation>();

		// Fetch WifiConnectedLocations
		DefaultDAO<WifiConnectedLocation> daoWC = DatabaseManager.getDAOInstance(context, WifiConnectedLocation.class,
				WifiConnectedLocation.tableName);
		daoWC.open();
		List<WifiConnectedLocation> locationsWC = daoWC.fetchAll(where);
		daoWC.close();
		locations.addAll(locationsWC);

		// Fetch WifisDetectedLocations
		DefaultDAO<WifisDetectedLocation> daoWD = DatabaseManager.getDAOInstance(context, WifisDetectedLocation.class,
				WifisDetectedLocation.tableName);
		daoWD.open();
		List<WifisDetectedLocation> locationsWD = daoWD.fetchAll(where);
		daoWD.close();
		locations.addAll(locationsWD);

		// Fetch CellLocations
		DefaultDAO<CellNetworkLocation> daoC = DatabaseManager.getDAOInstance(context, CellNetworkLocation.class,
				CellNetworkLocation.tableName);
		daoC.open();
		List<CellNetworkLocation> locationsC = daoC.fetchAll(where);
		daoC.close();
		locations.addAll(locationsC);

		// Fetch CoordinatesLocations
		DefaultDAO<CoordinatesLocation> daoCo = DatabaseManager.getDAOInstance(context, CoordinatesLocation.class,
				CoordinatesLocation.tableName);
		daoCo.open();
		List<CoordinatesLocation> locationsCo = daoCo.fetchAll(where);
		daoCo.close();
		locations.addAll(locationsCo);

		return locations;
	}
}
