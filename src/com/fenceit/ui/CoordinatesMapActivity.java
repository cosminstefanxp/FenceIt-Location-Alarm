/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.apache.log4j.Logger;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.fenceit.R;
import com.fenceit.ui.adapters.LocationMapInfoWindowAdapter;
import com.fenceit.ui.adapters.LocationMapInfoWindowAdapter_;
import com.fenceit.ui.adapters.LocationMapInfoWindowView;
import com.fenceit.ui.helpers.LocationMapSearchTask;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.googlecode.androidannotations.annotations.Bean;
import com.googlecode.androidannotations.annotations.EActivity;
import com.googlecode.androidannotations.annotations.NonConfigurationInstance;
import com.googlecode.androidannotations.annotations.OptionsItem;
import com.googlecode.androidannotations.annotations.OptionsMenu;

/**
 * The Class CoordinatesMapActivity.
 */
@EActivity
public class CoordinatesMapActivity extends SherlockFragmentActivity {

	/** The logger. */
	private static final Logger log = Logger.getLogger(CoordinatesMapActivity.class);

	/** The map. */
	private GoogleMap mMap;

	/** The selected location. */
	private LatLng selectedLocation;

	/** The marker. */
	private Marker mMarker;

	/** The search result marker. */
	private Marker searchMarker;

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
				mMap.setInfoWindowAdapter(LocationMapInfoWindowAdapter_.getInstance_(getBaseContext()));
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

	@OptionsItem(android.R.id.home)
	public void homeSelected() {
		returnSelectedLocation();
	}

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

	@Override
	public boolean onCreateOptionsMenu(final Menu menu) {
		MenuInflater menuInflater = getSupportMenuInflater();
		menuInflater.inflate(R.menu.activity_location_map, menu);
		SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				log.info("Searching for: " + query);
				searchTask.searchInBackground(query);
				collapseSearch(menu.findItem(R.id.menu_search));
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	private void collapseSearch(MenuItem searchItem) {
		// hide virtual keyboard
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(searchItem.getActionView().getWindowToken(), 0);
	}

	@NonConfigurationInstance
	@Bean
	protected LocationMapSearchTask searchTask;

	public void showSearchResult(Address result) {
		log.info("Found location: " + result);
		LatLng resultLocation = new LatLng(result.getLatitude(), result.getLongitude());
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(resultLocation, 12f));
		// prepare data
		String title = result.getMaxAddressLineIndex() > 0 ? result.getAddressLine(0) : "";
		String description = "";
		for (int i = 1; i <= result.getMaxAddressLineIndex(); i++)
			description += result.getAddressLine(i) + " ";
		// do something with result
		if (searchMarker != null) {
			searchMarker.setPosition(resultLocation);
			searchMarker.setTitle(title);
			searchMarker.setSnippet(description + "~Touch to use this location");
			searchMarker.showInfoWindow();
		} else {
			searchMarker = mMap.addMarker(new MarkerOptions().position(resultLocation).title(title)
					.snippet(description + "~Touch to use this location").draggable(true)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			searchMarker.showInfoWindow();
		}
	}

}
