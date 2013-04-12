/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.apache.log4j.Logger;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.widget.SearchView;
import com.actionbarsherlock.widget.SearchView.OnQueryTextListener;
import com.fenceit.R;
import com.fenceit.ui.adapters.LocationMapInfoWindowAdapter_;
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

	/** The search result location. */
	private LatLng searchResultLocation;

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
							updateSelectedMarker();
							mMarker.hideInfoWindow();
						} else
							createSelectedMarker();
					}
				});

				// When the marker is dragged, update the selected location
				mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {

					@Override
					public void onMarkerDragStart(Marker marker) {
					}

					@Override
					public void onMarkerDragEnd(Marker marker) {
						if (marker == mMarker) {
							selectedLocation = marker.getPosition();
							mChangesMade = true;
						}
					}

					@Override
					public void onMarkerDrag(Marker marker) {
					}
				});

				// When the search marker is clicked, set it as the mMarker
				mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {

					@Override
					public void onInfoWindowClick(Marker marker) {
						if (marker.equals(searchMarker)) {
							selectedLocation = marker.getPosition();
							if (mMarker == null)
								createSelectedMarker();
							else
								updateSelectedMarker();
							searchMarker.remove();
							searchMarker = null;
						}
					}
				});
			}
		}
	}

	@SuppressLint("InlinedApi")
	@OptionsItem(android.R.id.home)
	public void homeSelected() {
		returnSelectedLocation();
	}

	public void onBackPressed() {
		returnSelectedLocation();
	}

	public void updateSelectedMarker() {
		mMarker.setSnippet(CoordinatesMapActivity.this.getString(R.string.location_geo_map_marker_snippet,
				selectedLocation.latitude, selectedLocation.longitude));
		mMarker.setPosition(selectedLocation);
	}

	public void createSelectedMarker() {
		mMarker = mMap.addMarker(new MarkerOptions().title(getString(R.string.location_geo_map_marker_title))
				.draggable(true).position(selectedLocation));
		mMarker.setSnippet(CoordinatesMapActivity.this.getString(R.string.location_geo_map_marker_snippet,
				selectedLocation.latitude, selectedLocation.longitude));
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
		// Properly set up the search view
		final SearchView searchView = (SearchView) menu.findItem(R.id.menu_search).getActionView();
		searchView.setOnQueryTextListener(new OnQueryTextListener() {

			@Override
			public boolean onQueryTextSubmit(String query) {
				// Start the search, in background
				log.info("Searching for: " + query);
				searchTask.searchAddressInBackground(query);

				// Hide virtual keyboard
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
				return false;
			}

			@Override
			public boolean onQueryTextChange(String newText) {
				return false;
			}
		});
		return super.onCreateOptionsMenu(menu);
	}

	/** The search task executing the search in the background. */
	@NonConfigurationInstance
	@Bean
	protected LocationMapSearchTask searchTask;

	/**
	 * Shows the search result on the activity
	 * 
	 * @param result the result
	 */
	public void showSearchResult(Address result) {
		if (log.isInfoEnabled())
			log.info("Search result found: " + result);
		searchResultLocation = new LatLng(result.getLatitude(), result.getLongitude());
		mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(searchResultLocation, 12f));

		// prepare data
		String title = result.getMaxAddressLineIndex() >= 0 ? result.getAddressLine(0) : "";
		StringBuilder description = new StringBuilder();
		for (int i = 1; i <= result.getMaxAddressLineIndex(); i++)
			description.append(result.getAddressLine(i)).append(' ');

		// do something with result
		if (searchMarker != null) {
			searchMarker.setPosition(searchResultLocation);
			searchMarker.setTitle(title);
			searchMarker.setSnippet(description + "~Touch to use this location");
			searchMarker.showInfoWindow();
		} else {
			searchMarker = mMap.addMarker(new MarkerOptions().position(searchResultLocation).title(title)
					.snippet(description + "~Touch to use this location").draggable(true)
					.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
			searchMarker.showInfoWindow();
		}

	}
}
