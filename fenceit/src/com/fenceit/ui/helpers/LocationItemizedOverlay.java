/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui.helpers;

import android.graphics.drawable.Drawable;

import com.fenceit.alarm.locations.CoordinatesLocation;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.ItemizedOverlay;
import com.google.android.maps.OverlayItem;

/**
 * The LocationItemizedOverlay is used to display a marker for a
 * {@link CoordinatesLocation} on a map.
 */
public class LocationItemizedOverlay extends ItemizedOverlay<OverlayItem> {

	/** The selected latitude. */
	private double selectedLatitude;

	/** The selected longitude. */
	private double selectedLongitude;

	/**
	 * Instantiates a new location itemized overlay.
	 * 
	 * @param defaultMarker the default marker
	 * @param location the location
	 */
	public LocationItemizedOverlay(Drawable defaultMarker, Double selectedLatitude, Double selectedLongitude) {
		super(defaultMarker);
		this.selectedLatitude = selectedLatitude;
		this.selectedLongitude = selectedLongitude;
	}

	@Override
	public String toString() {
		return "LocationItemizedOverlay [selectedLatitude=" + selectedLatitude + ", selectedLongitude="
				+ selectedLongitude + "]";
	}

	@Override
	protected OverlayItem createItem(int index) {
		GeoPoint point = new GeoPoint((int) (selectedLatitude * 1000000), (int) (selectedLongitude * 1000000));
		return new OverlayItem(point, "Selected point", String.format("Latitude: %f\nLongitude: %f",
				selectedLatitude, selectedLongitude));
	}

	@Override
	public int size() {
		return 1;
	}

}
