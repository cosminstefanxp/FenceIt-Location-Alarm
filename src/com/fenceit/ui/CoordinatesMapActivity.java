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

import com.fenceit.R;
import com.fenceit.ui.helpers.LocationItemizedOverlay;
import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;
import com.google.android.maps.MyLocationOverlay;

/**
 * The Class CoordinatesMapActivity.
 */
public class CoordinatesMapActivity extends MapActivity {

	/** The logger. */
	private static final Logger log = Logger.getLogger(CoordinatesMapActivity.class);

	/** The map view. */
	private MyCustomMapView mapView;

	/** The my location overlay. */
	private MyLocationOverlay myLocationOverlay = null;

	/** The overlay for showing the location. */
	private LocationItemizedOverlay locationItemizedOverlay = null;

	/** The selected latitude. */
	private Double selectedLatitude;

	/** The selected longitude. */
	private Double selectedLongitude;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location_map);
		mapView = (MyCustomMapView) findViewById(R.id.coordinatesMap_mapView);
		mapView.setBuiltInZoomControls(true);

		// Get location coordinates
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			selectedLatitude = extras.getDouble("lat");
			selectedLongitude = extras.getDouble("long");
			log.info("Showing coordinates map with current position at " + selectedLatitude + "/"
					+ selectedLongitude);
		}

		// Add an on long press listener
		mapView.setOnLongpressListener(new MyCustomMapView.OnLongpressListener() {
			public void onLongpress(final MapView view, final GeoPoint longpressLocation) {
				runOnUiThread(new Runnable() {
					public void run() {
						locationSelected(longpressLocation);
					}
				});
			}
		});

		// Add overlay for current position
		myLocationOverlay = new MyLocationOverlay(this, mapView);
		mapView.getOverlays().add(myLocationOverlay);

		// Add overlay for location, if anything was selected before
		if (selectedLatitude != null) {

			locationItemizedOverlay = new LocationItemizedOverlay(getResources().getDrawable(
					R.drawable.ic_logo_old), selectedLatitude, selectedLongitude);
			Logger.getRootLogger().info("Initialized: " + locationItemizedOverlay);
			mapView.getOverlays().add(locationItemizedOverlay);
		}

		mapView.postInvalidate();

	}

	/**
	 * A new location was selected.
	 * 
	 * @param point the geographical coordinates
	 */
	public void locationSelected(GeoPoint point) {
		log.info("Long press at: " + point);
		Toast.makeText(this, "Selected point: " + point, Toast.LENGTH_SHORT).show();
		Intent intent = new Intent();
		intent.putExtra("lat", point.getLatitudeE6());
		intent.putExtra("long", point.getLongitudeE6());
		setResult(RESULT_OK, intent);
		finish();
	}

	@Override
	public void onResume() {
		super.onResume();
		myLocationOverlay.enableMyLocation();
		myLocationOverlay.runOnFirstFix(new Runnable() {
			public void run() {
				mapView.getController().setCenter(myLocationOverlay.getMyLocation());
			}
		});
	}

	@Override
	public void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	@Override
	protected boolean isLocationDisplayed() {
		return myLocationOverlay.isMyLocationEnabled();
	}

	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
