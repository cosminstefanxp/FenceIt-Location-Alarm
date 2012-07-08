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

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location_map);
		mapView = (MyCustomMapView) findViewById(R.id.coordinatesMap_mapView);

		mapView.setBuiltInZoomControls(true);

		// Add on longp ress listener
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
		mapView.postInvalidate();

	}

	/**
	 * a new location was selected.
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

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onResume()
	 */
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

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#onPause()
	 */
	@Override
	public void onPause() {
		super.onPause();
		myLocationOverlay.disableMyLocation();
	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isLocationDisplayed()
	 */
	@Override
	protected boolean isLocationDisplayed() {
		return myLocationOverlay.isMyLocationEnabled();

	}

	/* (non-Javadoc)
	 * @see com.google.android.maps.MapActivity#isRouteDisplayed()
	 */
	@Override
	protected boolean isRouteDisplayed() {
		return false;
	}

}
