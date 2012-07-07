/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.locations.AbstractAlarmLocation;
import com.fenceit.alarm.locations.CellNetworkLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.CellContextData;
import com.fenceit.provider.CellDataProvider;

/**
 * The Class CellActivity.
 */
public class CellNetworkActivity extends AbstractLocationActivity implements OnClickListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(CellNetworkActivity.class);

	/** The Constant DIALOG_ENABLE_NETWORK. */
	private static final int DIALOG_ENABLE_NETWORK = 0;

	/** The data access object. */
	private DefaultDAO<CellNetworkLocation> dao = null;

	/** The location. */
	private CellNetworkLocation location;

	/** If it's a new entity. */
	private boolean newEntity;

	/** The save button. */
	private Button saveButton;

	/** The refresh button. */
	private ImageButton refreshButton;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cell_location);
		((TextView) findViewById(R.id.title_titleText)).setText("Edit Location");

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), CellNetworkLocation.class,
					CellNetworkLocation.tableName);

		// If it's a new activity
		if (savedInstanceState == null) {
			// Get the location from the database
			Bundle extras = getIntent().getExtras();
			Long locationID = (Long) (extras != null ? extras.get("id") : null);
			// See if the location is forced to be favorite
			isForcedFavorite = extras.getBoolean("forced");

			fetchLocation(locationID);
		}
		// If it's a restored instance
		else {
			// See if the location is forced to be favorite
			isForcedFavorite = savedInstanceState.getBoolean("forced");

			// Get the unsaved location from the saved instance
			location = (CellNetworkLocation) savedInstanceState.getSerializable("location");
			log.info("Restored saved instance of location: " + location);

		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.title_saveButton);
		saveButton.setOnClickListener(this);
		refreshButton = (ImageButton) findViewById(R.id.cell_refreshButton);
		refreshButton.setOnClickListener(this);

		// Fill data
		refreshActivity();
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onSaveInstanceState(android.os.Bundle) */
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putSerializable("location", location);
		outState.putBoolean("forced", isForcedFavorite);
	}

	/**
	 * Refresh the activity displayed views using the data from the location.
	 */
	private void refreshActivity() {
		// Refresh options of the AbstractAlarmLocation
		refreshAbstractLocationElements();

		// Location Section
		if (location.isComplete()) {
			((TextView) findViewById(R.id.cell_cellIdText)).setText(Integer.toString(location.getCellId()));
			((TextView) findViewById(R.id.cell_lacText)).setText(Integer.toString(location.getLac()));
			((TextView) findViewById(R.id.cell_mncText)).setText(Integer.toString(location.getMnc()));
			((TextView) findViewById(R.id.cell_mccText)).setText(Integer.toString(location.getMcc()));
		} else {
			((TextView) findViewById(R.id.cell_cellIdText)).setText("Click on the refresh button.");
			((TextView) findViewById(R.id.cell_lacText)).setText("-");
			((TextView) findViewById(R.id.cell_mncText)).setText("-");
			((TextView) findViewById(R.id.cell_mccText)).setText("-");
		}
	}

	/**
	 * Fetches the associated location from the database, or builds a new one, if no id was
	 * provided.
	 * 
	 * @param locationID the location id
	 */
	private void fetchLocation(Long locationID) {
		if (locationID != null) {
			log.info("Fetching CellLocation from database with id: " + locationID);
			dao.open();
			location = dao.fetch(locationID);
			dao.close();
			log.debug("Fetched location: " + location);
			if (location != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new CellLocation...");
		location = new CellNetworkLocation();
		newEntity = true;
		if(isForcedFavorite)
			location.setFavorite(true);
	}

	/**
	 * Stores the location in the database.
	 * 
	 * @return true, if successful
	 */
	private boolean storeLocation() {
		// Checks
		if (location == null) {
			log.error("No location to store in database.");
			return false;
		}
		// Store required data

		// Check if all data is all right
		if (!location.isComplete()) {
			log.error("Not all required fields are filled in");
			return false;
		}

		// Save the entity to the database
		log.info("Saving location in database...");
		dao.open();
		if (newEntity) {
			long id = dao.insert(location);
			if (id == -1)
				return false;
			log.info("Successfully saved new location with id: " + id);
			location.setId(id);
			newEntity = false;
		} else
			dao.update(location, location.getId());
		dao.close();

		return true;

	}

	/* (non-Javadoc)
	 * 
	 * @see android.view.View.OnClickListener#onClick(android.view.View) */
	@Override
	public void onClick(View v) {
		if (v == saveButton) {
			log.info("Save button clicked. Storing entity...");
			if (!storeLocation()) {
				Toast.makeText(this, "Not all fields are completed corectly. Please check all of them.",
						Toast.LENGTH_SHORT).show();
				return;
			}
			Intent intent = new Intent();
			intent.putExtra("id", location.getId());
			setResult(RESULT_OK, intent);
			finish();
			return;
		} else if (v == refreshButton) {
			log.info("Refreshing details regarding the Cell Tower currently connected to.");
			gatherContextInfo();
		}
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onCreateDialog(int) */
	@Override
	protected Dialog onCreateDialog(int id) {
		Dialog dialog;
		// Check if the AbstractLocationActivity can handle this type of dialog
		dialog = createAbstractLocationDialog(id);
		if (dialog != null)
			return dialog;

		// Try to handle this type of dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		switch (id) {
		// Create a dialog asking the user if he wants to go to the Wifi Settings
		case DIALOG_ENABLE_NETWORK:
			builder.setMessage(
					"The device does not seem to be connected to any mobile phone networks. Would you like to adjust the settings now?")
					.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_WIRELESS_SETTINGS));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/**
	 * Gather context info from the environment and fill in the location and the views.
	 */
	private void gatherContextInfo() {
		// Check for availability;
		if (!CellDataProvider.isCellNetworkConnected(this)) {
			Toast.makeText(this, "Cell network is not available", Toast.LENGTH_SHORT);
			showDialog(DIALOG_ENABLE_NETWORK);
			return;
		}

		CellContextData cellInfo = CellDataProvider.getCellContextData(this, false);
		log.info("Cell Network info: " + cellInfo);
		// Update the location
		location.setCellId(cellInfo.cellId);
		location.setLac(cellInfo.lac);
		location.setMnc(Integer.parseInt(cellInfo.networkOperator.substring(0, 3)));
		location.setMcc(Integer.parseInt(cellInfo.networkOperator.substring(3)));

		// Update the view
		refreshActivity();
	}

	@Override
	protected AbstractAlarmLocation getLocation() {
		return location;
	}

}
