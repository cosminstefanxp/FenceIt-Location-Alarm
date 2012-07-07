/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.util.List;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.fenceit.R;
import com.fenceit.alarm.locations.AlarmLocation;
import com.fenceit.alarm.locations.LocationType;
import com.fenceit.db.AlarmLocationBroker;
import com.fenceit.ui.adapters.LocationsAdapter;
import com.fenceit.ui.adapters.SingleChoiceAdapter;

/**
 * The Class LocationPanelActivity showing all the Locations that have been marked as favorite.
 */
public class LocationPanelActivity extends DefaultActivity implements OnItemClickListener, OnClickListener {

	private static final int REQ_CODE_ADD_LOCATION = 1;

	private static final int DIALOG_NEW_LOCATION = 1;

	private static final int REQ_CODE_EDIT_LOCATION = 2;

	/** The logger. */
	private static Logger log = Logger.getRootLogger();

	/** The list view. */
	ListView listView;

	/** The list adapter. */
	LocationsAdapter listAdapter;

	List<AlarmLocation> locations;

	private SingleChoiceAdapter<LocationType> locationsAdapter;

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreate(android.os.Bundle) */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.location_panel);

		// Fetch the locations from the database that are marked as favorite
		fetchLocations();

		// Prepare the listview and the adapter
		listView = (ListView) findViewById(R.id.locationPanel_locationList);
		listAdapter = new LocationsAdapter(this, locations);
		listView.setAdapter(listAdapter);
		registerForContextMenu(listView);
		listView.setOnItemClickListener(this);

		// Set up other controls
		findViewById(R.id.locationPanel_addButton).setOnClickListener(this);
		locationsAdapter = AlarmLocationBroker.getLocationTypesAdapter();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		log.info("ListView item click for editing location with id " + id);
		Intent editLocationActivityIntent = AlarmLocationBroker.getActivityIntent(getApplicationContext(), locations
				.get(position).getType());
		editLocationActivityIntent.putExtra("id", id);
		startActivityForResult(editLocationActivityIntent, REQ_CODE_EDIT_LOCATION);
	}

	private void fetchLocations() {
		// Fetch the locations from the database that are marked as favorite
		log.info("Fetching all favorite locations from database...");
		locations = AlarmLocationBroker.fetchAllLocation(getApplicationContext(), "favorite='"
				+ DefaultDAO.BOOLEAN_TRUE_VALUE + "'");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		// The button to add a new alarm was clicked
		case R.id.locationPanel_addButton:
			log.debug("Add location button clicked.");
			showDialog(DIALOG_NEW_LOCATION);
			break;
		}

	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onActivityResult(int, int, android.content.Intent) */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		log.debug("Activity Result received for request " + requestCode + " with result code: " + resultCode);

		// If a new location was added, fetch the locations and refresh the adapter
		// TODO: can be optimized to only fetch new things
		if ((requestCode == REQ_CODE_ADD_LOCATION || requestCode == REQ_CODE_EDIT_LOCATION) && resultCode == RESULT_OK) {
			log.debug("Refreshing locations...");
			fetchLocations();
			listAdapter.setLocations(locations);
		}

	}

	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		case DIALOG_NEW_LOCATION:
			// Create the dialog associated with creating a new type of location
			builder.setTitle("Location Type");
			builder.setItems(locationsAdapter.getNames(), new DialogInterface.OnClickListener() {

				// Process the selection
				public void onClick(DialogInterface dialog, int item) {
					log.debug("Creating new location of type: " + locationsAdapter.getValues()[item]);
					startActivityForNewLocation(locationsAdapter.getValues()[item]);
					dialog.dismiss();
				}
			});
			// Build the dialog
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Start activity for new location.
	 */
	private void startActivityForNewLocation(LocationType type) {
		Intent intent = AlarmLocationBroker.getActivityIntent(this, type);
		startActivityForResult(intent, REQ_CODE_ADD_LOCATION);
	}
}
