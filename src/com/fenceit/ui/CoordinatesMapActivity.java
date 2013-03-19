/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.apache.log4j.Logger;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.fenceit.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

/**
 * The Class CoordinatesMapActivity.
 */
public class CoordinatesMapActivity extends SherlockFragmentActivity {

	/** The logger. */
	private static final Logger log = Logger.getLogger(CoordinatesMapActivity.class);

	/** The map. */
	private GoogleMap mMap;

	/** The selected location. */
	private LatLng selectedLocation;

	/** The marker. */
	private Marker mMarker;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location_map);

		// Get location coordinates
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			if (extras.containsKey("lat"))
				selectedLocation = new LatLng(extras.getDouble("lat"), extras.getDouble("long"));
			log.info("Showing coordinates map with current position at " + selectedLocation);
		}

		// Set up the map
		setUpMapIfNeeded();

		Toast.makeText(this, "Long press on the map to select the location's position.", Toast.LENGTH_LONG)
				.show();
	}

	/**
	 * Sets up the map if it is possible to do so (i.e., the Google Play
	 * services APK is correctly installed) and the map has not already been
	 * instantiated. This will ensure that we only ever manipulate the map once
	 * when it {@link #mMap} is not null.
	 * <p>
	 * If it isn't installed {@link SupportMapFragment} (and
	 * {@link com.google.android.gms.maps.MapView MapView}) will show a prompt
	 * for the user to install/update the Google Play services APK on their
	 * device.
	 */
	private void setUpMapIfNeeded() {
		// Do a null check to confirm that we have not already instantiated the
		// map.
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(
					R.id.coordinates_map_fragment)).getMap();
			// Check if we were successful in obtaining the map.
			if (mMap != null) {
				// The Map is verified. It is now safe to manipulate the map:
				mMap.setMyLocationEnabled(true);
				if (selectedLocation != null) {
					// Set default zoom and location
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 15f));
					mMarker = mMap.addMarker(new MarkerOptions().position(selectedLocation));
				}

				mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

					@Override
					public void onMapLongClick(LatLng point) {
						selectedLocation = point;
						mMarker.setPosition(selectedLocation);
					}
				});

			}
		}
	}

	@Override
	public void onBackPressed() {
		returnSelectedLocation();
	}

	/**
	 * A new location was selected so the results are returned to the previous
	 * activity.
	 * 
	 */
	public void returnSelectedLocation() {
		Toast.makeText(this, "Selected position: " + selectedLocation, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.putExtra("lat", selectedLocation.latitude);
		intent.putExtra("long", selectedLocation.longitude);
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (mMap != null)
			mMap.setMyLocationEnabled(true);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (mMap != null)
			mMap.setMyLocationEnabled(false);
	}

}
