/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.provider;

import java.util.HashSet;

import org.apache.log4j.Logger;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

/**
 * The Class LocationDataProvider.
 */
public class LocationDataProvider implements LocationListener {

	/** The location manager. */
	private LocationManager locationManager;

	/** The best location obtained so far. */
	private Location bestLocation;

	/** The Constant TWO_MINUTES. */
	private static final int TWO_MINUTES = 1000 * 60 * 2;

	/** The listeners. */
	private HashSet<CoordinatesLocationDataListener> listeners = new HashSet<CoordinatesLocationDataListener>();

	/** The logger. */
	private static Logger log = Logger.getLogger(LocationDataProvider.class);

	/**
	 * Adds a new coordinates location data listener.
	 * 
	 * @param listener the listener
	 */
	public void addCoordinatesLocationDataListener(CoordinatesLocationDataListener listener, Context context) {
		if (listeners.size() == 0)
			startLocating(context);
		listeners.add(listener);
	}

	/**
	 * Removes a coordinates location data listener.
	 * 
	 * @param listener the listener
	 */
	public void removeCoordinatesLocationDataListener(CoordinatesLocationDataListener listener) {
		listeners.remove(listener);
		if (listeners.size() == 0)
			stopLocating();
	}

	/**
	 * Register this provider for receiving location updates.
	 * 
	 * @param context the context
	 */
	private void startLocating(Context context) {
		log.info("Registering for location updates...");
		// Acquire a reference to the system Location Manager
		locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

		// Register the listener with the Location Manager to receive location updates
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, this);
		locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, this);
	}

	/**
	 * Unregister this provider from receiving location updates.
	 * 
	 * @param context the context
	 */
	private void stopLocating() {
		log.info("Unregistering from location updates...");
		locationManager.removeUpdates(this);
		locationManager = null;
	}

	/**
	 * Determines whether one Location reading is better than the current Location fix.
	 * 
	 * @param location The new Location that you want to evaluate
	 * @param currentBestLocation The current Location fix, to which you want to compare the new one
	 * @return true, if is better location
	 */
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
		// A new location is always better than no location
		if (currentBestLocation == null) {
			return true;
		}

		// Check whether the new location fix is newer or older
		long timeDelta = location.getTime() - currentBestLocation.getTime();
		boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
		boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
		boolean isNewer = timeDelta > 0;

		// If it's been more than two minutes since the current location, use the new location
		// because the user has likely moved
		if (isSignificantlyNewer) {
			return true;
			// If the new location is more than two minutes older, it must be worse
		} else if (isSignificantlyOlder) {
			return false;
		}

		// Check whether the new location fix is more or less accurate
		int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
		boolean isLessAccurate = accuracyDelta > 0;
		boolean isMoreAccurate = accuracyDelta < 0;
		boolean isSignificantlyLessAccurate = accuracyDelta > 200;

		// Check if the old and new location are from the same provider
		boolean isFromSameProvider = isSameProvider(location.getProvider(), currentBestLocation.getProvider());

		// Determine location quality using a combination of timeliness and accuracy
		if (isMoreAccurate) {
			return true;
		} else if (isNewer && !isLessAccurate) {
			return true;
		} else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
			return true;
		}
		return false;
	}

	/**
	 * Checks whether two providers are the same.
	 * 
	 * @param provider1 the provider1
	 * @param provider2 the provider2
	 * @return true, if is same provider
	 */
	private boolean isSameProvider(String provider1, String provider2) {
		if (provider1 == null) {
			return provider2 == null;
		}
		return provider1.equals(provider2);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onLocationChanged(android.location.Location) */
	@Override
	public void onLocationChanged(Location location) {
		if (log.isDebugEnabled())
			log.debug("New location from LocationManager: " + location);
		if (isBetterLocation(location, bestLocation)) {
			log.debug("New location is better than previous location. Notifying listeners...");
			bestLocation = location;
			for (CoordinatesLocationDataListener l : listeners)
				l.onLocationUpdate(bestLocation);
		}

	}

	/* (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderDisabled(java.lang.String) */
	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onProviderEnabled(java.lang.String) */
	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * 
	 * @see android.location.LocationListener#onStatusChanged(java.lang.String, int,
	 * android.os.Bundle) */
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
}
