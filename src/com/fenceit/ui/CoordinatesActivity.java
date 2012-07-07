/*
 * Fence It
 *
 * Stefan-Dobrin Cosmin
 * Copyright 2012
 */
package com.fenceit.ui;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Date;

import org.androwrapee.db.DefaultDAO;
import org.apache.log4j.Logger;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fenceit.R;
import com.fenceit.alarm.locations.AbstractAlarmLocation;
import com.fenceit.alarm.locations.CoordinatesLocation;
import com.fenceit.db.DatabaseManager;
import com.fenceit.provider.CoordinatesDataProvider;
import com.fenceit.provider.CoordinatesLocationDataListener;
import com.fenceit.ui.picker.NumberPickerDialog;
import com.fenceit.ui.picker.NumberPickerDialog.OnNumberSetListener;

/**
 * The Class CoordinatesActivity used for setting up Locations based on Coordinates.
 */
public class CoordinatesActivity extends AbstractLocationActivity implements OnClickListener,
		CoordinatesLocationDataListener {

	/** The logger. */
	private static final Logger log = Logger.getLogger(CoordinatesActivity.class);

	/** The Constant DIALOG_ENABLE_NETWORK. */
	private static final int DIALOG_ENABLE_LOCALIZATION = 0;

	/** The Constant DIALOG_ACTIVATION_DISTANCE. */
	private static final int DIALOG_ACTIVATION_DISTANCE = 1;

	/** The data access object. */
	private DefaultDAO<CoordinatesLocation> dao = null;

	/** The location. */
	private CoordinatesLocation location;

	/** If it's a new entity. */
	private boolean newEntity;

	/** The save button. */
	private Button saveButton;

	/** The refresh button. */
	private ImageButton refreshButton;

	/** The progress bar. */
	private ProgressBar progressBar;

	/** The Constant used for number format. */
	private static final NumberFormat nf = new DecimalFormat("##.########");

	/** The location provider. */
	private CoordinatesDataProvider locationProvider;

	/**
	 * Called when the activity is first created.
	 * 
	 * @param savedInstanceState the saved instance state
	 */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.coordinates_location);
		((TextView) findViewById(R.id.title_titleText)).setText("Edit Location");

		// Prepare database connection
		if (dao == null)
			dao = DatabaseManager.getDAOInstance(getApplicationContext(), CoordinatesLocation.class,
					CoordinatesLocation.tableName);

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
			location = (CoordinatesLocation) savedInstanceState.getSerializable("location");
			log.info("Restored saved instance of location: " + location);
		}

		// Buttons and others
		saveButton = (Button) findViewById(R.id.title_saveButton);
		saveButton.setOnClickListener(this);
		refreshButton = (ImageButton) findViewById(R.id.coordinates_refreshButton);
		refreshButton.setOnClickListener(this);
		progressBar = (ProgressBar) findViewById(R.id.coordinates_progressBar);

		findViewById(R.id.coordinates_mapSection).setOnClickListener(this);
		findViewById(R.id.coordinates_radiusSection).setOnClickListener(this);

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
			((TextView) findViewById(R.id.coordinates_latText)).setText(nf.format(location.getLatitude()));
			((TextView) findViewById(R.id.coordinates_longText)).setText(nf.format(location.getLongitude()));
			if (location.getExtra() != null)
				((TextView) findViewById(R.id.coordinates_extraText)).setText(location.getExtra());
			else
				((TextView) findViewById(R.id.coordinates_extraText)).setText("-");
		} else {
			((TextView) findViewById(R.id.coordinates_latText)).setText("Click on the refresh button.");
			((TextView) findViewById(R.id.coordinates_longText)).setText("-");
			((TextView) findViewById(R.id.coordinates_extraText)).setText("-");
		}
		// Settings section
		((TextView) findViewById(R.id.coordinates_radiusText)).setText(location.getActivationDistance() + " m");
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onDestroy() */
	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationProvider != null) {
			locationProvider.removeCoordinatesLocationDataListener(this);
			locationProvider = null;
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
			log.info("Fetching CoordinatesLocation from database with id: " + locationID);
			dao.open();
			location = dao.fetch(locationID);
			dao.close();
			log.debug("Fetched location: " + location);
			if (location != null)
				return;
		}
		// No entity in database... creating a new one
		log.info("Creating new CellLocation...");
		location = new CoordinatesLocation();
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
		switch (v.getId()) {
		case R.id.title_saveButton:
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
		case R.id.coordinates_radiusSection:
			log.debug("Updating radius...");
			showDialog(DIALOG_ACTIVATION_DISTANCE);
			break;
		case R.id.coordinates_refreshButton:
			log.info("Refreshing details regarding the current loction of the device.");
			if (locationProvider == null)
				locationProvider = new CoordinatesDataProvider();
			locationProvider.addCoordinatesLocationDataListener(this, this.getApplicationContext());
			this.progressBar.setProgress(10);
			this.progressBar.setVisibility(View.VISIBLE);
			this.refreshButton.setVisibility(View.INVISIBLE);
			break;
		}
	}

	// TODO: Cool:
	// http://stackoverflow.com/questions/7709030/get-gps-location-in-a-broadcast-receiver-or-service-to-broadcast-receiver-data-t/7709140#7709140

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
		// Create a dialog asking the user if he wants to go to the Location Settings
		case DIALOG_ENABLE_LOCALIZATION:
			builder.setMessage(
					"The device does not seem to have any localization interfaces enabled. Would you like to adjust the settings now?")
					.setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
						}
					}).setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							dialog.cancel();
						}
					});
			dialog = builder.create();
			break;
		case DIALOG_ACTIVATION_DISTANCE:
			// Create the dialog associated with the activation distance for the location
			NumberPickerDialog dialogT = new NumberPickerDialog(this, -1, 0);
			dialogT.setRange(300, 2000);
			dialogT.setCurrent(location.getActivationDistance());
			dialogT.setTitle("It is activated at (m)");
			dialogT.setOnNumberSetListener(new OnNumberSetListener() {

				@Override
				public void onNumberSet(int selectedNumber) {
					location.setActivationDistance(selectedNumber);
					((TextView) findViewById(R.id.coordinates_radiusText)).setText(selectedNumber + " m");
				}
			});
			dialog = dialogT;
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	/* Called when a new location is available from the LocationProvider
	 * 
	 * (non-Javadoc)
	 * 
	 * @see
	 * com.fenceit.provider.CoordinatesLocationDataListener#onLocationUpdate(android.location.Location
	 * ) */
	@Override
	public void onLocationUpdate(Location location) {
		if (log.isInfoEnabled())
			log.info("Location updated in Activity: " + location);

		// Update the location
		this.location.setLatitude(location.getLatitude());
		this.location.setLongitude(location.getLongitude());
		this.location.setExtra(location.getProvider() + "/" + location.getAccuracy() + "/"
				+ new Date(location.getTime()).toString());

		// Update the UI
		refreshActivity();
		this.progressBar.setVisibility(View.INVISIBLE);
		this.refreshButton.setVisibility(View.VISIBLE);
	}

	/* (non-Javadoc)
	 * 
	 * @see android.app.Activity#onStop() */
	@Override
	protected void onStop() {
		super.onStop();
		if (locationProvider != null) {
			locationProvider.removeCoordinatesLocationDataListener(this);
			locationProvider = null;
		}

	}

	/* (non-Javadoc)
	 * 
	 * @see com.fenceit.ui.AbstractLocationActivity#getLocation() */
	@Override
	protected AbstractAlarmLocation getLocation() {
		return location;
	}

}
