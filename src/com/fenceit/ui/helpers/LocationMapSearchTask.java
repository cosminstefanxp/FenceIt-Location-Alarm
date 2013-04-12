/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.helpers;

import java.util.List;
import java.util.Locale;

import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import com.fenceit.ui.CoordinatesMapActivity;
import com.googlecode.androidannotations.annotations.Background;
import com.googlecode.androidannotations.annotations.EBean;
import com.googlecode.androidannotations.annotations.RootContext;
import com.googlecode.androidannotations.annotations.UiThread;

/**
 * Represents a task that is executed in the background and that searches for a
 * given address and returns the location.
 */
@EBean
public class LocationMapSearchTask {

	/** The callback activity. */
	@RootContext
	CoordinatesMapActivity activity;

	/**
	 * Search address in background.
	 * 
	 * @param toSearch the to search string
	 */
	@Background
	public void searchAddressInBackground(String toSearch) {
		try {
			// Build a geocoder
			Geocoder geocoder = new Geocoder(activity, Locale.UK);

			// Get the results
			List<Address> results = geocoder.getFromLocationName(toSearch, 1);
			if (results.size() == 0) {
				return;
			}
			Address address = results.get(0);

			// Update the UI
			updateUI(address);
		} catch (Exception e) {
			Log.e("", "Something went wrong: ", e);
		}
	}

	/**
	 * Manipulate the activity reference only from the UI thread
	 * 
	 * @param result
	 */
	@UiThread
	void updateUI(Address result) {
		activity.showSearchResult(result);
	}
}
