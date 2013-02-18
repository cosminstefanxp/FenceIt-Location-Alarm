/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.db;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.androwrapee.db.DefaultDAO;

import android.content.Context;
import android.content.Intent;

import com.fenceit.R;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.alarm.locations.WifiConnectedLocation;
import com.fenceit.alarm.locations.WifisDetectedLocation;
import com.fenceit.service.BackgroundService;
import com.fenceit.ui.CellNetworkActivity;
import com.fenceit.ui.CoordinatesActivity;
import com.fenceit.ui.WifiConnectedActivity;
import com.fenceit.ui.WifisDetectedActivity;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The AlarmLocationBroker is a class that is aware of the implemented activities corresponding to location
 * types. It is used to mediate communication between activities that use AlarmLocation and the effective
 * implementations of Activities corresponding to each Alarm Location type.
 * <p>
 * It is also used to handle interaction with the database.
 * </p>
 */
public class AlarmLocationBroker {

	/** The Constant locationTypes. */
	private static final LocationType[] locationTypes = new LocationType[] {
			LocationType.WifiConnectedLocation, LocationType.WifisDetectedLocation,
			LocationType.CellNetworkLocation, LocationType.GeoCoordinatesLocation };

	/**
	 * Gets the location types. The LocationType array returned is final and should not be modified.
	 * 
	 * @return the location types
	 */
	public static final LocationType[] getLocationTypes() {
		return locationTypes;
	}

	/**
	 * Gets the location types adapter.
	 *
	 * @param ctx the context
	 * @return the location types adapter
	 */
	public static SingleChoiceAdapter<LocationType> getLocationTypesAdapter(Context ctx) {
		return new SingleChoiceAdapter<LocationType>(null, getLocationTypes(), ctx.getResources()
				.getStringArray(R.array.location_types));
	}

	/**
	 * Gets the location types adapter, including the fake {@link LocationType#FavoriteExistingLocation}.
	 * 
	 * @param ctx the context
	 * @return the location types adapter
	 */
	public static SingleChoiceAdapter<LocationType> getLocationTypesAdapterWithFavorite(Context ctx) {
		return new SingleChoiceAdapter<LocationType>(null, new LocationType[] {
				LocationType.WifiConnectedLocation, LocationType.WifisDetectedLocation,
				LocationType.CellNetworkLocation, LocationType.GeoCoordinatesLocation,
				LocationType.FavoriteExistingLocation }, ctx.getResources().getStringArray(
				R.array.location_types_favorite));
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
	 * @param type the type
	 * @return the alarm location, or null if no location found
	 */
	public static AlarmLocation fetchLocation(Context context, long id, LocationType type) {

		AlarmLocation location = null;
		switch (type) {
		case CellNetworkLocation:
			DefaultDAO<CellNetworkLocation> daoC = DatabaseManager.getDAOInstance(context,
					CellNetworkLocation.class, CellNetworkLocation.tableName);
			daoC.open();
			location = daoC.fetch(id);
			daoC.close();
			break;
		case WifiConnectedLocation:
			DefaultDAO<WifiConnectedLocation> daoWC = DatabaseManager.getDAOInstance(context,
					WifiConnectedLocation.class, WifiConnectedLocation.tableName);
			daoWC.open();
			location = daoWC.fetch(id);
			daoWC.close();
			break;
		case WifisDetectedLocation:
			DefaultDAO<WifisDetectedLocation> daoWD = DatabaseManager.getDAOInstance(context,
					WifisDetectedLocation.class, WifisDetectedLocation.tableName);
			daoWD.open();
			location = daoWD.fetch(id);
			daoWD.close();
			break;
		case GeoCoordinatesLocation:
			DefaultDAO<CoordinatesLocation> daoCo = DatabaseManager.getDAOInstance(context,
					CoordinatesLocation.class, CoordinatesLocation.tableName);
			daoCo.open();
			location = daoCo.fetch(id);
			daoCo.close();
			break;
		}
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
		DefaultDAO<WifiConnectedLocation> daoWC = DatabaseManager.getDAOInstance(context,
				WifiConnectedLocation.class, WifiConnectedLocation.tableName);
		daoWC.open();
		List<WifiConnectedLocation> locationsWC = daoWC.fetchAll(where);
		daoWC.close();
		locations.addAll(locationsWC);

		// Fetch WifisDetectedLocations
		DefaultDAO<WifisDetectedLocation> daoWD = DatabaseManager.getDAOInstance(context,
				WifisDetectedLocation.class, WifisDetectedLocation.tableName);
		daoWD.open();
		List<WifisDetectedLocation> locationsWD = daoWD.fetchAll(where);
		daoWD.close();
		locations.addAll(locationsWD);

		// Fetch CellLocations
		DefaultDAO<CellNetworkLocation> daoC = DatabaseManager.getDAOInstance(context,
				CellNetworkLocation.class, CellNetworkLocation.tableName);
		daoC.open();
		List<CellNetworkLocation> locationsC = daoC.fetchAll(where);
		daoC.close();
		locations.addAll(locationsC);

		// Fetch CoordinatesLocations
		DefaultDAO<CoordinatesLocation> daoCo = DatabaseManager.getDAOInstance(context,
				CoordinatesLocation.class, CoordinatesLocation.tableName);
		daoCo.open();
		List<CoordinatesLocation> locationsCo = daoCo.fetchAll(where);
		daoCo.close();
		locations.addAll(locationsCo);

		return locations;
	}

	/**
	 * Start the background service from an activity and issues a trigger check for a particular location
	 * type.
	 * <p>
	 * The purpose is to notify the background service, if running (otherwise start it), that some triggers
	 * with this particular location type has been modified and that at a trigger check should be scheduled
	 * soon.
	 * </p>
	 * 
	 * @param context the context
	 * @param type the type of location, or null if the service should schedule a check for all location types
	 */
	public static void startServiceFromActivity(Context context, LocationType type) {
		Intent intent = new Intent(context, BackgroundService.class);
		if (type == null)
			intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
					BackgroundService.SERVICE_EVENT_FORCE_RECHECK);
		else
			switch (type) {
			case WifiConnectedLocation:
				intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
						BackgroundService.SERVICE_EVENT_WIFI_CONNECTED);
				break;
			case WifisDetectedLocation:
				intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
						BackgroundService.SERVICE_EVENT_WIFIS_DETECTED);
				break;
			case GeoCoordinatesLocation:
				intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
						BackgroundService.SERVICE_EVENT_GEO_COORDINATES);
				break;
			case CellNetworkLocation:
				intent.putExtra(BackgroundService.SERVICE_EVENT_FIELD_NAME,
						BackgroundService.SERVICE_EVENT_CELL_NETWORK);
				break;
			}
		context.startService(intent);
	}
}
