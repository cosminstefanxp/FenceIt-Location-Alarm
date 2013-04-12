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
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
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

	/** Whether any changes were made. */
	private boolean mChangesMade = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location_map);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
					mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedLocation, 13f));
					mMarker = mMap.addMarker(new MarkerOptions()
							.position(selectedLocation)
							.draggable(true)
							.title(getString(R.string.location_geo_map_marker_title))
							.snippet(
									getString(R.string.location_geo_map_marker_snippet,
											selectedLocation.latitude, selectedLocation.longitude)));
				}

				// On long click on the map select a new location and create a
				// new marker
				mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

					@Override
					public void onMapLongClick(LatLng point) {
						selectedLocation = point;
						mChangesMade = true;
						if (mMarker != null) {
							mMarker.setSnippet(CoordinatesMapActivity.this.getString(
									R.string.location_geo_map_marker_snippet, selectedLocation.latitude,
									selectedLocation.longitude));
							mMarker.setPosition(selectedLocation);
							mMarker.hideInfoWindow();
						} else
							mMarker = mMap.addMarker(new MarkerOptions()
									.position(selectedLocation)
									.title(getString(R.string.location_geo_map_marker_title))
									.draggable(true)
									.snippet(
											CoordinatesMapActivity.this.getString(
													R.string.location_geo_map_marker_snippet,
													selectedLocation.latitude, selectedLocation.longitude)));
					}
				});

				// When the marker is dragged, update the selected location
				mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker marker) {
					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						selectedLocation = marker.getPosition();
						mChangesMade = true;
					}

					@Override
					public void onMarkerDrag(Marker marker) {
					}
				});
			}
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getSupportMenuInflater();
		inflater.inflate(R.menu.activity_location_map, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			returnSelectedLocation();
			return true;

		}
		return super.onOptionsItemSelected(item);
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
		Intent intent = new Intent();
		if (mChangesMade) {
			Toast.makeText(this, "Selected position: " + selectedLocation, Toast.LENGTH_SHORT).show();
			intent.putExtra("lat", selectedLocation.latitude);
			intent.putExtra("long", selectedLocation.longitude);
			setResult(RESULT_OK, intent);
		} else
			setResult(RESULT_CANCELED);
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
